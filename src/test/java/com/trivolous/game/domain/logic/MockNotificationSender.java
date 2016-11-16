package com.trivolous.game.domain.logic;

import com.trivolous.game.domain.Game;
import com.trivolous.game.domain.GameComment;
import com.trivolous.game.domain.Member;
import com.trivolous.game.domain.Player;
import com.trivolous.game.domain.Round;
import com.trivolous.game.notify.NotificationSenderInterface;

public class MockNotificationSender implements NotificationSenderInterface {

	public int questionIsReadyCount = 0;
	public int reminderStatusCount = 0;
	public int roundResultsCount = 0;
	public int inviteCount = 0;
	public int startCount = 0;
	public int roundResultsAndQueuedCount = 0;
	public int gameOverCount = 0;
	
	@Override
	public void sendQuestionIsReadyNotice(Game game) {

		questionIsReadyCount++;
	}

	@Override
	public void sendRoundResults(Round round) {
		roundResultsCount++;
	}

	@Override
	public void sendGameComment(GameComment gameComment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendQuestionWasQueuedNotice(Game game) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendRegistrationComment(GameComment gameComment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendPasswordReminder(Member member) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendRoundResultsAndNextQuestionQueued(Round lastRound) {
		// TODO Auto-generated method stub
		roundResultsAndQueuedCount++;
	}

	@Override
	public void sendQuestionComment(Round round, GameComment gameComment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMasterMessage(Player player, String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendInvite(Game game, boolean isReminder, boolean isLastReminder) {
		inviteCount++;
	}

	@Override
	public void sendReminderToAnswer(Game game) {
		reminderStatusCount++;
		
	}

	@Override
	public void sendReminderForQuestion(Game game) {
		reminderStatusCount++;
	}

	@Override
	public void sendReminderToMaster(Game game) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendGameOver(Game game) {
		gameOverCount++;
		
	}

	@Override
	public void sendGameCancelled(Game game) {
		gameOverCount++;
		
	}

	@Override
	public void sendRegistrationAborted(Game game) {
		gameOverCount++;
		
	}

	@Override
	public void sendRegistrationLink(String email, String link) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendWelcome(String email) {
		// TODO Auto-generated method stub
		
	}

}
