package com.trivolous.game.web3;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// TODO -- why not use HTTP session for this?
public class RememberMeCookies {
	protected final Log logger = LogFactory.getLog(getClass());
	static final int MAX_AGE = 14*24*60*60; // 2 weeks
	static final String ID_NAME = "id";
	static final String PASSWORD_NAME = "password";
	Long id;
	String password;
	
	public void set(HttpServletResponse response, Long id, String password) {
		Cookie cookie1 = new Cookie(ID_NAME, id.toString());
        cookie1.setMaxAge(MAX_AGE); // 2 weeks
        response.addCookie(cookie1); 					
        // encrpty password for some security
		Cookie cookie2 = new Cookie(PASSWORD_NAME, new PassEncrypt().encode(password));
        cookie2.setMaxAge(MAX_AGE); // 2 weeks
        response.addCookie(cookie2); 					
	}
	
	public void clear(HttpServletResponse response) {
		Cookie cookie1 = new Cookie(ID_NAME, "");
        cookie1.setMaxAge(0); 
        cookie1.setPath("/");
        response.addCookie(cookie1); 					
		Cookie cookie2 = new Cookie(PASSWORD_NAME, "");
        cookie2.setMaxAge(0);
        cookie2.setPath("/");
        response.addCookie(cookie2); 					
	}
	
	// call read then use gets 
	public boolean read(HttpServletRequest request) {
		boolean foundId = false;
		boolean foundPass = false;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				if (c.getName().equals(ID_NAME)) {
					id = Long.valueOf(c.getValue());
					foundId = true;
				}
				if (c.getName().equals(PASSWORD_NAME)) {
					password = new PassEncrypt().decode(c.getValue());
					foundPass = true;
				}
			}
		}
		return foundId && foundPass;
	}
	
	public Long getMemberId() {return id;}
	
	public String getPassword() {return password;}
	
}
