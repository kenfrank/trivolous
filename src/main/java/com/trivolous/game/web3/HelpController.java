package com.trivolous.game.web3;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.WebUtils;

/**
 */
@Controller
@RequestMapping(value="member/help")
public class HelpController {
	protected final Log logger = LogFactory.getLog(getClass());

	@RequestMapping(method=RequestMethod.GET)
	public String handleGetRequest(HttpServletRequest request) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		logger.info("Help shown for member id = " + userSession.getMemberId());
		return "member/help"; 
	}
}
