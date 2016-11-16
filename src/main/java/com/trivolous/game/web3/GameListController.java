package com.trivolous.game.web3;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.trivolous.game.data.GameDescriptionData;
import com.trivolous.game.data.GameList;
import com.trivolous.game.domain.logic.NewGameService;
import com.trivolous.game.domain.logic.PlayerService;
import com.trivolous.game.domain.logic.PlayerService.InviteStatus;
import com.trivolous.game.domain.logic.RegistrationKey;

@Controller
@RequestMapping(value="member")
public class GameListController {
	protected final Log logger = LogFactory.getLog(getClass());
	@Autowired
	private NewGameService gameService;
	@Autowired
	private PlayerService playerService;
	final static String OPEN_REG_COOKIE_NAME = "regkey";
	

	@RequestMapping(value="joinGameToken")
	@ResponseBody
	public String registerWithToken(HttpServletRequest request,
			HttpServletResponse response,
			String key) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");

		RegistrationKey rk = new RegistrationKey();
		boolean openRegFailed = false;
		if (key != null) {
			Long gameId = rk.keyToGameId(key);
			logger.info("found openreg cookie for member " + userSession.getMemberId());
			PlayerService.InviteStatus status = 
					playerService.inviteMember(gameId, userSession.getMemberId());
			if (status != InviteStatus.SUCCESS) {
				openRegFailed = true;
			}
		}
		return openRegFailed ? "failed" : "success";
	}
	
	
	
	@RequestMapping(value="gameList")
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		ModelAndView mav = new ModelAndView("member/gameList");

		RegistrationKey rk = new RegistrationKey();
		String key = rk.findAndRemoveKeyCookie(request, response);
		boolean openRegFailed = false;
		if (key != null) {
			Long gameId = rk.keyToGameId(key);
			logger.info("found openreg cookie for member " + userSession.getMemberId());
			PlayerService.InviteStatus status = 
					playerService.inviteMember(gameId,userSession.getMemberId());
			if (status != InviteStatus.SUCCESS) {
				openRegFailed = true;
				GameDescriptionData failedGame = gameService.getGameDescription(gameId);
				mav.addObject("openRegFailedGame", failedGame);
				mav.addObject("openRegFailedReason", status);
			}
		}
		
		GameList gameList = gameService.getGameList(userSession.getMemberId());
		
		mav.addObject("openRegFailed", openRegFailed);
		mav.addObject("invitesGameList",gameList.getInvitedGames());
		mav.addObject("activeGameList",gameList.getActiveGames());
		mav.addObject("completedGameList",gameList.getCompletedGames());
		mav.addObject("publicGameList",gameList.getPublicGames());
		
		return mav;
	}
	
	@RequestMapping(value="inviteResponse", method=RequestMethod.POST)
	public String inviteResponse(@RequestParam("gameId") long gameId, HttpServletRequest request) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		boolean wasAccepted = (request.getParameterMap().containsKey("accept"));			
		playerService.registrationResponse(userSession.getMemberId(), gameId, wasAccepted);
		if (wasAccepted) {
			userSession.setGameId(gameId);
			return "redirect:/game/game";
		}
		else {
			return "redirect:/member/gameList";
		}
	}

}