package com.trivolous.game.web3;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.WebUtils;

import com.trivolous.game.data.GameDescriptionData;
import com.trivolous.game.domain.logic.NewGameService;



@Controller
public class GameCreate1Controller {
	@Autowired
	private NewGameService gameService;

	
	@ModelAttribute
	protected GameDescriptionData getGame(HttpServletRequest request)
	{
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		GameDescriptionData gd = userSession.getGameDesc(); 
		return gd;
	}
	
	@RequestMapping(value="/game/create/description",method=RequestMethod.GET)
    protected String gameCreate(ModelMap model, HttpServletRequest request) 
    {
        return "member/masterCreate";
    }

	@RequestMapping(value="/game/create/description",method=RequestMethod.POST)
	protected String onSubmit2(@ModelAttribute GameDescriptionData game, HttpServletRequest request)
	{
		if (request.getParameterMap().containsKey("delete")) {
			gameDelete(request);
			return "redirect:/member/gameList";
		}
		gameService.updateGame(game);
		return "redirect:/game/create/make";
	}
	
    protected void gameDelete(HttpServletRequest request) 
    {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		GameDescriptionData gd = userSession.getGameDesc(); 
		gameService.endGame(gd.getId());
		userSession.setGameDesc(null);
		userSession.setGameId(-1);
    }


}




