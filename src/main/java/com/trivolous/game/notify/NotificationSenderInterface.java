package com.trivolous.game.notify;

import com.trivolous.game.domain.Game;
import com.trivolous.game.domain.GameComment;
import com.trivolous.game.domain.Member;
import com.trivolous.game.domain.Player;
import com.trivolous.game.domain.Round;

public interface NotificationSenderInterface {

	public abstract void sendQuestionIsReadyNotice(Game game);

	public abstract void sendRoundResults(Round round);
	
	public abstract void sendGameComment(GameComment gameComment);
	
	public abstract void sendQuestionWasQueuedNotice(Game game);

	public abstract void sendGameOver(Game game);

	public abstract void sendRegistrationComment(GameComment gameComment);

	public abstract void sendPasswordReminder(Member member);

	public abstract void sendRoundResultsAndNextQuestionQueued(Round lastRound);

	public abstract void sendQuestionComment(Round round, GameComment gameComment);

	public abstract void sendMasterMessage(Player player, String text);

	public abstract void sendInvite(Game game, boolean isReminder, boolean isLastReminder);

	public abstract void sendReminderToAnswer(Game game);
	public abstract void sendReminderForQuestion(Game game);
	public abstract void sendReminderToMaster(Game game);

	public abstract void sendGameCancelled(Game game);

	public abstract void sendRegistrationAborted(Game game);

	public abstract void sendRegistrationLink(String email, String link);

	public abstract void sendWelcome(String email);

}