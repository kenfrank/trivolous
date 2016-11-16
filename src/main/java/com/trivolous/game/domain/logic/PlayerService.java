package com.trivolous.game.domain.logic;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trivolous.game.dao.GameDao;
import com.trivolous.game.dao.InviteDao;
import com.trivolous.game.dao.MemberDao;
import com.trivolous.game.dao.PlayerDao;
import com.trivolous.game.dao.QuestionDao;
import com.trivolous.game.data.InviteData;
import com.trivolous.game.data.MemberData;
import com.trivolous.game.data.PlayerData;
import com.trivolous.game.data.QuestionData;
import com.trivolous.game.data.RoundData;
import com.trivolous.game.data.TurnForm;
import com.trivolous.game.domain.Game;
import com.trivolous.game.domain.Game.STATUS;
import com.trivolous.game.domain.GameComment;
import com.trivolous.game.domain.Invite;
import com.trivolous.game.domain.Member;
import com.trivolous.game.domain.Player;
import com.trivolous.game.domain.Player.Status;
import com.trivolous.game.domain.Question;
import com.trivolous.game.domain.QueuedQuestion;
import com.trivolous.game.domain.Round;
import com.trivolous.game.domain.Turn;
import com.trivolous.game.notify.NotificationSenderInterface;

@Service
@Transactional
public class PlayerService  {
	final int MAX_PLAYERS = 10;
	protected final Log logger = LogFactory.getLog(getClass());
	@Autowired
	GameDao gameDao;
	@Autowired
 	MemberDao memberDao;
	@Autowired
 	QuestionDao questionDao;
	@Autowired
	PlayerDao playerDao;
	@Autowired
	InviteDao inviteDao;
	@Autowired
	NotificationSenderInterface notificationSender;
	@Autowired
	AnswerSession answerSession;
	@Autowired
	private ImageService imageService;
	@Autowired
	private NewGameService gameService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private CommentService commentService;
	
	private GameState getGameState(Game game)
	{
		return new GameState(game, new GameStateActionHandlerImpl(notificationSender, gameService));
	}
		
	private void generatePlayerName(Player player)
	{
		String name = player.getMember().getFirstName();
		boolean nameUnique = false;
		int i = 1;
		while (!nameUnique)
		{
			nameUnique = true;
			for (Player p : player.getGame().getActivePlayers())
			{
				if (name.equals(p.getName()) && (p.getId() != player.getId()))
				{
					logger.debug("Name collision '" + name + "' ("+player.getMember().getEmail()+") and '" 
							+ p.getName() + "' (" + p.getMember().getEmail()+")");
					nameUnique = false;
					name = player.getMember().getFirstName() + i;
					i += 1;
					break;
				}
			}
		}
		player.setName(name);
	}		
	public List<PlayerData> findPlayersByMember(long memberId)
	{
		Member member = memberDao.findById(memberId);
		// TODO -- search on memberId instead...
		List<Player> players = playerDao.findByMember(member);
		ArrayList<PlayerData> playersData = new ArrayList<PlayerData>();
		for (Player p : players) {
			playersData.add(new PlayerData(p));
		}
		return playersData;
	}
	public PlayerData findPlayerById(long id)
	{
		Player p = playerDao.findById(id);
		return new PlayerData(p); 
	}
	
	private Player lookupPlayer(long id)
	{
		Player p = playerDao.findById(id); 
		if (p==null) {
			logger.error("Player id not found. id="+id);
			// TODO -- do this better.  Make custom exceptions for service maybe?
			// throw new NullPointerException("Game id not found");
			throw new TrivolousError("Player id not found " + id);
		}		
		return p;
	}	
	

	
	private Integer findNextPlayerOrder(Game game)
	{
		if (game.getPlayers() == null) return 1;
		Integer max = 0;
		for (Player p : game.getPlayers()) {
			if (p.getOrder() > max) max = p.getOrder();
		}
		return max+1;
	}

	public static enum InviteStatus {
		SUCCESS,
		BAD_GAME,
		BAD_MEMBER,
		BAD_STATE,
		ALREADY_MEMBER
	};
	
	// TODO invite -- should invite, then register.  never externally add a player.  make this all internal.
	// should happen when game created and when players register.
	private void addPlayer(long gameId, long memberId)
	{
		Game game = gameService.lookupGame(gameId);
		Member member = memberDao.findById(memberId);
		logger.debug("addPlayer game="+game.getName()+ " member="+ member.getId());
		
		// check if member already in game, if so do not add (throw error too?)
		for (Player p : game.getPlayers()) {
			if (p.getMember().getId() == member.getId()) {
				logger.error("Member is already a player");
				return;
			}
		}
		Player player = new Player();
		player.setGame(game);
		player.setMember(member);
		player.setOrder(findNextPlayerOrder(game));
		playerDao.create(player);
		

	}		
	
	public void removeInvite(long inviteId)
	{
//		else
//			if (!player.getHasRegistered()) {
//				// check if player was last to have not registered, if so, start game.
//				player.setIsActive(false);
//				Game game = player.getGame();
//				getGameState(game).eventInviteResponse();
//			}		
		// TODO invite
	}
	
	// returns TRUE if removed caused game to start
	public boolean removePlayer(long playerId)
	{
		Player player = lookupPlayer(playerId);

		logger.debug("remove player gameID="+player.getGame().getId()+
				" member="+player.getId());
		
		if (player.getGame().getStatus() == Game.STATUS.CREATING ||
			player.getGame().getStatus() == Game.STATUS.INVITING) {
			// if player hasnt responded yet, just remove it.
			// TODO -- could be some nasty windows here where player is registering when this happens.
			deletePlayer(player);
		} 
		else {
			player.setIsActive(false);
			getGameState(player.getGame()).eventPlayerRemoved();
		}
		return false;
	}
		
	
	
	private void deletePlayer(Player p) 
	{
		// TODO (high) -- maybe shouldnt allow the deletion of an active player, or a player
		// that has turns.  this will make the history/score board messed up.  for instance
		// 'some got it right' may show no player got it right.  Could check for all this
		// stuff and only delete in that case.  for now leave it up to admin (me).
		logger.info("Deleting player ; '" + p.getName() +"'");
		playerDao.remove(p);
	}
	public PlayerData findPlayer(long gameId, long memberId)
	{
		Game game = gameService.lookupGame(gameId);
		Player player = null;
		logger.debug("findPlayer gameID="+gameId+" mid="+memberId);
		if (game != null)
		{
			List<Player> players = game.getPlayers();
			// TODO (low) -- think about better way to do this.  Maybe make players a map? or add this to playerDao
			for (Player p : players)
			{
				if (p.getMember().getId() == memberId)
				{
					player = p;
					logger.debug("findPlayer found="+player.getName()+" game round="+ player.getGame().getRoundNumber());
					return new PlayerData(player);
				}
			}
		}
		logger.warn("findPlayer NOT found");
		return null;
	}

	public boolean verifyPlayer(long gameId, long memberId)
	{
		return this.findPlayer(gameId, memberId) != null;
	}
		
	
	// returns question and time left in session (maybe called more than once for reloads)
	public QuestionData startTimedAnswerSession(long playerId, int bet)
	{
		Player player = lookupPlayer(playerId);
		logger.debug("StartTimedAnswerSession = " + player.getName());
		Game game = player.getGame();

		Question question = player.getGame().getCurrentRound().getQuestion();

		long timeout = question.getTimeout();
		long timeLeft = timeout;
		
		// first time starting session, create a turn object.
		// hasanswered() returns true if turn object exists for this round,
		// even though answer may not have been submitted.
		Player.Status status = player.getStatus();
		if (status == Player.Status.NEEDS_TO_ANSWER)
		{
			// start timer here to timeout session if submit is not sent.
			// also make sure that submit handles being called after session was
			// already timedout out... just to be safe.
			logger.debug("Start answer session for " + player.getName());
			Turn turn = new Turn();
			turn.setIsAsker(false);
			turn.setPlayer(player);
			turn.setCreated(new Date());
			turn.setRound(game.getCurrentRound());
			turn.setBet(bet);
			answerSession.start(player, question.getTimeout());
		}
		else if (status == Player.Status.ANSWERING)
		{
			logger.debug("Re-start answer session for " + player.getName());
			// reload (because of refresh), so adjust time.
			Turn turn = player.getLastTurn();
			long timeElapsed = new Date().getTime() - 
				turn.getCreated().getTime();
			// round to closest second to make sure refreshed dont make time
			// go slower.
			timeLeft -= (timeElapsed+500)/1000;
			if (timeLeft < 0) timeLeft = 0;
		}
		else
		{
			// Not sure why this would ever happen... but just to be safe.
			logger.error("startTimedAnswerSession - Unexpected state " + player.getName());
			timeLeft = 0;
		}
		QuestionData questionData = new QuestionData(question);
		questionData.setSecondsLeft((int) timeLeft);
		return questionData;
	}

	
	// TODO (med) -- make sure to handle concurrency here (called from AnswerTimeout thread too). 
	// Could make message since email is sent too and this should be done 
	// in another dthread anyway.
	public boolean submitAnswer(long playerId, TurnForm tf)
	{
		Player player = lookupPlayer(playerId);
		logger.debug("Submit answer from pid=" + player.getId());
		// need to check answer before submitting as round could end after that.
		boolean answeredCorrectly = player.getGame().getQuestion().getAnswer().equals(tf.getAnswer()); 
		answerSession.end(player.getId(), tf.getSecondsLeft(), tf.getAnswer());
		return  answeredCorrectly;
	}

	// TODO (med) -- this shouldnt be public.  Fix this up later.
	// I am thinking that AnswerTimeout should be AnswerSession and cancel can
	// be replaced with submit.  The only problem is I dont think the all answered
	// should be handled there.  Wait to see how this whole things looks like later
	// and then refactor.  Note a session object could also handle returning time
	// left and handling reload.
	@Transactional
	public Turn submitAnswerInternal(long playerId, long secondsLeft, int answer)
	{
		Player player = playerDao.findById(playerId);
		
		Game game = player.getGame();
		Turn turn = player.getLastTurn();
		logger.info("Submit internal answer from " + player.getName() + " turnid=" + turn.getId());
		
		// NOTE: Got NPE on this line below when playing with Rick because I answered at same time and round was 
		// ended because on check for players that need to answer not answering.  Could make sure Question is not
		// null, but this should never happen again as logic was fixed to check for answering too.
		if (turn.getChoice() != -1)  {
			// whoa.. something aint right.
			// !!! NOTE: had this happen to me on 8/3/15.  Answered once got this, game page showed I still needed to answer,
			// answered again, it was fine.
			logger.error("received answer for already answered question playerid="+playerId+" turn id="+turn.getId());
		}
		turn.setTimeToAnswer((int)(game.getQuestion().getTimeout() - secondsLeft));
		if (turn.getTimeToAnswer() <= 0) {
			answer = Turn.TIMEOUT;
			turn.setTimeToAnswer(0);
		}
		if (answer < 0) {
			logger.error("Answer < 0, setting to TIMEOUT.  = " + answer);
			answer = Turn.TIMEOUT;
		}
		turn.setChoice(answer);
		
		getGameState(game).eventAnswer();
		// note: turn may not be the same as lastTurn if question was queued and player was asker.
		return turn;  
	}	

	public RoundData getLastRound(long playerId)
	{
		Player player = lookupPlayer(playerId);
		Turn turn = player.getLastTurn();
		if (turn == null) return null;
		Round round = turn.getRound();
		List<GameComment> comments = commentService.findAllRoundComments(
				player.getGame().getId(), round.getRoundNumber());
		return new RoundData(round, comments);
	}	
	
	public void queueQuestion(long playerId, int id)
	{
		Player player = lookupPlayer(playerId);
		// always queue it, even though it might get unqueued immediately.
		logger.warn("Player " + player.getId() + " submitting question");

		Question q = questionDao.findById(id);

		if (q == null) {
			logger.error("Queue Question not found.");
		}
		else {

			List<QueuedQuestion> qqs = player.getQueuedQuestions();
			boolean isInQueue = false;
			for (QueuedQuestion qq : qqs) {
				if (qq.getQuestion().getId() == id) {
					isInQueue = true;
				}
			}
			if (!isInQueue) {
				QueuedQuestion qq = new QueuedQuestion();
				qq.setRank(qqs.size()+ 1);  // start rank at 1
				qq.setPlayer(player);
				qq.setQuestion(q);
			}
		}
	}

	// submit a new question for a game.  Maybe be queued if not players turn as author.
	// if question already queued, will update
	public void submitQuestion(long playerId, QuestionData qd)
	{
		Player player = lookupPlayer(playerId);
		logger.info("Submit question from playerid=" + player.getId());
		logger.debug("submt found="+player.getName()+" game round="+ player.getGame().getRoundNumber());
		findPlayer(player.getGame().getId(), player.getMember().getId());
		
		// TODO -- add member to image record and verify here that any image is owned by author.

		if (qd.getId() > 0) {
			List<QueuedQuestion> qqs = player.getQueuedQuestions();
			if (qqs == null || qqs.isEmpty()) {
				logger.error("Submited question update, but none queued");
				return;
			}
			if (qqs.get(0).getQuestion().getId() != qd.getId()) {
				logger.error("Submited question update, but id not equal to queued" +
						qqs.get(0).getQuestion().getId() + " != " + qd.getId());
				return;
			}
			Question q = qqs.get(0).getQuestion();
			qd.fillQuestion(q, player.getMember());
			questionDao.create(q);
			logger.info("update queued question id="+q.getId());
		}
		else {
			logger.info("Submit new question");
			Question q = new Question();
			qd.fillQuestion(q, player.getMember());
			questionDao.create(q);
			queueQuestion(playerId, q.getId());
			if (player.getStatus() == Status.NEEDS_TO_MAKE_QUESTION) {
				getGameState(player.getGame()).eventQuestionMade();
			}
			if (player.getGame().getCurrentRound()==null) {
				logger.debug("******* Current round=null");
			} 
			else {
				logger.debug("******* Current round="+player.getGame().getCurrentRound().getRoundNumber());
				logger.debug("******* game asker="+player.getGame().getAsker().getName());
			}
		}

		
	}
	
	// just used internally, to delete questions use reorder.
	private void questionRemove(Player player, QueuedQuestion qq) {
		long qqid = qq.getId();
		// dont allow master's first quesiton to be removed
//		if (player.getIsMaster() && 
//				player.getGame().getRoundNumber() == 1 &&
//				player.getQueuedQuestions().size() == 1) {
//			logger.error("Master tried to delete first question playerid="+player.getId());
//			return;
//		}
			
		logger.debug("Delete question id = " + qqid);
		int i;
		for (i = 0; i < player.getQueuedQuestions().size(); ++i)
		{
			if (qqid == player.getQueuedQuestions().get(i).getId()) {
				logger.debug("Question found to remove = " + qqid);
				player.getQueuedQuestions().remove(i);
				break;
			}
		}
	}
	
	private Question getSkipQuestion(Player player, String msg) {
		// TODO -- check if in database already, and if so use that.
		Question q = new Question();
		q.setText("Sorry, I am skipping making my question this turn.  This question and the answers were automatically generated.  Everybody gets 25 pts, and I get nothing.");
		List<String> choices = new ArrayList<String>();
		choices.add("Everybody is right!");
		choices.add("Nobody is wrong.");
		q.setChoiceList(choices);
		q.setCreated(new Date());
		q.setAnswer(1);
		q.setAuthor(player.getMember());
		q.setExplanation(msg);
		return q;
	}
	
	public void askerSkip(long playerId, String msg) {
		Player asker = lookupPlayer(playerId);
		if (!asker.getIsAsker()) {
			logger.error("asker skip called for non-asker id=" + asker.getId());
			return;
		}
		Game game = asker.getGame();
		Question skipQ = getSkipQuestion(asker,msg);
		
		// to fix bizzare hibernate error "object references an unsaved transient instance"
		questionDao.create(skipQ);
		
		getGameState(game).eventSkipQuestion(skipQ);	
	}
	
	// TODO !!! move this into PlayerData
	public boolean findIfPlayerHasQueuedQuestion(Player player, Long id)
	{
		
		for (QueuedQuestion qq : player.getQueuedQuestions())
		{
			if (qq.getQuestion().getId() == id)
			{
				return true;
			}
		}
		return false;
	}
	
	public List<InviteData> getInvites(long gameId) {
		Game game = gameService.lookupGame(gameId);
		ArrayList<InviteData> invites = new ArrayList<InviteData>();
		for (Invite i : game.getInvites()) {
			invites.add(new InviteData(i));
		}
		return invites;
	}	
	
	public List<PlayerData> getPlayersInGame(long gameId) {
		Game game = gameService.lookupGame(gameId);
		ArrayList<PlayerData> pl = new ArrayList<PlayerData>();
		for (Player p : game.getPlayers()) {
			pl.add(new PlayerData(p));
		}
		return pl;
	}	
	
	public List<PlayerData> getActivePlayersInGame(long gameId) {
		Game game = gameService.lookupGame(gameId);
		ArrayList<PlayerData> pl = new ArrayList<PlayerData>();
		for (Player p : game.getActivePlayers()) {
			pl.add(new PlayerData(p));
		}
		return pl;
	}	
	/*
	 * Adds player to game, if didnt exist makes member with just email set.
	 * If player declines, it is removed from list.
	 * Note, new players must fill out rest of member information when registering.
	 */
	// TODO FB -- add FB invite that fills in user name, and stores user's FB id, but does not know
	// email.  Then when registering get email.  Could add FB uid to DB?  Or just set email to FB ID until registration complete.
	public InviteStatus invitePlayer(long gameId, String email) {
		Game game = gameService.lookupGame(gameId);
		logger.debug("invite player: " + email + " to game " + game.getName()) ;
		if (game.getStatus() != Game.STATUS.REGISTERING && 
				game.getStatus() != Game.STATUS.INVITING &&
				game.getStatus() != Game.STATUS.CREATING) {
					logger.warn("Cannot add player.  bad state. gameid="+gameId+" state="+game.getStatus());
				return InviteStatus.BAD_STATE;
		}		
		
		if (email.matches(".*@.*\\..*"))
		{
			Member member = memberDao.findByEmail(email);
			Invite invite = new Invite();
			if (member == null)
			{
				for (Invite i : game.getInvites()) {
					if (i.getEmail() != null && i.getEmail() == email ) 
						return InviteStatus.ALREADY_MEMBER;
				}
				invite.setEmail(email);
			}
			else {
				if (checkIfInvited(game, member)) {
					return InviteStatus.ALREADY_MEMBER;
				}
				
				invite.setEmail(member.getEmail());
				invite.setMember(member);
				invite.setFacebookId(member.getFacebookId());
				invite.setName(member.getName());
			}
			invite.setGame(game);
			inviteDao.create(invite);
		} 
		else {
			logger.warn("Invited bad email '"+email+"'");
		}
		return InviteStatus.SUCCESS;
	}

	public InviteStatus inviteMember(long gameId, long memberId) {
		logger.info("Invite Member gameid="+gameId+" MemberId="+memberId);
		Game game = gameService.lookupGame(gameId);		
		if (game==null) {
			logger.warn("Bad game id");
			return InviteStatus.BAD_GAME;		
		}
		if (game.getStatus() != Game.STATUS.REGISTERING && 
			game.getStatus() != Game.STATUS.INVITING &&
			game.getStatus() != Game.STATUS.CREATING) {
				logger.warn("Cannot add player.  bad state. gameid="+gameId+" state="+game.getStatus());
			return InviteStatus.BAD_STATE;
		}		
		Member member = memberDao.findById(memberId);
		if (member==null) { 
			logger.warn("Bad member id " + memberId);
			return InviteStatus.BAD_MEMBER;
		}
		logger.debug("invite Member: " + memberId + " to game " + game.getName()) ;
		if (checkIfInvited(game, member)) {
			return InviteStatus.ALREADY_MEMBER;
		}
			
		Invite invite = new Invite();
		invite.setEmail(member.getEmail());
		invite.setMember(member);
		invite.setFacebookId(member.getFacebookId());
		invite.setName(member.getName());
		invite.setGame(game);
		inviteDao.create(invite);
		return InviteStatus.SUCCESS;
	}
	
	public InviteStatus inviteFbPlayer(long gameId, String id, String firstName, String lastName) {
		Game game = gameService.lookupGame(gameId);		
		if (game==null) return InviteStatus.BAD_GAME;		
		logger.info("invite facebook player: " + firstName + " " +lastName + "("+id+") to game " + game.getName()) ;
		if (game.getStatus() != Game.STATUS.REGISTERING && 
			game.getStatus() != Game.STATUS.INVITING &&
			game.getStatus() != Game.STATUS.CREATING) {
				logger.warn("Cannot add player.  bad state. gameid="+gameId+" state="+game.getStatus());
			return InviteStatus.BAD_STATE;
		}		
		Member member = memberDao.findByFacebookId(id);
		Invite invite = new Invite();
		if (member == null)
		{
			for (Invite i : game.getInvites()) {
				if (i.getFacebookId() != null && i.getFacebookId() == id ) 
					return InviteStatus.ALREADY_MEMBER;
			}
			invite.setFacebookId(id);
			invite.setName(firstName + " " + lastName);
			invite.setGame(game);
		}
		else {
			if (checkIfInvited(game, member)) {
				return InviteStatus.ALREADY_MEMBER;
			}
			invite.setEmail(member.getEmail());
			invite.setMember(member);
			invite.setFacebookId(member.getFacebookId());
			invite.setName(member.getName());
		}
		invite.setGame(game);
		inviteDao.create(invite);

		return InviteStatus.SUCCESS;
	}

	private boolean checkIfInvited(Game game, Member member) {
		for (Invite i : game.getInvites()) {
			if (i.getMember() != null && i.getMember().getId() == member.getId() ) {
				logger.warn("Duplicate member id invite " + member.getId());
				return true;
			}
		}
		if (member.getId() == game.getMaster().getId()) {
			logger.warn("Master tried to invite self??? " + game.getId());
			return true;
		}
		return false;
	}

	public MemberData registerFbPlayer(String fbid, String firstname, String lastname, String email, Long gameId) {
		logger.info("register facebook player: " + firstname + " " +lastname + " fbid=" + fbid);
		Member member = memberDao.findByFacebookId(fbid);
		if (member == null)
		{
			member = new Member();
			member.setFacebookId(fbid);
			member.setFirstName(firstname);
			member.setLastName(lastname);
			member.setEmail(email);
			// this will NOT allow user to log in non-fb way.  but they can change it via profile.
			member.setPassword("Facebook Only");
			memberDao.create(member);	
		}
		if (gameId != null) {
			Game game = gameService.lookupGame(gameId);
			if (game != null) {
				logger.info("register facebook user to game: game=" + game.getId()+ " fbid="+fbid);
				addPlayer(game.getId(), member.getId());
			}
		}
		// reload member to get Hibernate wrapper.
		return new MemberData( memberDao.findById(member.getId()));	
	}
		
	// if no game specified choose auotmatically in this order:
	// 	- game if only one
	// 	- game needing action if more than one
	// 	- active game if only one
	// 	NO - any game
	// TODO rename autoSetPlayer
	public long autoSetGameId(long memberId) {
		long gameId=0;
		Member member = memberDao.findById(memberId);
		List<Player> players = playerDao.findByMember(member);
		if (players.size() == 1) {
			gameId = players.get(0).getGame().getId();
		} 
		else {
			for (Player p : players) {
				if (p.getIsActionRequired()) {
					gameId = p.getGame().getId();
					break;
				}
			}
		}	
		if (gameId <= 0) {
			long activeId = 0;
			long activeCount = 0;
			for (Player p : players) {
				if (p.getGame().getIsActive())	 {
					activeId = p.getGame().getId();
					activeCount++;
				}
			}
			if (activeCount == 1) gameId = activeId;
		}	
		
		logger.debug("Auto set game id = " + gameId);
		return gameId;
	}	
	
	// could get more advanced here and pick game that needs action if only one...
	// TODO -- with no Session-in-view, need to make sure only id passed in.  cant trust objects.
	// TODO -- use autosetgameid instead of this?
	// TODO -- rename to getDefaultPlayer
	public long getDefaultGameId(long memberId) {
		// set game Id if only one game.
		Member member = memberDao.findById(memberId);
		long gameId = -1;
		if (member.getPlayers().size() == 1)
		{
			gameId = member.getPlayers().get(0).getGame().getId();
		}
		return gameId;
	}

	public List<QuestionData> getQueuedQuestions(Long playerId) {
		Player player = lookupPlayer(playerId);
		ArrayList<QuestionData> qds = new ArrayList<QuestionData>();
		for (QueuedQuestion qq : player.getQueuedQuestions()) {
			qds.add(new QuestionData(qq.getQuestion()));
		}
		return qds;
	}
	
	// reorder and/or delete existing questions
	public void reorderQueuedQuestions(Long playerId, List<Integer> newOrder) {
		Player player = lookupPlayer(playerId);

		// check for invalid ids
		for (Integer id : newOrder) {
			boolean found = false;
			for (QueuedQuestion qq : player.getQueuedQuestions()) {
				if (qq.getQuestion().getId()==id) {
					found = true;
					break;
				}
			}
			if (!found) {
				logger.error("Invalid question id in reorder. id="+id);
				throw new TrivolousError("Invalid question id " + id);
			}
		}
		
		// rerank by new order and make list of not found ids to delete
		ArrayList<QueuedQuestion> qqsToBeDeleted = new ArrayList<QueuedQuestion>(); 
		for (QueuedQuestion qq : player.getQueuedQuestions()) {
			boolean found = false;
			int qid =  qq.getQuestion().getId();
			int rank = 1;
			for (Integer id : newOrder) {
				if (qid == id) {
					found = true;
					break;
				}
				rank++;
			}
			if (!found) {
				qqsToBeDeleted.add(qq);
			}
			else {
				// reorder based on new order
				qq.setRank(rank);
			}
		}

		// delete separately to prevent concurrent mod error
		for (QueuedQuestion qq : qqsToBeDeleted) {
			questionRemove(player, qq);
		}
	}

	// returns id of new comment
	public long submitComment(Long playerId, String text) {
		Player player = lookupPlayer(playerId);
		long id;
		if (player.getGame().getStatus() == STATUS.COMPLETE) {
			id = commentService.submitGameOverComment(player, text);
		}
		else {
			id = commentService.submitCommentToAnswered(playerId, text);
		}
		return id;
	}
	
	public void submitCommentToMaster(Long playerId, String text) {
		commentService.submitCommentToMaster(playerId, text);
	}
	
	// returns list of all past players (as members) excluding any already in game.  used for invites.
	public List<MemberData> findPastPlayers(Long playerId) {
		Player master = lookupPlayer(playerId);
		Set<Member> memberSet = new HashSet<Member>();
		for (Player thisPlayer : playerDao.findByMember(master.getMember())) {
			for (Player otherPlayer : thisPlayer.getGame().getPlayers()) {
				memberSet.add(otherPlayer.getMember());
			}
		}
		
		// remove all players currently invited
		for (Invite i  : master.getGame().getInvites()) {
			memberSet.remove(i.getMember());
		}
		// remove self!
		memberSet.remove(master.getMember());
		
		// sort by first name
		List<Member> sortedMembers = new ArrayList<Member>(memberSet);
		Collections.sort(sortedMembers, new Comparator<Member>() {
		    public int compare(Member o1, Member o2) {
		        return o1.getName().compareTo(o2.getName());
		    }});
		
		ArrayList<MemberData> membersData = new ArrayList<MemberData>();
		for (Member m : sortedMembers) {
			membersData.add(new MemberData(m));
		}
		return membersData;
	}	

	// returns true if game is started
	public boolean registrationResponse(long memberId, long gameId, boolean wasAccepted)
	{
		Game game = gameService.lookupGame(gameId);
		Member member = memberDao.findById(memberId);

		logger.info("registrationResponse mid= "+memberId + " gid = " + gameId);

		if (game == null || member == null) {
			logger.warn("Invalid gameId or memberId");
			return false;
		}
		
		/////////////////////////////////////////////////////
		Invite foundInvite = null;
		for (Invite i : game.getInvites()) {
			if (i.matchesMember(member)) { 
				foundInvite = i;
				break;
			}
		}
		
		if (foundInvite != null) {
			foundInvite.setHasJoined(wasAccepted);
		}
		else {
			logger.warn("Unknown attempt to join " + gameId + " mid="+memberId);
			return false;
		}

		if (wasAccepted) {
			addPlayer(gameId, memberId);
			PlayerData playerData = findPlayer(gameId, memberId);
			Player player = playerDao.findById(playerData.getId());
			generatePlayerName(player);
		}		

		boolean allregistered = getGameState(game).eventInviteResponse();

		return allregistered;
	}		
	
	
}


