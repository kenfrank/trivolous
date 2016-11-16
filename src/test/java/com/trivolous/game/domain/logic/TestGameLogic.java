package com.trivolous.game.domain.logic;


import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.trivolous.game.domain.Game;
import com.trivolous.game.domain.Player;
import com.trivolous.game.domain.Question;
import com.trivolous.game.domain.Round;
import com.trivolous.game.domain.Turn;
import com.trivolous.game.domain.logic.GameLogic;

public class TestGameLogic {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	public void testVector(int p2choice, 
			int p2time, int p3choice, int p3time, 
			Turn.Reason p2reason, int points2, 
			Turn.Reason p3reason, int points3, 
			Turn.Reason p1reason, int points1 ) {
		Game game = new Game();
		Round round = new Round();
		game.getRounds().add(round);
		GameLogic gameLogic = new GameLogic(game);

		Player player1 = new Player();
		Player player2 = new Player();
		Player player3 = new Player();
		player1.setId(1);
		player2.setId(2);
		player3.setId(3);
		player1.setOrder(0);
		player1.setGame(game);
		player2.setOrder(1);
		player2.setGame(game);
		player3.setOrder(2);
		player3.setGame(game);
		player1.setScore(100);
		player2.setScore(100);
		player3.setScore(100);

		player1.setIsActive(true);
		player2.setIsActive(true);
		player3.setIsActive(true);
		
		Question question = new Question();
		question.setAnswer(new Integer(1));
		round.setQuestion(question);
		round.setAsker(player1);
		
		Turn turn1 = new Turn();
		player1.getTurns().add(turn1);
		
		Turn turn2 = new Turn();
		turn2.setChoice(new Integer(p2choice));
		turn2.setTimeToAnswer(p2time);
		player2.getTurns().add(turn2);

		Turn turn3 = new Turn();
		turn3.setChoice(new Integer(p3choice));
		turn3.setTimeToAnswer(p3time);
		player3.getTurns().add(turn3);

		gameLogic.score();

		assertEquals(p1reason, turn1.getReason());
		assertEquals(p2reason, turn2.getReason());
		assertEquals(p3reason, turn3.getReason());

		assertEquals(points1, turn1.getPoints());
		assertEquals(points2, turn2.getPoints());
		assertEquals(points3, turn3.getPoints());

		assertEquals(turn1.getScore().intValue(), 100 + turn1.getPoints());
		assertEquals(turn2.getScore().intValue(), 100 + turn2.getPoints());
		assertEquals(turn3.getScore().intValue(), 100 + turn3.getPoints());
	}

	@Test
	public void testV1() {
		testVector(
				0, 10, 
				0, 10, 
				Turn.Reason.ANSWER_WRONG_WON, 1,
				Turn.Reason.ANSWER_WRONG_WON, 1,
				Turn.Reason.QUESTION_WRONG, -2
				);
		testVector(
				0, 10, 
				1, 10, 
				Turn.Reason.ANSWER_WRONG_LOST, -1,
				Turn.Reason.ANSWER_CORRECT, 1,
				Turn.Reason.QUESTION_RIGHT, 0
				);
		testVector(
				1, 10, 
				0, 10, 
				Turn.Reason.ANSWER_CORRECT, 1, 
				Turn.Reason.ANSWER_WRONG_LOST, -1,
				Turn.Reason.QUESTION_RIGHT, 0
				);
		testVector(
				1, 5, 
				1, 10, 
				Turn.Reason.ANSWER_CORRECT_QUICKEST, 2, 
				Turn.Reason.ANSWER_CORRECT, 1,
				Turn.Reason.QUESTION_RIGHT, -3
				);
		testVector(
				1, 10, 
				1, 5, 
				Turn.Reason.ANSWER_CORRECT, 1,
				Turn.Reason.ANSWER_CORRECT_QUICKEST, 2, 
				Turn.Reason.QUESTION_RIGHT, -3
				);
		// if times the same, then no quickest.
		testVector(
				1, 10, 
				1, 10, 
				Turn.Reason.ANSWER_CORRECT, 1, 
				Turn.Reason.ANSWER_CORRECT, 1,
				Turn.Reason.QUESTION_RIGHT, -2
				);
		
		testVector(
				Turn.GAVEUP, 10, 
				Turn.GAVEUP, 10, 
				Turn.Reason.ANSWER_NONE_WON, 1,
				Turn.Reason.ANSWER_NONE_WON, 1,
				Turn.Reason.QUESTION_NONE, -2
				);
		testVector(
				Turn.TIMEOUT, 10, 
				Turn.TIMEOUT, 10, 
				Turn.Reason.ANSWER_NONE_WON, 1, 
				Turn.Reason.ANSWER_NONE_WON, 1,
				Turn.Reason.QUESTION_NONE, -2
				);
		testVector(
				0, 10, 
				Turn.GAVEUP, 10, 
				Turn.Reason.ANSWER_WRONG_WON, 1, 
				Turn.Reason.ANSWER_NONE, 0,
				Turn.Reason.QUESTION_WRONG, -1
				);
		testVector(
				0, 10, 
				Turn.TIMEOUT, 10, 
				Turn.Reason.ANSWER_WRONG_WON, 1,
				Turn.Reason.ANSWER_NONE, 0,
				Turn.Reason.QUESTION_WRONG, -1
				);
		testVector(
				1, 10, 
				Turn.GAVEUP, 10, 
				Turn.Reason.ANSWER_CORRECT, 1, 
				Turn.Reason.ANSWER_NONE, 0,
				Turn.Reason.QUESTION_RIGHT, -1
				);
		testVector(
				1, 10, 
				Turn.TIMEOUT, 10, 
				Turn.Reason.ANSWER_CORRECT, 1,
				Turn.Reason.ANSWER_NONE, 0,
				Turn.Reason.QUESTION_RIGHT, -1
				);
		testVector(
				Turn.GAVEUP, 10, 
				0, 10, 
				Turn.Reason.ANSWER_NONE, 0,
				Turn.Reason.ANSWER_WRONG_WON, 1, 
				Turn.Reason.QUESTION_WRONG, -1
				);
		testVector(
				Turn.TIMEOUT, 10, 
				0, 10, 
				Turn.Reason.ANSWER_NONE, 0,
				Turn.Reason.ANSWER_WRONG_WON, 1,
				Turn.Reason.QUESTION_WRONG, -1
				);
		testVector(
				Turn.GAVEUP, 10, 
				1, 10, 
				Turn.Reason.ANSWER_NONE, 0,
				Turn.Reason.ANSWER_CORRECT, 1, 
				Turn.Reason.QUESTION_RIGHT, -1
				);
		testVector(
				Turn.TIMEOUT, 10, 
				1, 10, 
				Turn.Reason.ANSWER_NONE, 0, 
				Turn.Reason.ANSWER_CORRECT, 1,
				Turn.Reason.QUESTION_RIGHT, -1
				);
	}
	
}
