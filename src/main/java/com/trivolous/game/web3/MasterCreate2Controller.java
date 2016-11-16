package com.trivolous.game.web3;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.util.WebUtils;

import com.trivolous.game.data.PlayerData;
import com.trivolous.game.data.QuestionData;
import com.trivolous.game.data.TurnForm;
import com.trivolous.game.domain.Turn;
import com.trivolous.game.domain.logic.ImageService;
import com.trivolous.game.domain.logic.NewGameService;
import com.trivolous.game.domain.logic.PlayerService;

/**
 */
@Controller
@RequestMapping(value="game/create")
public class MasterCreate2Controller {

	@Autowired
	private NewGameService gameService;
	@Autowired
	private PlayerService playerService;	
	@Autowired
	private ImageService imageService;	
	protected final Log logger = LogFactory.getLog(getClass());

	@RequestMapping(value="/make", method=RequestMethod.GET)
	public String makeGet(HttpServletRequest request, ModelMap model, SessionStatus status) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		logger.info("masterCreate2 shown for member id = " + userSession.getMemberId());

		QuestionData q = (QuestionData) WebUtils.getSessionAttribute(request, "qsess");
		
		if (q != null) {
			logger.debug("Got question from session");
			Boolean isVerified = null;
			try {
				isVerified = ServletRequestUtils.getBooleanParameter(request, "verified");
			} catch (ServletRequestBindingException e) {
				logger.warn("make question session entered without verified? Clear session");
			}
			if (isVerified != null) {
				model.addAttribute("verified", isVerified);
			}
			else {
				// has session, but not an edit (no verified flag).  Clear session and go through normal processing
				q=null;
			}
		}		

		if (q == null) {
			PlayerData player = playerService.findPlayer(userSession.getGameId(), userSession.getMemberId());
			List<QuestionData> qqs = playerService.getQueuedQuestions(player.getId());
			if (qqs.size() > 0) {
				q = qqs.get(0);
				logger.debug("Got queued question.");
			}
			else {
				logger.debug("Creating question as none in session or id.");
				q = new QuestionData();
			}
		}
		model.addAttribute("question", q);
		logger.debug("================> qid="+q.getId());		
		return "game/masterCreate2"; 
	}


	@RequestMapping(value="/make", method=RequestMethod.POST)
	protected String makePost(
			HttpServletRequest request, HttpServletResponse response, 
			QuestionData q, 
			ModelMap model)
			throws Exception {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		PlayerData player = playerService.findPlayer(userSession.getGameId(), userSession.getMemberId());

		logger.debug("makequestion post playerid="+player.getId()+" qid="+q.getId()+" imageId="+q.getImageId());
		
		logger.debug("================> qid="+q.getId());		
		request.getSession().setAttribute("qsess", q);
	
		return "redirect:preview";
	}
	
	
	@RequestMapping(value="/preview", method=RequestMethod.GET)
	protected String previewGet( HttpServletRequest request, 
			ModelMap model) {
		QuestionData q = (QuestionData) WebUtils.getSessionAttribute(request, "qsess");
		if (q == null) {
			logger.error("No session in preview get!");
			return "redirect:make";
		} 
		else {
			TurnForm tf = new TurnForm();
			tf.setSecondsLeft(q.getTimeout());
			tf.setAnswer(Turn.GAVEUP);  // default to gave up
			model.addAttribute("command",tf);
			model.addAttribute("question",q);
			logger.error("================> qid="+q.getId());		
			return "/game/answer";
		}
	}
	
	@RequestMapping(value="/preview", method=RequestMethod.POST)
	protected String previewPost(HttpServletRequest request, 
			@ModelAttribute TurnForm tf, 
			ModelMap model)
			throws Exception {
		QuestionData q = (QuestionData) WebUtils.getSessionAttribute(request, "qsess");
		if (q == null) {
			logger.error("No session in preview post!");
			return "redirect:make";
		} 		
		logger.debug("================> qid="+q.getId());

		boolean isTimeout = (tf.getSecondsLeft() <= 0);
		if (isTimeout) tf.setAnswer(Turn.TIMEOUT);

		boolean isRight = false;
		if (q.getAnswer().equals(tf.getAnswer())) isRight=true;
		
		logger.debug("Question preview id="+q.getId()+" isRight="+isRight);
		return "redirect:make?verified="+isRight; 
	}

	@RequestMapping(value="/submit")
	protected String submit(HttpServletRequest request,
			SessionStatus status
			) throws ServletRequestBindingException {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		PlayerData player = playerService.findPlayer(userSession.getGameId(), userSession.getMemberId());
		QuestionData q = (QuestionData) WebUtils.getSessionAttribute(request, "qsess");
		if (q == null) {
			logger.error("No session in preview post!");
			return "redirect:make";
		} 		
		logger.debug("================> qid="+q.getId());
		playerService.submitQuestion(player.getId(), q);
		// allow invitations to be sent now... question cannot be changed after this.
		gameService.beginGameInvitation(userSession.getGameId());
		request.getSession().removeAttribute("qsess");
		return "redirect:/game/create/invite/home";
	}
	
}
