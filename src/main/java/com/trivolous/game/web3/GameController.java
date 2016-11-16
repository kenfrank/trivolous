package com.trivolous.game.web3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.WebUtils;

import com.trivolous.game.data.ActiveGameData;
import com.trivolous.game.data.GameList;
import com.trivolous.game.data.PlayerData;
import com.trivolous.game.data.RoundData;
import com.trivolous.game.domain.Game.STATUS;
import com.trivolous.game.domain.Player.Status;
import com.trivolous.game.domain.logic.CommentService;
import com.trivolous.game.domain.logic.ImageService;
import com.trivolous.game.domain.logic.NewGameService;
import com.trivolous.game.domain.logic.PlayerService;

/**
 */
@Controller
public class GameController {
	protected final Log logger = LogFactory.getLog(getClass());
	@Autowired
	private NewGameService gameService;
	@Autowired
	private PlayerService playerService;
	@Autowired
	private CommentService commentService;
	@Autowired
	private ImageService imageService;	
	
	@RequestMapping(value="/game/game", method=RequestMethod.GET)
	public String handleGetRequest(Model model, HttpServletRequest request) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		logger.info("Game shown for member id = " + userSession.getMemberId() + " status = " + userSession.getGameDesc().getStatus());
		long gameId = userSession.getGameId();
		if (userSession.getGameDesc().getStatus() == STATUS.CREATING) {
			return "redirect:/game/create/description";
		} 
		else 
		if (userSession.getGameDesc().getStatus() == STATUS.COMPLETE) {
			// TODO -- might not need to redirect here.  
			return "redirect:/game/over";
		} 
		model.addAttribute("gameDesc", userSession.getGameDesc());
		PlayerData player = playerService.findPlayer(gameId, userSession.getMemberId());

		if (player.getStatus() == Status.NEEDS_TO_ANSWER) {
			RoundData currentRound = gameService.getCurrentRound(gameId);		
			model.addAttribute("currentRound", currentRound);
		}
		// only valid if game is active (not creating/registering)
		if (player.getStatus() == Status.ANSWERED || player.getStatus() == Status.MADE_QUESTION) { 
			ArrayList<PlayerData> playersToAnswer = new ArrayList<PlayerData>();
			for (PlayerData p : playerService.getActivePlayersInGame(gameId)) {
				if (!p.getIsAnswered()) {
					playersToAnswer.add(p);
				}
			}
			model.addAttribute("playersToAnswer",playersToAnswer);
		}

		RoundData lastRound = playerService.getLastRound(player.getId());
		if (lastRound == null) {
			// set first timer to give help if first round and player has not completed any games yet.
			GameList gameList = gameService.getGameList(userSession.getMemberId());
			if (gameList.getCompletedGames().isEmpty()) {	
				model.addAttribute("firstTimer",true);
			}
		} else {
			model.addAttribute("comments", lastRound.getComments());		
		}
		model.addAttribute("lastRound", lastRound);
		model.addAttribute("players", playerService.getActivePlayersInGame(gameId));
		model.addAttribute("player", player);
		model.addAttribute("activeGame", gameService.getActiveGameData(gameId));
	
		ArrayList<RoundData> rounds = new ArrayList<RoundData>();
		if (lastRound != null) {
			for (int i=lastRound.getRoundNumber()-1; i > 0; --i) {
				rounds.add(gameService.getRound(gameId, i));
			}
		}
		model.addAttribute("rounds", rounds);
	
		if (userSession.getTemps().containsKey("answered")) {
			model.addAttribute("answered", userSession.getTemps().get("answered"));
			userSession.getTemps().remove("answered");
		}
		
		return "game/game"; 
	}
	
	@RequestMapping(value="/game/comment", method=RequestMethod.POST)
	public String addComment(@RequestParam("text") String text, HttpServletRequest request, Model model) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		PlayerData player = playerService.findPlayer(userSession.getGameId(), userSession.getMemberId());
		//commentService.submitCommentToAnswered(player.getId(), text);
		playerService.submitComment(player.getId(), text);
		RoundData lastRound = playerService.getLastRound(player.getId());
		model.addAttribute("comments", lastRound.getComments());		
		return "game/_comments"; 
	}

	@RequestMapping(value="/game/over/comment", method=RequestMethod.POST)
	public String addCommentOver(@RequestParam("text") String text, HttpServletRequest request, Model model) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		PlayerData player = playerService.findPlayer(userSession.getGameId(), userSession.getMemberId());
		//commentService.submitCommentToAnswered(player.getId(), text);
		playerService.submitComment(player.getId(), text);
		model.addAttribute("comments", gameService.getGameComments(userSession.getGameId()));
		return "game/_comments"; 
	}

	@RequestMapping(value="/game/hostcomment", method=RequestMethod.POST)
	@ResponseBody
	public String addHostComment(@RequestParam("text") String text, HttpServletRequest request, Model model) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		PlayerData player = playerService.findPlayer(userSession.getGameId(), userSession.getMemberId());
		playerService.submitCommentToMaster(player.getId(), text);
		return "success"; 
	}

	@RequestMapping(value="/game/info", method=RequestMethod.GET)
	public String info(Model model,  HttpServletRequest request) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		model.addAttribute("gameDesc", userSession.getGameDesc());
		if (userSession.getGameDesc().isActive()) {
			model.addAttribute("activeGameDesc", gameService.getActiveGameData(userSession.getGameDesc().getId()));
		}
		model.addAttribute("players", playerService.getPlayersInGame(userSession.getGameDesc().getId()));
		return "game/gameInfo"; 
	}

	@RequestMapping(value="/game/score", method=RequestMethod.GET)
	public String score(Model model,  HttpServletRequest request) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		long gameId = userSession.getGameId();
		List<PlayerData> players = playerService.getActivePlayersInGame(gameId);
		// Sort by score
		Collections.sort(players, new Comparator<PlayerData>() {
		    public int compare(PlayerData o1, PlayerData o2) {
		        return (int) (o2.getScore() - o1.getScore());
		    }});
		model.addAttribute("players", players);
		return "game/gameScore"; 
	}

	@RequestMapping(value="/game/previousQuestions", method=RequestMethod.GET)
	public String previousQuestions(Model model,  HttpServletRequest request) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		long gameId = userSession.getGameId();
		ArrayList<RoundData> rounds = new ArrayList<RoundData>();
		PlayerData player = userSession.getPlayerData();
		RoundData lastRound = playerService.getLastRound(player.getId());
		Integer lastRoundNumber = lastRound.getRoundNumber();
		if (userSession.getGameDesc().getStatus() != STATUS.COMPLETE) {
			lastRoundNumber -= 1;
		}
		if (lastRound != null) {
			for (int i=lastRoundNumber; i > 0; --i) {
				rounds.add(gameService.getRound(gameId, i));
			}
		}
		model.addAttribute("rounds", rounds);
		model.addAttribute("players", playerService.getActivePlayersInGame(gameId));
		return "game/previousQuestions"; 
	}
	@RequestMapping(value="/game/over/previousQuestions", method=RequestMethod.GET)
	public String previousQuestionsOver(Model model,  HttpServletRequest request) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		long gameId = userSession.getGameId();
		model.addAttribute("rounds", gameService.getCompletedRounds(gameId));
		model.addAttribute("players", playerService.getActivePlayersInGame(gameId));
		return "game/previousQuestions"; 
	}


	@RequestMapping(value="/game/over", method=RequestMethod.GET)
	public String over(Model model,  HttpServletRequest request) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		model.addAttribute("gameDesc", userSession.getGameDesc());
		long gameId = userSession.getGameId();
		List<PlayerData> players = playerService.getActivePlayersInGame(gameId);
		// Sort by score
		Collections.sort(players, new Comparator<PlayerData>() {
		    public int compare(PlayerData o1, PlayerData o2) {
		        return (int) (o2.getScore() - o1.getScore());
		    }});
		model.addAttribute("players", players);
		model.addAttribute("rounds", gameService.getCompletedRounds(gameId));
		model.addAttribute("comments", gameService.getGameComments(gameId));
		
		// just in case user was last one to answer in game....
		if (userSession.getTemps().containsKey("answered")) {
			model.addAttribute("answered", userSession.getTemps().get("answered"));
			userSession.getTemps().remove("answered");
		}		
		return "game/gameOver"; 
	}
	
	// TODO -- come up with some type of security here so that pics are private.  here could check to make sure
	// that pic is in question in game.   
	@RequestMapping(value="/game/image", method=RequestMethod.GET, produces = "image/jpg" )
	public void getImage(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		long id = ServletRequestUtils.getLongParameter(request, "id", -1);
		byte[] imageArray = null;

		if (id > 0) {
			imageArray = imageService.getImageById(id);
		}
		
		
		if (imageArray != null)
		{
			response.getOutputStream().write(imageArray);
		}
	}

	@RequestMapping(value="/game/image", method=RequestMethod.POST, produces="application/json", consumes="multipart/form-data")
	@ResponseBody
	public String onSubmitJsonBack(
			HttpServletRequest request,  
			@RequestParam("file") MultipartFile mpfile)
			throws Exception {
		// TODO -- add check that user is not adding more pictures than queued questions.
		logger.debug("Question Image Upload");
        // let's see if there's content there
        byte[] file = mpfile.getBytes();
		if (file != null && file.length != 0) {
			// TODO -- add member to image record in db.
        	long id = imageService.putImage(file);
        	// TODO -- need much better security than sequential incrememnting id!  Use some sort of key.
        	// need to protect again user alterning id in form submit.  cannot verify id belongs to user.  maybe
        	// store member name with image?
    		return "{\"file\" : \"Success\", \"id\" : \""+id+"\"}";
        }
		return "Failed";
	}



	@RequestMapping(value="/game/image", method=RequestMethod.POST, produces="text/html" ,consumes="multipart/form-data")
	public String onSubmitHtmlBack(
			HttpServletRequest request,  
			@RequestParam("file") MultipartFile mpfile)
			throws Exception {
		// UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		logger.debug("Question Image Upload");
        // let's see if there's content there
        byte[] file = mpfile.getBytes();
		if (file != null && file.length != 0) {
        	imageService.putImage(file);
        	// TODO -- this id needs to get into question form.  Probably would be best to remove this and
        	// if HTML5 is not available, just submit pic with question form.  For now just assume HTML5.
		}
		return "redirect:/assets/testing2.html";
	} 
	
	
	@RequestMapping(value="/game/image/delete", method=RequestMethod.POST)
	@ResponseBody
	public String playerDelete(@RequestParam("id") long id) {

		// TODO -- ensure image belongs to user (
		imageService.removeImage(id);
		return "{\"result\" : \"Success\"}";
	}

	@RequestMapping(value="/game/skip", method=RequestMethod.POST)
	public String skipQuestion(@RequestParam("text") String text, HttpServletRequest request, Model model) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		PlayerData player = playerService.findPlayer(userSession.getGameId(), userSession.getMemberId());
		playerService.askerSkip(player.getId(), text);
		return "redirect:/game/game";
	}
		
	
}
