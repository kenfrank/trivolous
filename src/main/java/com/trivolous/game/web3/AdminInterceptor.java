package com.trivolous.game.web3;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import com.trivolous.game.data.GameDescriptionData;
import com.trivolous.game.data.MemberData;
import com.trivolous.game.data.PlayerData;
import com.trivolous.game.domain.Game.STATUS;
import com.trivolous.game.domain.logic.MemberService;
import com.trivolous.game.domain.logic.NewGameService;
import com.trivolous.game.domain.logic.PlayerService;


/**
 */
public class AdminInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	private MemberService memberService;

	protected final Log logger = LogFactory.getLog(getClass());

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		UserSession userSession = (UserSession) WebUtils.getRequiredSessionAttribute(request, "userSession");

		if (userSession == null) {
			return false;
		}
		
		boolean isAllowed = false;
		
		MemberData member = memberService.findMemberById(userSession.getMemberId());
		if (member.getEmail().equals("kenfranklynch@yahoo.com")) {
			isAllowed = true;
		}
		
		return isAllowed;
		
	}
}
