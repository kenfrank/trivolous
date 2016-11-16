package com.trivolous.game.domain.logic;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trivolous.game.dao.GameDao;
import com.trivolous.game.dao.MemberDao;
import com.trivolous.game.dao.PlayerDao;
import com.trivolous.game.dao.QuestionDao;
import com.trivolous.game.data.ActiveGameData;
import com.trivolous.game.data.CommentData;
import com.trivolous.game.data.GameDescriptionData;
import com.trivolous.game.data.GameList;
import com.trivolous.game.data.InviteData;
import com.trivolous.game.data.PlayerData;
import com.trivolous.game.data.QuestionData;
import com.trivolous.game.data.RoundData;
import com.trivolous.game.domain.Game;
import com.trivolous.game.domain.Game.STATUS;
import com.trivolous.game.domain.GameComment;
import com.trivolous.game.domain.Invite;
import com.trivolous.game.domain.Member;
import com.trivolous.game.domain.Player;
import com.trivolous.game.domain.Question;
import com.trivolous.game.domain.Round;
import com.trivolous.game.notify.NotificationSenderInterface;

// TODO -- eventually rename this back to GameService once all old code is ported over.
@Service
@Transactional
public class NewGameService  {
	final int MAX_PLAYERS = 10;
	protected final Log logger = LogFactory.getLog(getClass());
	@Autowired
	GameDao gameDao;
	@Autowired
 	QuestionDao questionDao;
	@Autowired
	PlayerDao playerDao;
	@Autowired
	NotificationSenderInterface notificationSender;
	@Autowired
	AnswerSession answerSession;
	@Autowired
	private ImageService imageService;
	@Autowired
 	MemberDao memberDao;	
	// TODO -- attempt to remove circular reference here.  Should be.  Member <- Game <- Player.
	@Autowired
	private PlayerService playerService;
	@Autowired
	private CommentService commentService;
	
	public void setGameDao(GameDao gameDao) {
		this.gameDao = gameDao;
	}

	public void setQuestionDao(QuestionDao questionDao) {
		this.questionDao = questionDao;
	}

	public void setPlayerDao(PlayerDao playerDao) {
		this.playerDao = playerDao;
	}

	public void setImageService(ImageService imageService) {
		this.imageService = imageService;
	}

	// send a reminder to players to take their turn or register depending on game state.
	// note, daily reminders are sent from a different spot.
	public void sendReminder(long gameId) {
		Game game = lookupGame(gameId);
		if (game.getStatus() == STATUS.REGISTERING) {
			if (!game.getAreAllPlayersRegistered()) {
				notificationSender.sendInvite(game, true, false);
			}
		}
		else
		if (game.getStatus() == STATUS.ACTIVE) {
			if (game.getCurrentRound().getQuestion() == null) {
				notificationSender.sendReminderForQuestion(game);
			}
			else {
				notificationSender.sendReminderToAnswer(game);
			}
		}
	}
		
	public void setAnswerSession(AnswerSession answerSession) {
		this.answerSession = answerSession;
	}

	public void setNotificationSender(NotificationSenderInterface notificationSender) {
		this.notificationSender = notificationSender;
	}

 	public NewGameService()
 	{
 	
 	}

 	public GameDescriptionData getGameDescription(long gameId)
	{
		Game g = lookupGame(gameId);
		return new GameDescriptionData(g);
	}
 	
	public ActiveGameData getActiveGameData(long gameId)
	{
		Game g = lookupGame(gameId);
		return new ActiveGameData(g);
	}
 	
 	

	public QuestionData findQuestionById(long id)
	{
		// look up game.
		logger.debug("findQuestionById "+id);
		Question q = lookupQuestion(id);
		return new QuestionData(q);
	}
	
	private Question lookupQuestion(long id) {
		// TODO -- throw exception if not found.
		return questionDao.findById(id);
	}

	private List<Game> findAllGames()
	{
		// look up game.
		return gameDao.findAll(); 
	}

	// create a new game
	// TODO -- consider making masterservice.  that way game service wont reference players.
	public long createNewGame(GameDescriptionData gd, long masterId)
	{
		Member master = memberDao.findById(masterId);
		if (master==null) {
			logger.error("Bad master id in create ="+masterId);
			return -1;
		}
		Game game = new Game();
		game.setId(gd.getId());
		game.setName(gd.getName());
		game.setDescription(gd.getDescription());
		game.setTotalAuthorCycles(gd.getTotalAuthorCycles());
		game.setMaxPlayers(gd.getMaxPlayers());
		game.setAllowOpenRegistration(gd.getAllowOpenRegistration());
		game.setPrivacy(gd.getPrivacy());
		game.setCountDown(gd.getCountDown());
		
    	game.setCreated(new Date());
    	
		game.setMaster(master);
		game.setStatus(Game.STATUS.CREATING);

		gameDao.create(game);

		// make master first player
		// playerService.addPlayer(game.getId(), masterId);
		// automatically register master too
		Member member = memberDao.findById(masterId);
		Player player = new Player();
		player.setGame(game);
		player.setMember(member);
		player.setOrder(1);
		player.setName(member.getFirstName());
		logger.debug("addPlayer is master game="+game.getName()+ " member="+ member.getId());
		playerDao.create(player);
				

		logger.info("Created new game.  id = "+game.getId()+" masterid="+master.getId()+" name="+game.getName());
		gd.setGameId(game.getId());
		return game.getId();
	}
	
	// enter this state to send notifications to email and begin registration state machine.
	public void beginGameRegistration(long gameId)
	{
		Game game = lookupGame(gameId);
		
		// set max players
		int maxPlayers = game.getMaxPlayers();
		maxPlayers += game.getPlayers().size();
		maxPlayers += game.getInvites().size();
		if (maxPlayers > 12) {
			logger.warn("Max players > 12");
			maxPlayers = 12;
		}
		game.setMaxPlayers(maxPlayers);
		
		if (game.getStatus() == Game.STATUS.INVITING) {	
			getGameState(game).eventBeginRegistration();
		} 
		else {
			logger.error("Game in bad state to start registration " + gameId + " state="+game.getStatus());
		}
	}
	
	// enter this state after creating to allow link to be used.  does not send email notifications yet, or 
	// start registration countdown.
	public void beginGameInvitation(long gameId)
	{
		Game game = lookupGame(gameId);
		if (game.getStatus() == Game.STATUS.CREATING) {	
			getGameState(game).eventBeginInvitation();
		}
		else {
			logger.error("Game in bad state to start invite " + gameId + " state="+game.getStatus());
		}
	}

	// Used by other services... could just have them use Dao and make this private.
	Game lookupGame(long gameId) {
		Game game = gameDao.findById(gameId);
		if (game==null) {
			logger.error("Game id not found. id="+gameId);
			// TODO -- do this better.  Make custom exceptions for service maybe?
			// throw new NullPointerException("Game id not found");
			throw new TrivolousError("Game id not found = " + gameId);
		}
		return game;
	}
	
	// This is only done to force a start because master does not want to wait for an auto start when
	// everyone is registered, or registration window closes.
	public void startGame(long gameId)
	{
		Game game = lookupGame(gameId);
		logger.info("start game = " + game.getName());
		if (game.getStatus() == Game.STATUS.REGISTERING) {
			getGameState(game).eventStart();
		}
		
	}
	

	// Package scope only.  
	void deleteGame(Game g )
	{

		// TODO -- delete question too?
		// TODO -- delete any registration comments too?
		logger.warn("Deleting game : '" + g.getName() +"'");
		for (Player p : g.getPlayers()) {
			playerDao.remove(p);
		}
		gameDao.delete(g);
	}
		
	public void updateGame(GameDescriptionData gameDescription)
	{
		logger.info("Update game id="+gameDescription.getId());
		// TODO -- should verify that player is master here?  would need to get player too.  Could divide services into player/master service to make this clear and then
		// leave security up to higher level.
		
		// because no longer using session in view, and backing objects, need to copy over data.
		// Should really redo whole data model at this point... but that is a big refactoring.
		Game g = gameDao.findById(gameDescription.getId());
		if (g != null) {
			
			// dont let cycles be set to less than already played.
			Round round = g.getCurrentRound();
			if (round != null) {
				if (round.getAskerCycle() >= g.getTotalAuthorCycles()) {
					g.setTotalAuthorCycles(round.getAskerCycle());
				}
			}
			
			g.setCountDown(gameDescription.getCountDown());
			g.setDescription(gameDescription.getDescription());
			g.setName(gameDescription.getName());
			g.setTotalAuthorCycles(gameDescription.getTotalAuthorCycles());
			g.setMaxPlayers(gameDescription.getMaxPlayers());
			
			gameDao.create(g);
		}
	}
			

	// TODO -- this seems strange.  remove?
	public Round findRoundByQuestion(long gameId, long questionId)
	{
		Game g = lookupGame(gameId);
		Question question = lookupQuestion(questionId);
		// TODO (Low) -- a database query may be better here? But no RoundDao.  Ok for now.
		for (Round r : g.getRounds())
		{
			// TODO -- question could be null?  is this function ever called?
			if (r.getQuestion().getId() == question.getId())
				return r;
		}
		return null;
	}
	
	



	// returns TRUE if game deleted
	public boolean endGame(long gameId) {
		Game game = lookupGame(gameId);
		logger.info("End game id= " + game.getId());
		// TODO -- this should be done is action handler instead because game can end automatically too (register timout).
		// if state is inviting, game is not deleted right now because some players may have joined and answered questions.
		// probably should delete in future though.  Need to make delete more complete -- remove players, turns, questions, etc...
		if (game.getStatus() == Game.STATUS.CREATING) {
			// delete game if not started
			deleteGame(game);
			return true;
		} else {
			getGameState(game).eventCancelGame();
		}
		return false;
	}
	
	
	public void pauseGame(long gameId) {
		Game g = lookupGame(gameId);
		g.setStatus(Game.STATUS.INACTIVE);
	}
	
	public void restartGame(long gameId) {
		Game g = lookupGame(gameId);
		g.setStatus(Game.STATUS.ACTIVE);
	}
	




	public void sendRegistrationLink(String email, String link)
	{
		notificationSender.sendRegistrationLink(email, link);
	}
	

	private GameState getGameState(Game game)
	{
		return new GameState(game, new GameStateActionHandlerImpl(notificationSender, this));
	}
	
	public RoundData getRound(long gameId, int roundNum) 
	{
		Round r;
		Game game = lookupGame(gameId);
		if (roundNum >= game.getRoundNumber()) {
			r=  game.getCurrentRound();
		}
		else {
			if (roundNum < 1) roundNum = 1;
			r =  game.getCompletedRounds().get(roundNum - 1);
		}
		List<GameComment> comments = commentService.findAllRoundComments(gameId, roundNum);
		return new RoundData(r, comments);
	}

	public List<RoundData> getCompletedRounds(long gameId) 
	{
		ArrayList<RoundData> rounds = new ArrayList<RoundData>(); 
		Game game = lookupGame(gameId);
		for (Round r : game.getCompletedRounds()) {
			List<GameComment> comments = commentService.findAllRoundComments(gameId, r.getRoundNumber());
			rounds.add(new RoundData(r, comments));
		}
		return rounds;
	}

	public RoundData getCurrentRound(long gameId) 
	{
		Round r;
		Game game = lookupGame(gameId);
		r=  game.getCurrentRound();
		List<GameComment> comments = commentService.findAllRoundComments(gameId, r.getRoundNumber());
		return new RoundData(r, comments);
	}


	// TODO -- this shouldnt be public...
	public void dailyTick(Date now) {
		List<Game> games = findAllGames();
		
		for (Game g: games)
		{
			getGameState(g).eventDailyTick(now);
		}
	}
	
	public List<GameDescriptionData> getAllGames() {
		ArrayList<GameDescriptionData> games = new ArrayList<GameDescriptionData>();
		for (Game game : findAllGames()) {
			games.add(new GameDescriptionData(game));
		}
		return games;
	}	
	
	private String findWinner(Game game) {
		int max = 0;
		Player winner = null;
		int tie = 0;
		for (Player p : game.getActivePlayers()) {
			if (p.getScore() > max) {
				max = p.getScore();
				winner = p;
				tie = 0;
			}
			else
			if (p.getScore() == max) {
				++tie;
			}
		}
		if (winner==null) return "None";  // not sure how this is possible, but just in case.
		return (tie>0 ? ""+(tie+1)+" way tie" : winner.getName());
	}

	public GameList getGameList(long memberId) {
		GameList gameList = new GameList();
		gameList.setActiveGames(new ArrayList<GameList.ActiveGame>());
		gameList.setCompletedGames(new ArrayList<GameList.CompletedGame>());
		gameList.setPublicGames(new ArrayList<GameDescriptionData>());
		gameList.setInvitedGames(new ArrayList<GameDescriptionData>());
		Member member = memberDao.findById(memberId);
	
		// TODO -- need to optimize this... will not scale.  make a query to get all game with a given member.
		for (Game game : findAllGames()) {
	
			logger.debug("gameid="+game.getId());
			Player player = null;
			for (Player p : game.getPlayers()) {
				if (p.getMember().getId() == memberId) {
					player = p;
					logger.debug("player found "+p.getId());
					break;
				}
			}
			
			if (player == null) {
				boolean foundInvite = false;
				if (game.getStatus() == STATUS.REGISTERING || game.getStatus() == STATUS.INVITING) {
					for (Invite i : game.getInvites()) {
						if (i.matchesMember(member)) {
							foundInvite = true;
							break;
						}
					}
				}
				if (foundInvite) {
					gameList.getInvitedGames().add(new GameDescriptionData(game));
				}
				else if (game.getPrivacy() == Game.PRIVACY.PUBLIC) {
					logger.debug("public game "+game.getId());
					gameList.getPublicGames().add(new GameDescriptionData(game));
				}
			}
			else 
			{
				logger.debug("game state "+game.getStatus());
				switch (game.getStatus())
				{
					case REGISTERING:
					case INVITING:
						// only include active players (not quit)
						if (player.getIsActive()) {
							GameList.ActiveGame ag = gameList.new ActiveGame();
							ag.setDesc(new GameDescriptionData(game));
							ag.setStatus(new ActiveGameData(game));
							ag.setPlayer(new PlayerData(player));
							gameList.getActiveGames().add(ag);
							logger.debug("Add to Active list " + game.getId());
						}
						break;
					case ACTIVE:
					case CREATING:
					case INACTIVE:
						// only include active players (not quit)
						if (player.getIsActive()) {
							GameList.ActiveGame ag = gameList.new ActiveGame();
							ag.setDesc(new GameDescriptionData(game));
							ag.setStatus(new ActiveGameData(game));
							ag.setPlayer(new PlayerData(player));
							gameList.getActiveGames().add(ag);
							logger.debug("Add to Active list " + game.getId());
						}
						break;
					case COMPLETE:
						GameList.CompletedGame cg = gameList.new CompletedGame();
						cg.setDesc(new GameDescriptionData(game));
						// TODO -- probably should store this and not calculate each time for long term optimization
						cg.setWinnerName(findWinner(game));
						gameList.getCompletedGames().add(cg);
						logger.debug("Add to Completed list " + game.getId());
						break;
				}
			}
		}
		return gameList;
	}	
	
	public boolean authorizeGameAccess(String code,long memberid, long gameId) {
		// not sure if this has to be done everytime.  Could just do it when gameid comes
		// in as a parameter?  Could gameid go invalid in middle of a session?
		Game game = null;
		PlayerData player = null;
		// could save this in session so all game controllers could use it?
		
		if (gameId > 0) {
			game = gameDao.findById(gameId);
		}
		
		if (game != null)
		{
			// TODO -- optimize this.  probably should be internal call.
			player = playerService.findPlayer(game.getId(), memberid);
			
			if (player != null) {
				// if game is being created, must be master to access.
				if (game.getStatus() == STATUS.CREATING && player.getIsMaster() == false) {
					game = null;
					player = null;
				}
			}
			else {
				// TODO (med) -- friends only not handled here -- need to implement with facebook maybe.
				if (game.getPrivacy() == Game.PRIVACY.PUBLIC) {
					// can access game because it is public
				}
				else
				if (game.getAllowOpenRegistration()) {
					// allow member to register as player -- could have done here, but want to keep intercepter simple.
					if (!code.equals(game.getCode())) {
						logger.warn("Invalid code for open registration got='"+code+"' exp='"+game.getCode()+"'");
						game = null;
					}
				}
				else {
					// cannot access game
					game = null;
				}
				
			}
			
		}
		return game != null;
	}	





	public String getFacebookIdList(long gameId) {
		Game game = lookupGame(gameId);
		String fbIds = "";
		for (Player p : game.getPlayers()) {
			if (p.getMember().getIsNew() && !p.getMember().getFacebookId().isEmpty())
			{
				fbIds += p.getMember().getFacebookId() + ",";
			}
		}
		if (!fbIds.isEmpty()) {
			// remove last comma
			fbIds=fbIds.substring(0, fbIds.length()-1);
		}
		return fbIds;	
	}

	// TODO invite -- remove invites once game is started.
	
	void removeInactivePlayers(Game game) {
//		// IMPORTANT -- MUST remove any unregistered players. (cannot do from gameState because does not have dao)
//		// if any players are not registered here, they must have either declined or not responded
//		// remove them.
//		// IMPORTANT!  Must use iterator to remove elements while iterating.
//		// TODO -- handle removes of players in callback, in order to move all logic to game state.
//		Iterator<Player> iter = game.getPlayers().iterator();
//		while (iter.hasNext()) {
//			Player p = iter.next();
//			if (p.getHasRegistered() && p.getIsActive() == false)
//			{
//				logger.info("remove declinded or non-confirmed player = " + p.getMember().getName());
//				iter.remove();
//				playerDao.remove(p);
//			}
//		}						
	}	
	
	public List<CommentData> getGameComments(long gameId) {
		List<GameComment> comments = commentService.findGameOverComments(gameId);
		List<CommentData> cdlist = new ArrayList<CommentData>();
		for (GameComment gc : comments) {
			cdlist.add(new CommentData(gc));
		}
		return cdlist;
	}

	// for now just use Player Data because that what was used before.  can refactor later to use InviteData...
	public List<InviteData> getInvites(long gameId) {
		Game game = lookupGame(gameId);
		List<InviteData> ilist = new ArrayList<InviteData>();
		for (Invite i : game.getInvites()) {
			// TODO -- should remove from invites once joined.
			if (i.getHasJoined() == null) {
				ilist.add(new InviteData(i));
			}
		}
		return ilist;
	}
	
}