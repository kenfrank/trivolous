package com.trivolous.game.notify;

import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.trivolous.game.domain.DurationFormat;
import com.trivolous.game.domain.Game;
import com.trivolous.game.domain.GameComment;
import com.trivolous.game.domain.Member;
import com.trivolous.game.domain.Player;
import com.trivolous.game.domain.Round;

//@Component
public class NotificationFormatter {
	private VelocityEngine velocityEngine;
	private String baseUrl;

	public class EmailContent {
		public String subject = "";
		public String plainText = "";
		public String htmlText = "";
	};
	
	private EmailContent parse(String veloText) {
		EmailContent emailContent = new EmailContent();
		String split[] = veloText.split("![A-Z]+\r\n");
		emailContent.subject = split[1].trim();
		emailContent.plainText = split[2];
		emailContent.htmlText = split[3];
		
		return emailContent;
	}

	
	private EmailContent template(EmailContent emailContent) {
		emailContent.plainText = plainTemplate(emailContent.plainText);
		emailContent.htmlText = htmlTemplate(emailContent.htmlText);
		return emailContent;
	}

	
	private String htmlTemplate(String content) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("content", content);
		model.put("baseUrl", baseUrl);
		String text = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, "com/trivolous/game/notify/template_html.vm", model);
		return text;
	}
	private String plainTemplate(String content) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("content", content);
		model.put("baseUrl", baseUrl);
		String text = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, "com/trivolous/game/notify/template_plain.vm", model);
		return text;
	}
	

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	public EmailContent formatQuestionIsReady(Game game) {
		assert (game != null);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("game", game);
		model.put("question", game.getQuestion());

		model.put("baseUrl", baseUrl);
		String text = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine,
				"com/trivolous/game/notify/question_is_ready.vm", model);
		return template(parse(text));
	}

	public EmailContent formatQuestionReminderStatus(Game game) {
		assert (game != null);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("game", game);
		String waitString = new DurationFormat(game
				.getSecondsWaiting()).timeAgoString();
		model.put("wait", waitString);
		model.put("baseUrl", baseUrl);
		String text = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, "com/trivolous/game/notify/reminder_question.vm", model);
		return template(parse(text));
	}

	public EmailContent formatAnswerReminderStatus(Game game) {
		assert (game != null);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("game", game);
		String waitString = new DurationFormat(game
				.getSecondsWaiting()).timeAgoString();
		model.put("wait", waitString);
		model.put("baseUrl", baseUrl);
		String text = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, "com/trivolous/game/notify/reminder_answer.vm", model);
		return template(parse(text));
	}

	public EmailContent formatMasterReminderStatus(Game game) {
		assert (game != null);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("game", game);
		String waitString = new DurationFormat(game
				.getSecondsWaiting()).timeAgoString();
		model.put("wait", waitString);
		model.put("baseUrl", baseUrl);
		String text = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, "com/trivolous/game/notify/reminder_master.vm", model);
		return template(parse(text));
	}

	public EmailContent formatGameOver(Game game) {
		assert (game != null);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("game", game);
		model.put("baseUrl", baseUrl);
		String text = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, "com/trivolous/game/notify/game_over.vm", model);
		return template(parse(text));
	}

	public EmailContent formatRoundResults(Round round) {
		assert (round != null);
		Game game = round.getGame();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("game", game);
		model.put("round", round);
		model.put("baseUrl", baseUrl);
		String text = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, "com/trivolous/game/notify/results.vm", model);
		return template(parse(text));
	}

	
	public EmailContent formatRoundResultsAndNextReady(Round lastRound) {
		assert (lastRound != null);
		Game game = lastRound.getGame();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("game", game);
		model.put("round", lastRound);
		model.put("question", game.getQuestion());
		model.put("baseUrl", baseUrl);
		String text = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, "com/trivolous/game/notify/results_and_ready.vm", model);
		return template(parse(text));
	}

	
	
	public EmailContent formatComment(GameComment gameComment) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("comment", gameComment);
		model.put("game", gameComment.getPlayer().getGame());
		model.put("baseUrl", baseUrl);
		String text = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, "com/trivolous/game/notify/comment.vm", model);
		return template(parse(text));

	}

	public EmailContent formatQuestionComment(GameComment gc) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("comment", gc);
		model.put("baseUrl", baseUrl);
		model.put("game", gc.getPlayer().getGame());
		String text = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, "com/trivolous/game/notify/question_comment.vm", model);
		return template(parse(text));
	}

	public EmailContent formatInvite(Game game, boolean isReminder, boolean isLastReminder) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("game", game);
		model.put("baseUrl", baseUrl);
		model.put("reminder_prefix", (isReminder ? 
				(isLastReminder ? "Last Reminder - " : "Reminder - ") : ""));
		String text = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, "com/trivolous/game/notify/game_invite.vm", model);
		return template(parse(text));
	}

	public EmailContent formatGameStart(Game game) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("game", game);
		model.put("baseUrl", baseUrl);
		String text = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, "com/trivolous/game/notify/game_start.vm", model);
		return template(parse(text));
	}

	public EmailContent formatNewInvite(Member member, Game game, boolean isReminder, boolean isLastReminder) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("member", member);
		model.put("game", game);
		model.put("baseUrl", baseUrl);
		model.put("reminder_prefix", (isReminder ? 
				(isLastReminder ? "Last Reminder - " : "Reminder - ") : ""));
		String text = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, "com/trivolous/game/notify/first_time_game_invite.vm", model);
		return template(parse(text));
	}

	public EmailContent formatRemindAutostart(Game game) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("game", game);
		model.put("baseUrl", baseUrl);
		String text = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, "com/trivolous/game/notify/reminder_autostart.vm", model);
		return template(parse(text));
	}

	public EmailContent formatForgotPassword(Member member) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("member", member);
		model.put("baseUrl", baseUrl);
		String text = VelocityEngineUtils.mergeTemplateIntoString(
 				velocityEngine, "com/trivolous/game/notify/forgot_password.vm", model);
		return template(parse(text));
	}

	public EmailContent formatMasterMessage(Player player, String message) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("player", player);
		model.put("message", message);
		model.put("baseUrl", baseUrl);
		model.put("game", player.getGame());
		String text = VelocityEngineUtils.mergeTemplateIntoString(
 				velocityEngine, "com/trivolous/game/notify/master_message.vm", model);
		return template(parse(text));
	}

	public EmailContent formatQuestionQueued(Player asker) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("game", asker.getGame());
		model.put("baseUrl", baseUrl);
		String text = VelocityEngineUtils.mergeTemplateIntoString(
 				velocityEngine, "com/trivolous/game/notify/question_queued.vm", model);
		return template(parse(text));
	}


	public EmailContent formatGameCancelled(Game game) {
		assert (game != null);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("game", game);
		model.put("baseUrl", baseUrl);
		String text = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, "com/trivolous/game/notify/game_cancelled.vm", model);
		return template(parse(text));
	}


	public EmailContent formatRegistrationAborted(Game game) {
		assert (game != null);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("game", game);
		model.put("baseUrl", baseUrl);
		String text = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, "com/trivolous/game/notify/game_aborted.vm", model);
		return template(parse(text));
	}


	public EmailContent formatRegisterLinkMessage(String link) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("link", link);
		model.put("baseUrl", baseUrl);
		String text = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, "com/trivolous/game/notify/register_link.vm", model);
		return template(parse(text));
	}


	public EmailContent formatWelcomeMessage() {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("baseUrl", baseUrl);
		String text = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, "com/trivolous/game/notify/welcome.vm", model);
		return template(parse(text));
	}

}
