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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.trivolous.game.data.GameList;
import com.trivolous.game.domain.logic.MemberService;
import com.trivolous.game.domain.logic.NewGameService;
import com.trivolous.game.domain.logic.PlayerService;

@Controller
@RequestMapping(value="admin")
public class AdminController {
	protected final Log logger = LogFactory.getLog(getClass());
	@Autowired
	private NewGameService gameService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private PlayerService playerService;
	final static String OPEN_REG_COOKIE_NAME = "regkey";
	
	@RequestMapping(value="main")
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		ModelAndView mav = new ModelAndView("admin/main");
		
		mav.addObject("games", gameService.getAllGames());
		mav.addObject("members", memberService.findAllMembers());
		
		return mav;
	}
	

}