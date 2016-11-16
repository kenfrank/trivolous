package com.trivolous.game.web3;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import com.trivolous.game.data.GameDescriptionData;
import com.trivolous.game.data.InviteData;
import com.trivolous.game.data.MemberData;
import com.trivolous.game.domain.logic.NewGameService;
import com.trivolous.game.domain.logic.PlayerService;
import com.trivolous.game.domain.logic.RegistrationKey;

/**
 */
@Controller
@RequestMapping(value="game/create/invite")
public class MasterCreate3Controller {
	protected final Log logger = LogFactory.getLog(getClass());
	@Autowired
	private PlayerService playerService;
	@Autowired
	private NewGameService gameService;
	
	@RequestMapping(value="home", method=RequestMethod.GET)
	public String handleGetRequest(Model model, HttpServletRequest request) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		logger.info("masterCreate3 shown for member id = " + userSession.getMemberId());
		GameDescriptionData game = gameService.getGameDescription(userSession.getGameId());
		List<InviteData> invites = playerService.getInvites(userSession.getGameId());
		List<MemberData> fellowPlayers = playerService.findPastPlayers(userSession.getPlayerData().getId());
		model.addAttribute("game", game);
		model.addAttribute("invites", invites); 
		model.addAttribute("friends", fellowPlayers);
		model.addAttribute("regkey", new RegistrationKey().gameIdToKey(userSession.getGameId()));
		return "game/masterCreate3"; 
	}
	
	@RequestMapping(value="home", method=RequestMethod.POST)
	public String startGame(GameDescriptionData newGame, HttpServletRequest request) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		Map pmap = request.getParameterMap();
		if (pmap.containsKey("action_start")) {
			gameService.beginGameRegistration(userSession.getGameId());
			return "redirect:/game/game";
		}
		else
		if (pmap.containsKey("_delete")) {
			gameDelete(request);
			return "redirect:/game/game";
		}
		else {
			return "redirect:home";
		}
	}

    protected void gameDelete(HttpServletRequest request) 
    {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		GameDescriptionData gd = userSession.getGameDesc(); 
		gameService.endGame(gd.getId());
		userSession.setGameDesc(null);
		userSession.setGameId(-1);
    }
	
	@RequestMapping(value="email", method=RequestMethod.POST)
	public String postEmails(@RequestParam("email") String emailcs, HttpServletRequest request)
	{
 		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		long gameId = userSession.getGameId();
		List<String> emails = Arrays.asList(emailcs.split(","));
		for (String email : emails) {
			email.trim();
			logger.debug("email invite="+email);
			playerService.invitePlayer(gameId, email);
		}
		return "redirect:home";
	}	
	
	@RequestMapping(value="member", method=RequestMethod.POST)
	public String postMembers(@RequestParam("member") long mids[], HttpServletRequest request)
	{
 		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		long gameId = userSession.getGameId();
		for (long mid : mids) {
			logger.debug("email invite member id="+ mid);
			playerService.inviteMember(gameId, mid);
		}
		return "redirect:home";
	}		
	
	@RequestMapping(value="remove", method=RequestMethod.POST)
	public String removeInvite(@RequestParam("inviteId") Long inviteId)
	{
 		playerService.removeInvite(inviteId);
 		return "redirect:home";
	}	

	@RequestMapping(value="share", method=RequestMethod.POST)
	@ResponseBody
	public String share(HttpServletRequest request, @RequestParam("max") Long count)
	{
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		GameDescriptionData game = gameService.getGameDescription(userSession.getGameId());
		logger.debug("save max players " + count);
		game.setMaxPlayers(count.intValue());
		gameService.updateGame(game);
 		return "success";
	}	
	@RequestMapping(value="days", method=RequestMethod.POST)
	@ResponseBody
	public String days(HttpServletRequest request, @RequestParam("days") Long count)
	{
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		GameDescriptionData game = gameService.getGameDescription(userSession.getGameId());
		logger.debug("save count down " + count);
		game.setCountDown(count.intValue());
		gameService.updateGame(game);
 		return "success";
	}	

	class FbInvite {
		public String id;
		public String first_name;
		public String last_name;
	}

	// TODO -- have js working, but need to make ajax call.  not sure if I should send all at once or one at time.  all at once makes
	// more sense.  but not sure how to do that.  Maybe should post.
	@RequestMapping(value="fbinvite", method=RequestMethod.POST)
	@ResponseBody
	public String fbinvite(
			@RequestParam("id") String id,
			@RequestParam("first_name") String first_name,
			@RequestParam("last_name") String last_name,
			HttpServletRequest request)
	{
 		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		long gameId = userSession.getGameId();
		playerService.inviteFbPlayer(gameId, id, first_name, last_name);
		return "success";
	}	


}
