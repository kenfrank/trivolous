package com.trivolous.game.web3;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import com.trivolous.game.data.MemberData;
import com.trivolous.game.domain.logic.MemberService;
import com.trivolous.game.domain.logic.NewGameService;


/**
 */
public class SignonInterceptor extends HandlerInterceptorAdapter {
	@Autowired
	private NewGameService gameService;
	@Autowired
	private MemberService memberService;
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");

		if (userSession == null) {
			// check if 'remember' cookie
			logger.info("No user session");
			RememberMeCookies rememberMeCookies = new RememberMeCookies();
			if (rememberMeCookies.read(request)) {
				MemberData member = memberService.findMemberById(rememberMeCookies.getMemberId());
				if (member != null) {
					logger.info("Found login cookie for "+ member.getName());
					if (member.getPassword().equals(rememberMeCookies.getPassword())) {
						logger.info("Cookie login successful for "+ member.getName());
						// cookie found and matched password
						// TODO -- this fails because no DB session.  I think solution may need
						// to be making a signon URL?
						userSession = new UserSession(member);
						request.getSession().setAttribute("userSession", userSession);
						request.getSession().setMaxInactiveInterval(3600 * 24 * 7);
						return true;
					}
				}
				logger.warn("Cookie login unsuccessful, removing cookies");
				// cookie didnt authenticate.  remove it.
				rememberMeCookies.clear(response);
			}
			
			
			String url = request.getServletPath();
			String query = request.getQueryString();
			ModelAndView modelAndView = new ModelAndView("redirect:/assets/index.html");
			// TODO -- facebook			
			//		"fb_login_url", Facebook.getLoginRedirectURL());
			if (query != null) {
				
				url += "?"+query;
			}
			// TODO -- remove if not used.
			//modelAndView.addObject("signonForwardAction", url);
			logger.debug("Set forward url="+url);
			request.getSession().setAttribute("forwardUrl", url);
			throw new ModelAndViewDefiningException(modelAndView);
		}
		else {
		
			return true;
		}
	}

}
