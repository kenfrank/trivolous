package com.trivolous.game.domain.logic;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trivolous.game.domain.Game;
import com.trivolous.game.domain.Player;
import com.trivolous.game.domain.Turn;
import com.trivolous.game.domain.Turn.Reason;

public class GameLogic {

	private Game game;
	protected final Log logger = LogFactory.getLog(getClass());
	
	
	public GameLogic(Game game)
	{
		this.game = game; 
	}


	public int getActivePlayerCount()
	{
		int count = 0;
		for (Player p : game.getPlayers())
		{
			if (p.getIsActive() == true) count+=1;
		}
		return count;
	}

	public Player getNextAsker()
	{
	
		
		int lastOrder = game.getLastCompletedRound().getAsker().getOrder();
		int newOrder = 99999;
		Player nextPlayer = null;
		Player firstPlayer = null;

		logger.debug("set next asker, last order = " + lastOrder);
		
		// find player with next higher order (may be more than one away)
		for (Player p : game.getActivePlayers())
		{
			// 
			if (p.getOrder() > lastOrder)
			{
				if (p.getOrder() < newOrder)
				{
					newOrder = p.getOrder();
					nextPlayer = p;
					logger.debug("set next asker, possible? order = " + newOrder);
				}
			}
			
			// the master is always the first player and cannot be removed.
			if (p.getOrder() == 1) firstPlayer = p;
		}
		
		if (nextPlayer == null)
		{
			logger.debug("set next asker, set back to first player");
			// all players have made turn.  Go back to first player, which is always the master.
			nextPlayer = firstPlayer;
		}

		logger.debug("set next asker, next asker = " + nextPlayer.getName());
		
		return nextPlayer;
		
	}


	private void settle(Player to, Player from, int amount) {
		int available = Math.min(from.getScore(), amount);
		to.getLastTurn().setPoints(	to.getLastTurn().getPoints() + available);
		from.getLastTurn().setPoints(	from.getLastTurn().getPoints() - available);
	}

	private void settleB(Player to, int amount) {
		to.getLastTurn().setPoints(	to.getLastTurn().getPoints() + amount);
	}


	private enum AnswerResult { 
		RIGHT, WRONG, GAVEUP
	}

	private class Answerer {
		public Player player;
		AnswerResult result;
	}
	
	public void scoreX()
	{
		int numCorrect = 0;
		int numWrong = 0;
		long minTime = 9999;
		long minCount = 0;
		
		ArrayList<Answerer> answerers = new ArrayList<Answerer>();
		Player asker = null;
		// calculate number correct/wrong and minimum time to answer
		// and make answerer list 
		for (Player p : game.getActivePlayers())
		{
			Turn turn = p.getLastTurn();
			turn.setPoints(0); // default to 0.  especially for asker, which uses this as talley.
			if (p.getIsAsker()) {
				asker = p;
			} else {
				AnswerResult result;
				int choice = turn.getChoice();
				if (choice == game.getQuestion().getAnswer()) {
					result = AnswerResult.RIGHT;
					numCorrect++;
					if (turn.getTimeToAnswer() < minTime) {
						minTime = turn.getTimeToAnswer();
						minCount = 1;
					}
					else if (turn.getTimeToAnswer() == minTime) {
						minCount++;
					}
				}
				else if (choice == Turn.GAVEUP || 
						 choice == Turn.TIMEOUT) {
					result = AnswerResult.GAVEUP;
				}
				else {
					result = AnswerResult.WRONG;
					numWrong++;
				}
				Answerer answerer = new Answerer();
				answerer.player = p;
				answerer.result = result;
				answerers.add(answerer);
			}
		}
		
		// TODO -- sort answerers to pay out in order answered?
		if (numCorrect >= 1) {
			asker.getLastTurn().setReason(Reason.QUESTION_RIGHT);
			// Some right
			// set asker reason to 'Question had some right answers. Asker matches right bets, and gets wrong bets.'
			for (Answerer a : answerers) {
				switch (a.result) {
					case RIGHT:
					{
						// asker matches player's bet
						// if first to answer, and more than one answer, then double bet!
						// but if 2 or more players answered in least time, then none quickest.
						int bet = a.player.getLastTurn().getBet();
						if (numCorrect > 1 &&
							minCount == 1 &&	
							a.player.getLastTurn().getTimeToAnswer() == minTime) {
								bet *= 2;
								a.player.getLastTurn().setReason(Reason.ANSWER_CORRECT_QUICKEST);
						}
						else {
							a.player.getLastTurn().setReason(Reason.ANSWER_CORRECT);
						}
						settle(a.player, asker, bet);
						break;
					}
					case WRONG:
						// player gives bet to asker
						settle(asker, a.player, a.player.getLastTurn().getBet());
						a.player.getLastTurn().setReason(Reason.ANSWER_WRONG_LOST);
						break;
					case GAVEUP:
						// player keeps bet.
						a.player.getLastTurn().setReason(Reason.ANSWER_NONE);
						break;
				}
			}
		}
		else if (numWrong >= 1) {
			asker.getLastTurn().setReason(Reason.QUESTION_WRONG);
			// None right
			// set asker reason to 'Question had no right answers. Asker matches wrong bets.'
			for (Answerer a : answerers) {
				switch (a.result) {
					case WRONG:
						// asker matches player's bet
						settle(a.player, asker, a.player.getLastTurn().getBet());
						// reason = won bet with wrong answer
						a.player.getLastTurn().setReason(Reason.ANSWER_WRONG_WON);
						break;
					case GAVEUP:
						// player keeps bet.
						a.player.getLastTurn().setReason(Reason.ANSWER_NONE);
						break;
					case RIGHT:
						break;
					default:
						break;
				}
			}
			
		}
		else {
			asker.getLastTurn().setReason(Reason.QUESTION_NONE);
			// All gave up
			// set asker reason to 'Question had no answers. Asker matches all bets.'
			for (Answerer a : answerers) {
				// asker matches player's bet
				settle(a.player, asker, a.player.getLastTurn().getBet());
				a.player.getLastTurn().setReason(Reason.ANSWER_NONE_WON);
			}
		}

		// Set score of all players.
		for (Player p : game.getActivePlayers()) {
			p.adjustScore(p.getLastTurn().getPoints() ); 
			p.getLastTurn().setScore(p.getScore());
		}
	}

	public void score()
	{
		int numCorrect = 0;
		int numWrong = 0;
		long minTime = 9999;
		long minCount = 0;
		
		ArrayList<Answerer> answerers = new ArrayList<Answerer>();
		Player asker = null;
		// calculate number correct/wrong and minimum time to answer
		// and make answerer list 
		for (Player p : game.getActivePlayers())
		{
			Turn turn = p.getLastTurn();
			turn.setPoints(0); // default to 0.  especially for asker, which uses this as talley.
			if (p.getIsAsker()) {
				asker = p;
			} else {
				AnswerResult result;
				int choice = turn.getChoice();
				if (choice == game.getQuestion().getAnswer()) {
					result = AnswerResult.RIGHT;
					numCorrect++;
					if (turn.getTimeToAnswer() < minTime) {
						minTime = turn.getTimeToAnswer();
						minCount = 1;
					}
					else if (turn.getTimeToAnswer() == minTime) {
						minCount++;
					}
				}
				else if (choice == Turn.GAVEUP || 
						 choice == Turn.TIMEOUT) {
					result = AnswerResult.GAVEUP;
				}
				else {
					result = AnswerResult.WRONG;
					numWrong++;
				}
				Answerer answerer = new Answerer();
				answerer.player = p;
				answerer.result = result;
				answerers.add(answerer);
			}
		}
		
		// TODO -- sort answerers to pay out in order answered?
		if (numCorrect >= 1) {
			asker.getLastTurn().setReason(Reason.QUESTION_RIGHT);
			// Some right
			// set asker reason to 'Question had some right answers. Asker matches right bets, and gets wrong bets.'
			for (Answerer a : answerers) {
				switch (a.result) {
					case RIGHT:
					{
						// player gets bet, plus 5 if first
						int bet = a.player.getLastTurn().getBet();
						if (numCorrect > 1 &&
							minCount == 1 &&	
							a.player.getLastTurn().getTimeToAnswer() == minTime) {
								bet += 5;
								a.player.getLastTurn().setReason(Reason.ANSWER_CORRECT_QUICKEST);
						}
						else {
							a.player.getLastTurn().setReason(Reason.ANSWER_CORRECT);
						}
						settleB(a.player, bet);
						break;
					}
					case WRONG:
						// player gives bet to asker
						settleB(asker, a.player.getLastTurn().getBet());
						a.player.getLastTurn().setReason(Reason.ANSWER_WRONG_LOST);
						break;
					case GAVEUP:
						// player keeps bet.
						a.player.getLastTurn().setReason(Reason.ANSWER_NONE);
						break;
				}
			}
		}
		else if (numWrong >= 1) {
			asker.getLastTurn().setReason(Reason.QUESTION_WRONG);
			// None right
			// set asker reason to 'Question had no right answers. Asker matches wrong bets.'
			for (Answerer a : answerers) {
				switch (a.result) {
					case WRONG:
						// asker matches player's bet
						settleB(a.player, a.player.getLastTurn().getBet());
						// reason = won bet with wrong answer
						a.player.getLastTurn().setReason(Reason.ANSWER_WRONG_WON);
						break;
					case GAVEUP:
						// player keeps bet.
						a.player.getLastTurn().setReason(Reason.ANSWER_NONE);
						break;
					case RIGHT:
						break;
					default:
						break;
				}
			}
			
		}
		else {
			asker.getLastTurn().setReason(Reason.QUESTION_NONE);
			// All gave up
			// set asker reason to 'Question had no answers. Asker matches all bets.'
			for (Answerer a : answerers) {
				// asker matches player's bet
				settleB(a.player, a.player.getLastTurn().getBet());
				a.player.getLastTurn().setReason(Reason.ANSWER_NONE_WON);
			}
		}

		// Set score of all players.
		for (Player p : game.getActivePlayers()) {
			p.adjustScore(p.getLastTurn().getPoints() ); 
			p.getLastTurn().setScore(p.getScore());
		}
	}
	
	
}
