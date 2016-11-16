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
public class MasterCreateController {
	@Autowired
	private NewGameService gameService;

	@RequestMapping(value="/member/masterCreate",method=RequestMethod.GET)
    protected String memberCreate(ModelMap model) 
    {
		GameDescriptionData gd;
		gd = new GameDescriptionData(); 
		model.addAttribute(gd);
        return "member/masterCreate";
    }


	@RequestMapping(value="/member/masterCreate",method=RequestMethod.POST)
	protected String onSubmit(@ModelAttribute GameDescriptionData game, HttpServletRequest request)
	{
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		long masterId = userSession.getMemberId();
		long newId = gameService.createNewGame(game, masterId);
		userSession.setGameId(newId);
		return "redirect:/game/create/make";
	}	


}




