package com.trivolous.game.web3;

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
import org.springframework.web.util.WebUtils;

import com.trivolous.game.data.GameDescriptionData;
import com.trivolous.game.data.PlayerData;
import com.trivolous.game.domain.Game.STATUS;
import com.trivolous.game.domain.logic.NewGameService;
import com.trivolous.game.domain.logic.PlayerService;
import com.trivolous.game.domain.logic.RegistrationKey;

/**
 */
@Controller
public class GameSettingsController {
	protected final Log logger = LogFactory.getLog(getClass());
	@Autowired
	private NewGameService gameService;
	@Autowired
	private PlayerService playerService;

	@RequestMapping(value="/game/settings", method=RequestMethod.GET)
	public String settings(Model model, HttpServletRequest request) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		PlayerData player = userSession.getPlayerData();
		GameDescriptionData  gdd = userSession.getGameDesc();
		if (player.getIsMaster()) {
			if (gdd.getStatus() == STATUS.REGISTERING) {
				List<PlayerData> players = playerService.getPlayersInGame(gdd.getId());

				model.addAttribute("registeredPlayers", players);
				model.addAttribute("invitedPlayers", gameService.getInvites(gdd.getId()));
				model.addAttribute("regkey", new RegistrationKey().gameIdToKey(userSession.getGameId()));
			}
			else {
				model.addAttribute("players", playerService.getPlayersInGame(gdd.getId()));
			}
			model.addAttribute("gameDesc", userSession.getGameDesc());
			return "game/gameSettingsAuthor"; 
		}
		else {
			return "game/gameSettings"; 
		}
	}
	@RequestMapping(value="/game/settings", method=RequestMethod.POST)
	public String settingsAction(Model model, HttpServletRequest request) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		PlayerData player = userSession.getPlayerData();
		long gameId = userSession.getGameId();
		if (player.getIsMaster()) {
			Map pmap = request.getParameterMap();
			if (pmap.containsKey("extend")) {
				logger.info("================ EXTEND!");
				GameDescriptionData gdd = userSession.getGameDesc();
				gdd.setCountDown(gdd.getCountDown() + 1);
				gameService.updateGame(gdd);
			} 
			else
			if (pmap.containsKey("start")) {
				logger.info("================ START!");
				gameService.startGame(gameId);
			}
			else
			if (pmap.containsKey("pause")) {
				logger.info("================ Pause!");
				gameService.pauseGame(gameId);
			}			
			else
			if (pmap.containsKey("resume")) {
				logger.info("================ Resume!");
				gameService.restartGame(gameId);
			}			
			else
			if (pmap.containsKey("end")) {
				logger.info("================ END!");
				gameService.endGame(gameId);
			}
		}
		else {
			Map pmap = request.getParameterMap();
			if (pmap.containsKey("quit")) {
				logger.info("================ QUIT!");
				playerService.removePlayer(player.getId());
				return "redirect:/member/gameList";
			}
		}
		return "redirect:settings";
	}
	@RequestMapping(value="/game/settings/removePlayer", method=RequestMethod.POST)
	public String settingsAction(@RequestParam("id") Long id, HttpServletRequest request) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		PlayerData player = userSession.getPlayerData();
		GameDescriptionData gdd = userSession.getGameDesc();
		if (player.getIsMaster()) {
			logger.info("================ REMOVE PLAYER! " + id);
			if (gdd.getNumPlayers() <=3) {
				logger.error("Cannot remove player when 3 or less " + id);
			}
			else {
				playerService.removePlayer(id);
			}
		}
		return "redirect:/game/settings";
	}
}
