package com.trivolous.game.domain.logic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trivolous.game.domain.Game;
import com.trivolous.game.notify.NotificationSenderInterface;

class GameStateActionHandlerImpl implements GameState.GameStateActionHandler
{
	protected final Log logger = LogFactory.getLog(getClass());
	private NotificationSenderInterface notificationSender;
	private NewGameService gameService;
	
	GameStateActionHandlerImpl(NotificationSenderInterface notificationSender, NewGameService gameService)
	{
		this.notificationSender = notificationSender;
		this.gameService = gameService;
	}
	
	@Override
	public void gameStateActionHandler(GameState.Actions action, Game game) {

		logger.info("GameState: actionHandler" + action.toString());
		switch (action)
		{
		case ACTION_BEGIN_REGISTRATION:
			notificationSender.sendInvite(game, false /* not reminder */, false);
			break;
		case ACTION_END_REGISTRATION:
			gameService.removeInactivePlayers(game);
			break;
		case ACTION_END:
			// TODO (med) -- change it so that if master ends game it is a cancel and sends a different
			// email.  Maybe status should even be set to cancelled?  The master should have to
			// give a reason too that is sent with the notice.
			notificationSender.sendGameOver(game);
			break;
		case ACTION_CANCELLED:
			notificationSender.sendGameCancelled(game); // "Master cancelled game before it was completed.");
			break;
		case ACTION_CANCELLED_NOT_ENOUGH_PLAYERS:
			// TODO -- probably should make seperate notification for this.  Note game will be deleted so link wont work.
			notificationSender.sendRegistrationAborted(game); // "Not enough players joined in time so the game was cancelled.");
			gameService.deleteGame(game);
			break;
		case ACTION_QUESTION_READY_MADE:
			notificationSender.sendQuestionIsReadyNotice(game);
			break;
		case ACTION_QUESTION_READY_QUEUED:
			notificationSender.sendRoundResultsAndNextQuestionQueued(
					game.getLastCompletedRound());
			break;
		case ACTION_MAKE_QUESTION:
			notificationSender.sendRoundResults(
					game.getLastCompletedRound());
			break;
		case ACTION_REMINDER_ANSWER: 
			notificationSender.sendReminderToAnswer(game);
			break;
		case ACTION_REMINDER_QUESTION: 
			notificationSender.sendReminderForQuestion(game);
			break;
		case ACTION_REMINDER_MASTER: 
			notificationSender.sendReminderToMaster(game);
			break;
		case ACTION_REMINDER_JOIN_LAST_CHANCE: 
			notificationSender.sendInvite(game, true, true);
			break;
		case ACTION_REMINDER_JOIN:
			notificationSender.sendInvite(game, true, false);
			break;
		}
	}
}
