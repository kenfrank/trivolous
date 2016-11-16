package com.trivolous.game.web3;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.WebUtils;

import com.trivolous.game.data.PlayerData;
import com.trivolous.game.data.QuestionData;
import com.trivolous.game.data.TurnForm;
import com.trivolous.game.domain.Player;
import com.trivolous.game.domain.Turn;
import com.trivolous.game.domain.logic.NewGameService;
import com.trivolous.game.domain.logic.PlayerService;

/**
 */
@Controller
@RequestMapping(value="game/answer")
public class AnswerController {
	protected final Log logger = LogFactory.getLog(getClass());
	@Autowired
	private PlayerService playerService;	
	@Autowired
	private NewGameService gameService;
	
	@RequestMapping(method=RequestMethod.GET)
	public String handleGetRequest(
			@RequestParam("bet")int bet, HttpServletRequest request, ModelMap model) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		PlayerData player = playerService.findPlayer(userSession.getGameId(), userSession.getMemberId());
		logger.info("Answer shown for player id = " + player.getId() + " bet=" + bet);
		if (player.getStatus() != Player.Status.NEEDS_TO_ANSWER && player.getStatus() != Player.Status.ANSWERING ) {
			logger.error("Invalid state for answer. id" + player.getId() + "status = " + player.getStatus());
			// TODO -- redirect here if this is working ok.  beware I had this in game interceptor and I had some false positives.
		}
		QuestionData q = playerService.startTimedAnswerSession(player.getId(), bet);
		TurnForm tf = new TurnForm();
		tf.setSecondsLeft(q.getTimeout());
		tf.setAnswer(Turn.GAVEUP);  // default to gave up
		model.addAttribute("command",tf);
		model.addAttribute("question",q);
		return "/game/answer";		
	}

	@RequestMapping(method=RequestMethod.POST)
	protected String onSubmit(HttpServletRequest request, 
			@ModelAttribute TurnForm tf, 
			ModelMap model)
			throws Exception {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		PlayerData player = playerService.findPlayer(userSession.getGameId(), userSession.getMemberId());
		logger.info("Submit answer for player id = " + player.getId() + " answer =" + tf.getAnswer());
		boolean wasCorrect = playerService.submitAnswer(player.getId(), tf);
		userSession.getTemps().put("answered", wasCorrect ? "right" : "wrong");
		return "redirect:/game/game";
	}
	
	
	
}
