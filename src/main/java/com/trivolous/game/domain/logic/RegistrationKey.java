package com.trivolous.game.domain.logic;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.trivolous.game.web3.PassEncrypt;


/**
 */
@Controller
public class RegistrationKey {

	protected final Log logger = LogFactory.getLog(getClass());
	@Autowired
	private MemberService memberService;
	@Autowired
	private PlayerService playerService;

	final static String OPEN_REG_COOKIE_NAME = "regkey";

	public void addCookie(String key,
			HttpServletResponse response) {
		Cookie c = new Cookie(OPEN_REG_COOKIE_NAME, key);
		c.setPath("/");
		c.setMaxAge(60*60); // 1 hour
		response.addCookie(c);
	}
	
	public String findAndRemoveKeyCookie(HttpServletRequest request,
			HttpServletResponse response) {
		String key = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie c : cookies) {
			if (c.getName().equals(OPEN_REG_COOKIE_NAME)) {
				key = c.getValue();
				// remove cookie
				c.setPath("/");
				c.setMaxAge(0);
				c.setValue("");
				response.addCookie(c);
				break;
			}
		}
		return key;
	}
	
	public Long keyToGameId(String key) {
		Long gameId = null;
		if (key != null) {
			PassEncrypt passEncrypt = new PassEncrypt();
			String keyClear = passEncrypt.decode(key);
			logger.info("Found Register key=" + key + " -> " + keyClear);
			try {
				gameId = Long.valueOf(keyClear);
			} catch(Exception e) {
				gameId = new Long(-1);
			}
		}
		return gameId;
	}
	
	public String gameIdToKey(Long gameId) {
		PassEncrypt passEncrypt = new PassEncrypt();
		String key = passEncrypt.encode(new Long(gameId).toString());
		return key;
	}
	
	
}