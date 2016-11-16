package com.trivolous.game.domain.logic;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trivolous.game.domain.Game;
import com.trivolous.game.domain.Game.STATUS;
import com.trivolous.game.domain.Player;
import com.trivolous.game.domain.Question;
import com.trivolous.game.domain.QueuedQuestion;
import com.trivolous.game.domain.Round;
import com.trivolous.game.domain.Turn;
/*
 * TODO - send out reg reminder after X days.  Cancel after X days if not enough players.  
 * - add function
 * 		Reminder tick (once a day?)
 * - add actions:
 * 		ACTION_REGISTRATION_REMINDER
 * 		ACTION_REGISTRATION_CANCELLED
 */
public class GameState {

	private Game game;
	protected final Log logger = LogFactory.getLog(getClass());
	private Player nextAsker = null;

	
	public enum Actions {
		ACTION_BEGIN_REGISTRATION,
		ACTION_END,
		ACTION_CANCELLED,
		ACTION_CANCELLED_NOT_ENOUGH_PLAYERS,
		ACTION_QUESTION_READY_MADE,
		ACTION_QUESTION_READY_QUEUED,
		ACTION_MAKE_QUESTION, 
		ACTION_END_REGISTRATION, 
		ACTION_REMINDER_QUESTION, 
		ACTION_REMINDER_ANSWER, 
		ACTION_REMINDER_MASTER, 
		ACTION_REMINDER_JOIN_LAST_CHANCE, 
		ACTION_REMINDER_JOIN, 
	}
	
	public enum State {
		STATE_AUTHORING,
		STATE_ANSWERING,
		STATE_NOT_ACTIVE
	}
	
	void logInfo(String msg) {
		logger.info("gameId=" + game.getId() +" : " + msg);
	}
	
	void logError(String msg) {
		logger.error("gameId=" + game.getId() +" : " + msg);
	}
	
	//private State state;

	interface GameStateActionHandler {
		void gameStateActionHandler(Actions action, Game game);	
	}
	
	GameStateActionHandler gameStateActionHandler;
	
	public GameState(Game game, GameStateActionHandler handler)
	{
		this.game = game; 
		this.gameStateActionHandler = handler;
	}

	public State getState()
	{
		if (!game.getIsActive())
		{
			return State.STATE_NOT_ACTIVE;
		}
		
		if (game.getQuestion() == null)
		{
			return State.STATE_AUTHORING;
		}
		else
		{
			return State.STATE_ANSWERING;
		}
	}
	
	/*
	 *  TASKS
	 */
	
	private void taskUnqueueQuestion(List<QueuedQuestion> qqs)
	{
		QueuedQuestion qq = qqs.get(0);
		
		// remember questions can be used in multiple games in the long run.
		game.getCurrentRound().setQuestionMadeDate(new Date());

		taskReadyQuestion(game.getAsker(), qq.getQuestion());
		
		// this removes from database via hibernate too.
		game.getAsker().getQueuedQuestions().remove(qq);
	}
		
	private void taskReadyQuestion(Player player, Question q) {
		game.setQuestion(q);
		game.getCurrentRound().setQuestion(q);
		
		// insert author turn record
		Turn turn = new Turn();
		turn.setIsAsker(true);
		turn.setCreated(new Date());
		turn.setPlayer(player);
		turn.setRound(game.getCurrentRound());
	}
	

	private void taskStartRound()
	{

		Round round = new Round();
		round.setStartDate(new Date()); 
		round.setAsker(nextAsker);

		// If asker is master then asker cycle completed.
		int nextCycle  = game.getAskerCycle();
		if (nextAsker.getMember().getId() == game.getMaster().getId()) ++nextCycle; 
		round.setAskerCycle(nextCycle);
				
		// increment from last complete round.  this means duplicates are possible, but only one will be complete.
		Round lastCompleteRound = game.getLastCompletedRound();
		Integer lastCompleteRoundNumber = lastCompleteRound == null ? 0 : lastCompleteRound.getRoundNumber();
		round.setRoundNumber(lastCompleteRoundNumber + 1);
		
		round.setGame(game);
	
		logInfo("Starting round#" + game.getRoundNumber()+ " asker="+game.getAsker().getName());	
	}

	private void taskStartInvitation() {

		game.setStatus(Game.STATUS.INVITING);
		nextAsker = game.getMasterPlayer();
		taskStartRound();	
		
		List<QueuedQuestion> qqs =  game.getAsker().getQueuedQuestions();
		taskUnqueueQuestion(qqs);
	
	}
	
	private void taskStartGame() {
		// everything done in invitation now.
		game.setStatus(Game.STATUS.REGISTERING);
	}
	
	
	/*
	 * TRANSIENT STATES 
	 */
	
	private void transientEndRound()
	{
		logInfo("GameState: transientEndTurn");
		Round round = game.getCurrentRound();
		round.setEndDate(new Date()); 
		
		// score
		GameLogic gameLogic = new GameLogic(game);
		gameLogic.score();
		
		transientNextAuthor();
	}
	
	private void transientNextAuthor()
	{
		logInfo("GameState: transientNextAuthor current=" + game.getAsker().getName());
		
		GameLogic gameLogic = new GameLogic(game);
		nextAsker = gameLogic.getNextAsker();  // will advance round and set game to complete if over

		logInfo("GameState: transientNextAuthor new=" + nextAsker.getName());

		checkForEndOfGame();
	}

	private void transientStartRound()
	{
		logInfo("GameState: transientStartTurn");
		
		taskStartRound();

		checkQuestionQueued();
	}
	
	/*
	 * CHECK POINTS
	 */

	private void checkForEndOfGame()
	{
		logInfo("GameState: checkForEndOfGame cyles = " + game.getTotalAuthorCycles());

		if (nextAsker.getIsMaster() && 
				game.getAskerCycle() >= game.getTotalAuthorCycles())
		{
			logInfo("GameState: endofgame check true");
			game.setStatus(STATUS.COMPLETE);
			game.setEndDate(new Date());
			actionHandler(Actions.ACTION_END);
		}
		else
		{
			transientStartRound();
		}
	}
	

	private void checkAllAnswered()
	{
		logInfo("GameState: checkAllAnswered");		
		
		boolean allAnswered = (
				game.getAreAllPlayersRegistered() &&
				game.getPlayersLeftToAnswer() == 0);
	
		if (allAnswered == true) {
			transientEndRound();
		}
	}
	

	private void checkQuestionQueued()
	{
		logInfo("GameState: checkQuestionQueued for " + game.getAsker().getName());
		// if question is queued, use it right away.
		List<QueuedQuestion> qqs =  game.getAsker().getQueuedQuestions();
		
		if (!qqs.isEmpty())
		{
			logInfo("Using queued question for asker " + game.getAsker().getName());
			taskUnqueueQuestion(qqs);
			
			// notify players round complete and new question ready.
			actionHandler(Actions.ACTION_QUESTION_READY_QUEUED);
		} else
		{
			actionHandler(Actions.ACTION_MAKE_QUESTION);
			//setState(State.STATE_AUTHORING);
		}
	}

	/*
	 * EVENTS
	 */
	
	public void eventCancelGame()
	{
		logInfo("GameState: eventCancelGame");
		game.setStatus(Game.STATUS.COMPLETE);
		game.setEndDate(new Date());
		actionHandler(Actions.ACTION_CANCELLED);
	}
	public void eventPause()
	{
		logInfo("GameState: eventPause");
		// TODO (high) -- implement game pause...
	}

	public void eventQuestionMade()
	{
		logInfo("GameState: eventQuestionMade");
		State state = getState();
		switch (state)
		{
		case STATE_AUTHORING:
			// if author made it then queue it up!
			List<QueuedQuestion> qqs =  game.getAsker().getQueuedQuestions();
			if (!qqs.isEmpty())
			{
				taskUnqueueQuestion(qqs);
				actionHandler(Actions.ACTION_QUESTION_READY_MADE);
				//setState(State.STATE_ANSWERING);
			}
			break;
		case STATE_ANSWERING:
		default:
			logError("Question made in invalid state: " + state);
			break;
		}
	}
	
	// Note: to do this a 'skip' question must be queued already.  the correct answer must be the first choice.
	public void eventSkipQuestion(Question skipQ)
	{
		logInfo("GameState: eventSkipQuestion");
		switch (getState())
		{
		case STATE_AUTHORING:
			game.getCurrentRound().setQuestionMadeDate(new Date());
			taskReadyQuestion(game.getAsker(), skipQ);
			// make all players answer with right answer.
			for (Player p : game.getActivePlayers()) {
				if (!p.getIsAsker()) {
			 		Turn turn = new Turn();
					turn.setIsAsker(false);
					turn.setPlayer(p);
					turn.setCreated(new Date());
					turn.setRound(game.getCurrentRound());
					turn.setBet(25);
					turn.setTimeToAnswer(1);
					turn.setChoice(1);
				}
			}
			checkAllAnswered();
			break;
		default:
			logError("Question skip in invalid state");
			break;
		}
	}
	public void eventAnswer()
	{
		logInfo("GameState: eventAnswer");
		switch (getState())
		{
		case STATE_ANSWERING:
			checkAllAnswered();
			break;
		default:
			// only expect answers if in answering state
			logError("Answer made in invalid state");
			break;
		}		
	}
	
	public void eventPlayerRemoved()
	{
		logInfo("GameState: eventPlayerRemoved");
		switch (getState())
		{
		case STATE_AUTHORING:
			// was it the author?
			if (game.getAsker().getIsActive() == false)
			{
				transientNextAuthor();
			}
			break;
		case STATE_ANSWERING:
			checkAllAnswered();
			break;
		default:
			// only expect answers if in answering state
			logError("Remove made in invalid state");
			break;
		}		
	}
	
	public boolean eventInviteResponse()
	{
		logInfo("GameState: eventInviteResponse game=" + game.getName());

		// if all players responded then start game
		if (!game.getAreAllPlayersRegistered()) return false;
		
		eventStart();
		return true;
		
	}
	
	// Start the game (end registration).  If any one not registered yet, drop em.
	public void eventStart()
	{
		logInfo("GameState: eventStart");

		if (game.getStatus() != Game.STATUS.REGISTERING)
		{
			logError("End Registration made in invalid state " + game.getStatus());
			return;
		}

		long MIN_PLAYERS = 3;
		if (game.getActivePlayers().size() < MIN_PLAYERS) {
			logError("Not enough players... ending game before started.");
			game.setStatus(Game.STATUS.COMPLETE);
			game.setEndDate(new Date());
			actionHandler(Actions.ACTION_CANCELLED_NOT_ENOUGH_PLAYERS);
			return;
		}
		game.setStatus(Game.STATUS.ACTIVE);
		
		// TODO invite -- remove all invites at this point.

		actionHandler(Actions.ACTION_END_REGISTRATION);

		// check if everyone answered already... this can happen if all players answer
		// then last player declines invite for example.
		checkAllAnswered();
	}

	public void eventInitialize()
	{
		logInfo("GameState: eventInitialize");
		
		game.setStatus(Game.STATUS.CREATING);
		game.setCreated(new Date());
	}
	
	
	public void eventBeginInvitation()
	{
		logInfo("GameState: eventBeginRegistration");
		
		if (game.getStatus() != Game.STATUS.CREATING)
		{
			logError("Begin Registration made in invalid state " + game.getStatus());
			return;
		}
		
		game.setCreated(new Date());
		
		taskStartInvitation();
	}

	
	public void eventBeginRegistration()
	{
		logInfo("GameState: eventBeginRegistration");
		
		if (game.getStatus() != Game.STATUS.INVITING)
		{
			logError("Begin Registration made in invalid state " + game.getStatus());
			return;
		}
		
		
		taskStartGame();
		
		// TODO -- Test this! 
		// check if max players reached here, which is possible in inviting state, and if so
		// start game right away.  Not sure if begin_reg event should be called above.
		// if all players responded then start game
		if (game.getAreAllPlayersRegistered()) {
			// log as warning because I dont think we really want this to happen very often.
			logger.warn("Staring game at registration because enough players already");
			eventStart();
		} 
		else {
			actionHandler(Actions.ACTION_BEGIN_REGISTRATION);
		}
			
	}

	private long getDays(long ms) {
		return ms / (1000 * 60 * 60 * 24);
	}
	
	public void eventDailyTick(Date now) {
		long nowMs = now.getTime();
		logInfo("GameState: eventReminder " + now);

		switch (game.getStatus()) {
			case ACTIVE:
					Round currentRound = game.getCurrentRound();
					long daysWaiting; 
					if (currentRound.getQuestion() == null) {
						daysWaiting = getDays(nowMs - currentRound.getStartDate().getTime());
						if (daysWaiting >= 1) {
							actionHandler(Actions.ACTION_REMINDER_QUESTION);
						}
					}
					else {
						daysWaiting = getDays(nowMs - currentRound.getQuestionMadeDate().getTime()); 
						if (daysWaiting >= 1) {
							actionHandler(Actions.ACTION_REMINDER_ANSWER);
						}
					}
					
					if (daysWaiting >= 2) {
						// don't send to master if master was notified above for question/answer
						Player master = game.getMasterPlayer();
						if (!master.getIsActionRequired()) {
							actionHandler(Actions.ACTION_REMINDER_MASTER);
						}
					}
	
					break;

			case REGISTERING:

					// players still joining
					long daysSinceFirstInvite = getDays(nowMs - game.getCreated().getTime()); 
					logger.info("Remind Players Joining Day " + daysSinceFirstInvite +" countdown=" + game.getCountDown());
					if (daysSinceFirstInvite > 0) {
						if (game.getCountDown()<=0) {
							eventStart();
						}
						else {
							if (game.getCountDown()==1) { 
								actionHandler(Actions.ACTION_REMINDER_JOIN_LAST_CHANCE);
							} else {
								actionHandler(Actions.ACTION_REMINDER_JOIN);
							}
							game.setCountDown(game.getCountDown()-1);
						}
					}
				break;
			default:
				break;

		}
}

	/*
	 * ACTIONS
	 */

	private void actionHandler(Actions action)
	{
		logInfo("GameState: actionHandler " + action.toString());
		gameStateActionHandler.gameStateActionHandler(action, game);	
	}
}
