package com.trivolous.game.domain.logic;


import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.trivolous.game.domain.Game;
import com.trivolous.game.domain.Invite;
import com.trivolous.game.domain.Member;
import com.trivolous.game.domain.Player;
import com.trivolous.game.domain.Question;
import com.trivolous.game.domain.QueuedQuestion;
import com.trivolous.game.domain.Game.STATUS;
import com.trivolous.game.domain.logic.GameState;
import com.trivolous.game.domain.logic.GameState.Actions;
import com.trivolous.game.domain.logic.GameState.GameStateActionHandler;

public class TestGameState {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	class TestHandler implements GameStateActionHandler {

		Actions lastAction = null;
		Actions lastAction2 = null;
		int actionCount = 0;
		@Override
		public void gameStateActionHandler(Actions action, Game game) {
			lastAction2 = lastAction;
			lastAction = action;
			actionCount++;
		}
		
	}

	@Test
	public void testInitialize() {
		Game game = createGame();
		TestHandler testHandler = new TestHandler();
		GameState gameState = new GameState(game, testHandler);
		
		gameState.eventInitialize();
		
		assertEquals(game.getStatus(),STATUS.CREATING);
	}
	
	
	@Test
	public void testInitializeThenCancel() {
		Game game = createGame();
		TestHandler testHandler = new TestHandler();
		GameState gameState = new GameState(game, testHandler);
		invite(game);
		invite(game);
		invite(game);
		// queue master question
		queueQuestion(game.getPlayers().get(0));		
		gameState.eventInitialize();
		gameState.eventCancelGame();
		assertEquals(game.getStatus(),STATUS.COMPLETE);
		assertEquals(testHandler.lastAction, Actions.ACTION_CANCELLED);
	}
	
	@Test
	public void testJoinThenCancel() {
		Game game = createGame();
		TestHandler testHandler = new TestHandler();
		GameState gameState = new GameState(game, testHandler);
		invite(game);
		invite(game);
		invite(game);
		// queue master question
		queueQuestion(game.getPlayers().get(0));		
		gameState.eventInitialize();
		gameState.eventBeginInvitation();
		gameState.eventBeginRegistration();
		gameState.eventCancelGame();
		assertEquals(game.getStatus(),STATUS.COMPLETE);
		assertEquals(testHandler.lastAction, Actions.ACTION_CANCELLED);
	}

	Game createGame() {
		Game game = new Game();
		Member member = new Member();
		member.setId(1);
		member.setFirstName("Mamber#"+1);
		game.setMaster(member);
		Player player = new Player();
		// player will be attached to game when set below (no need to add to list manually)
		player.setGame(game);
		player.setMember(member);
		player.setOrder(1);
		player.setId(1);
		player.setName("Master");
		return game;
	}
	void invite(Game game) {
		int num = game.getInvites().size();
		Invite invite = new Invite();
		invite.setId(num);
		game.getInvites().add(invite);
	}
	
	void join(Game game, int index, boolean response) {
		game.getInvites().get(index).setHasJoined(response);
		if (response) {
			Player player = new Player();
			// player will be attached to game when set below (no need to add to list manually)
			player.setGame(game);
			player.setName("Player");
		}
	}
	
	@Test
	public void testJoinThenStartWith2Yes() {
		Game game = createGame();
		TestHandler testHandler = new TestHandler();
		GameState gameState = new GameState(game, testHandler);
		invite(game);
		invite(game);
		invite(game);
		// queue master question
		queueQuestion(game.getPlayers().get(0));		
		gameState.eventInitialize();
		gameState.eventBeginInvitation();
		gameState.eventBeginRegistration();
		join(game,0,true);
		gameState.eventInviteResponse();
//		join(game,1,true);
//		gameState.eventInviteResponse();
		gameState.eventStart();
		assertEquals(STATUS.COMPLETE,game.getStatus());
		assertEquals(Actions.ACTION_CANCELLED_NOT_ENOUGH_PLAYERS, testHandler.lastAction);
	}
	
	void queueQuestion(Player player) {
		Question q = new Question();
		q.setId(0);
		List<QueuedQuestion> qqs = player.getQueuedQuestions();
		QueuedQuestion qq = new QueuedQuestion();
		qq.setRank(qqs.size()+ 1);  // start rank at 1
		qq.setPlayer(player);
		qq.setQuestion(q);		
	}
	
	@Test
	public void testJoinThenStartWith3YesOf4() {
		Game game = createGame();
		TestHandler testHandler = new TestHandler();
		GameState gameState = new GameState(game, testHandler);
		invite(game);
		// queue master question
		queueQuestion(game.getPlayers().get(0));
		invite(game);
		invite(game);
		invite(game);
		gameState.eventInitialize();
		gameState.eventBeginInvitation();
		gameState.eventBeginRegistration();
		assertEquals(true, game.getIsActive());
		assertEquals(STATUS.REGISTERING,game.getStatus());
		join(game,0,true);
		gameState.eventInviteResponse();
		join(game,1,true);
		gameState.eventInviteResponse();
		join(game,2,false);
		gameState.eventInviteResponse();
		gameState.eventStart();
		assertEquals(true, game.getIsActive());
		assertEquals(STATUS.ACTIVE,game.getStatus());
		assertEquals(Actions.ACTION_END_REGISTRATION, testHandler.lastAction );
		gameState.eventCancelGame();
		assertEquals(game.getStatus(),STATUS.COMPLETE);
		assertEquals(Actions.ACTION_CANCELLED, testHandler.lastAction);
		
	}
	@Test
	public void testJoinThenStartWith3YesOf3() {
		Game game = createGame();
		game.setAllowOpenRegistration(false);
		TestHandler testHandler = new TestHandler();
		GameState gameState = new GameState(game, testHandler);
		invite(game);
		// queue master question
		queueQuestion(game.getPlayers().get(0));
		invite(game);
		invite(game);
		gameState.eventInitialize();
		gameState.eventBeginInvitation();
		gameState.eventBeginRegistration();
		assertEquals(game.getStatus(),STATUS.REGISTERING);
		join(game,0,true);
		gameState.eventInviteResponse();
		join(game,1,true);
		gameState.eventInviteResponse();
		join(game,2,true);
		gameState.eventInviteResponse();
		assertEquals(game.getStatus(),STATUS.ACTIVE);
		assertEquals(Actions.ACTION_END_REGISTRATION, testHandler.lastAction );
		gameState.eventCancelGame();
		assertEquals(game.getStatus(),STATUS.COMPLETE);
		assertEquals(testHandler.lastAction, Actions.ACTION_CANCELLED);
	}
		
	// TODO - 3 answered then start -- should end round.  no questions answered in these test, so try this is other tests
	// TODO - 3 answered 1 No -- should end round
	
	
	
	
	@Test
	public void testJoinThenCancelledWith1Yes1No() {
		Game game = createGame();
		game.setAllowOpenRegistration(false);
		TestHandler testHandler = new TestHandler();
		GameState gameState = new GameState(game, testHandler);
		// queue master question
		queueQuestion(game.getPlayers().get(0));
		invite(game);
		invite(game);
		gameState.eventInitialize();
		gameState.eventBeginInvitation();
		gameState.eventBeginRegistration();
		join(game,0,true);
		gameState.eventInviteResponse();
		join(game,1,false);
		gameState.eventInviteResponse();
		assertEquals(game.getStatus(),STATUS.COMPLETE);
		assertEquals(testHandler.lastAction, Actions.ACTION_CANCELLED_NOT_ENOUGH_PLAYERS);
	}
	
	private Game setupActiveGame() {
		Game game = createGame();
		game.setAllowOpenRegistration(false);
		TestHandler testHandler = new TestHandler();
		GameState gameState = new GameState(game, testHandler);
		int numPlayers = 5;
		for (int i=0; i<numPlayers; ++i) {
			invite(game);
		}	
		// queue master question
		queueQuestion(game.getPlayers().get(0));
		gameState.eventInitialize();
		gameState.eventBeginInvitation();
		gameState.eventBeginRegistration();
		for (int i=0; i<numPlayers; ++i) {
			join(game,i,true);
			gameState.eventInviteResponse();
		}
		return game;
	}

	@Test
	public void testJoinNoReminder() {
		Game game = createGame();
		TestHandler testHandler = new TestHandler();
		GameState gameState = new GameState(game, testHandler);
		invite(game);
		// queue master question
		queueQuestion(game.getPlayers().get(0));
		invite(game);
		invite(game);
		gameState.eventInitialize();
		gameState.eventBeginInvitation();
		gameState.eventBeginRegistration();

		// make sure no reminder sent
		int count = testHandler.actionCount;
		gameState.eventDailyTick(game.getCreated());
		assertEquals(count, testHandler.actionCount);
		assertEquals(new Integer(2), game.getCountDown());

		Date now = addDays(game.getCreated(), 1);
		gameState.eventDailyTick(now);
		assertEquals(Actions.ACTION_REMINDER_JOIN, testHandler.lastAction);
		assertEquals(new Integer(1), game.getCountDown());
		
		Date now2 = addDays(game.getCreated(), 2);
		gameState.eventDailyTick(now2);
		assertEquals(Actions.ACTION_REMINDER_JOIN_LAST_CHANCE, testHandler.lastAction);
		assertEquals(new Integer(0), game.getCountDown());
		
		Date now3 = addDays(game.getCreated(), 3);
		gameState.eventDailyTick(now3);
		assertEquals(Actions.ACTION_CANCELLED_NOT_ENOUGH_PLAYERS, testHandler.lastAction);
		
	}
	
	private Date addDays(Date d, long days) {
		return new Date(d.getTime() + days * (24 * 60 * 60 * 1000));
	}
	
	@Test
	public void testActiveReminders() {
		Game game = setupActiveGame();
		TestHandler testHandler = new TestHandler();
		GameState gameState = new GameState(game, testHandler);

		// make sure no reminder sent
		int count = testHandler.actionCount;
		gameState.eventDailyTick(game.getCreated());
		assertEquals(count, testHandler.actionCount);

		Date now = addDays(game.getCurrentRound().getStartDate(), 1);
		gameState.eventDailyTick(now);
		assertEquals(Actions.ACTION_REMINDER_ANSWER, testHandler.lastAction);
		assertEquals(++count, testHandler.actionCount);

 		
		Date now2 = addDays(game.getCurrentRound().getStartDate(), 3);
		gameState.eventDailyTick(now2);
		assertEquals(Actions.ACTION_REMINDER_MASTER, testHandler.lastAction);
		assertEquals(Actions.ACTION_REMINDER_ANSWER, testHandler.lastAction2);
		assertEquals(count+2, testHandler.actionCount);
	}

	// test if max players join in invited state
	//    game should not start, until transition to registering, then go right to started.
	
	// test answer in invited state
	//   should not end round if all players answer  (probably similar test for registering)
	
	
	
	
}
