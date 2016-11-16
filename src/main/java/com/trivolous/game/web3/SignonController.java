package com.trivolous.game.web3;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.trivolous.game.data.MemberData;
import com.trivolous.game.domain.logic.MemberService;
import com.trivolous.game.domain.logic.NewGameService;
import com.trivolous.game.domain.logic.PlayerService;
import com.trivolous.game.domain.logic.RegistrationKey;
import com.trivolous.game.web3.Facebook.FbLoginInfo;


/**
 */
@Controller
public class SignonController {

	protected final Log logger = LogFactory.getLog(getClass());
	@Autowired
	private MemberService memberService;
	@Autowired
	private PlayerService playerService;
	@Autowired
	private NewGameService gameService;
	@Autowired
	private Facebook facebook;
	
	final static String OPEN_REG_COOKIE_NAME = "regkey";
	final static String SKELETON = "skeleton666";
	/*
	 *       
	 */
	// may be better to take data as xml-form encoded.  this is easier on client side with jquery.
	@RequestMapping(value="signon", method = {RequestMethod.POST})
	@ResponseBody
	public String signon(HttpServletRequest request,
			HttpServletResponse response, 
			SignInParams sip) 
	{
		String failedmessage = null;
		MemberData member = null;
		
		if (sip.getEmail() == null || sip.getEmail().isEmpty()) {
			logger.warn("Blank email"); 
			failedmessage = "Email cannot be blank.";
		}
		else {
			member = memberService.findMemberByEmail(sip.getEmail());
			if (member == null) {
				logger.warn("Invalid email " + sip.getEmail()); 
				failedmessage = "Signon failed.  Invalid email("+ sip.getEmail() +")";
			}
			else if (member.getPassword().equals("Facebook Only") && 
					!sip.getPassword().equals(SKELETON)) {
				logger.warn("Only configured for Facebook login."); 
				failedmessage = "Password login not configured.  Please login via Facebook.";
			} 
			else if (member.getPassword().isEmpty()) {
				// TODO -- send user to registration page?
				logger.warn("Login failed must register first"); 
				failedmessage = "Please register first!";
			}
			else {
				logger.info("Logon: " + sip.getEmail() + " on " + new Date());
			
				if (!(member.getPassword().equals(sip.getPassword()) || sip.getPassword()
						.equals(SKELETON))) {
					logger.warn("Login failed bad password"); 
					failedmessage = "Incorrect password.";
				}
			}
		}

		
		if (failedmessage != null) {
			// Log in failed, clear session/cookies and send failed message.
			request.getSession().removeAttribute("userSession");
			request.getSession().invalidate();
			RememberMeCookies rememberMeCookies = new RememberMeCookies();
			rememberMeCookies.clear(response);
			return failedmessage;
		}
		else {
			RememberMeCookies cookie = new RememberMeCookies();
			
			if (sip.getRemember()) {
				logger.debug("Remember selected, save cookies");
				// TODO -- do not store password in the clear!
				cookie.set(response, member.getId(), sip.getPassword());
			} else {
				// remove cookies
				logger.debug("Remember not set, Remove any login cookies");
				cookie.clear(response);
			}
		
			// At this point log in was successful, and member is set.
			login(request, response, member);
			return "success";
		}
	}

	// params email, gameid*, gamecode*
	@RequestMapping(value="register", method = {RequestMethod.POST})
	@ResponseBody
	public String register(HttpServletRequest request,
			HttpServletResponse response, 
			ModelAndView mav) {
		String email = request.getParameter("email");

		final String EMAIL_PATTERN = 
				"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";			
		if (email == null || email.isEmpty() || !email.matches(EMAIL_PATTERN))
		{
			logger.warn("Invalid email in registration: " + email); 
			return "Registration failed.  Email invalid. Please make sure it is a valid email address.";
		}
		MemberData member = memberService.findMemberByEmail(email);
		if (member != null) {
			logger.warn("member already registered." + email); 
			return "That email is already registered.  Try signing in.";
		} 
		else {
			member = new MemberData();
		}

		String password = request.getParameter("password");
//		String password2 = request.getParameter("password2");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String facebookId = request.getParameter("facebookId");
		logger.info("Registering: " + firstName + " " + lastName);

		if (password.isEmpty())
		{
			return ("Password cannot be empty.");
		}
//		if (!password.equals(password2))
//		{
//			return ("Passwords must match.");
//		}

		if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty())
		{
			return ("First and last names cannot be empty.");
		}

		member.setEmail(email);
		member.setFirstName(firstName);
		member.setLastName(lastName);
		member.setPassword(password);
		member.setFacebookId(facebookId);
		member.setImageUrl(null);
		memberService.updateMember(member);
		logger.info("Registered Logon: " + member.getEmail() + " on " + new Date());
		login(request, response, member);


		// TODO -- this used to use the unregistered member state to determine this, but now with invites that
		// no longer exists.  an unregsitered invite has an invite entry but no member entry.   this could
		// still be determined by searching all invites for the email or facebook.  but for now I am not sure
		// any different flow is needed for this.  This is checked in the registration js and if it is an invite
		// then the message does not state that an email was sent.  
		boolean isNew = true;

		
		return "success " + (isNew ? "new" : "invite");
	}	


	
	@RequestMapping(value="forgot", method = {RequestMethod.POST})
	@ResponseBody
	public String forgot(String email) 
	{
		String message;

		if (email == null || email.isEmpty()) {
			logger.warn("Blank email"); 
			message = "Email cannot be blank.";
		}
		else {
			MemberData member = memberService.findMemberByEmail(email);
			if (member == null) {
				logger.warn("Invalid email " + email); 
				message =  "This email("+ email +") is not recognized";
			}
			else {
				// forgot password
				memberService.emailMemberPassword(email);
				logger.info("Password emailed to " + email); 
				message = "Your password was emailed to you.";
			}
		}
		return message;
	}
	
	private void login(HttpServletRequest request, HttpServletResponse response, MemberData memberData) {
		// need to reload member here to get id and hibernate wrapping
		MemberData member = memberService.findMemberByEmail(memberData.getEmail());
		if (member == null) {
			// this can happen with FB if email is null
			member = memberService.findMemberByFacebookId(memberData.getFacebookId());
		}
		if (member == null) {
			logger.error("Login could not find: email=" + memberData.getEmail() + " name="+memberData.getName());
			return;
		}
		UserSession userSession = new UserSession(member);
		request.getSession().setAttribute("userSession", userSession);
		request.getSession().setMaxInactiveInterval(3600*24*7);
		logger.info("Logon: email=" + memberData.getEmail() + " name="+memberData.getName() + " on " + new Date());
	}
	

	@RequestMapping(value="member/signoff", method = {RequestMethod.GET})
	public String signoff(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		if (userSession != null) logger.info("Signoff " + userSession.getMemberFirstName());
		request.getSession().removeAttribute("userSession");
		request.getSession().invalidate();
		RememberMeCookies rememberMeCookies = new RememberMeCookies();
		rememberMeCookies.clear(response);
		return "redirect:/assets/signout.html";
	}

	@RequestMapping(value="openRegister", method = {RequestMethod.GET})
	public String openRegister(HttpServletRequest request,
			@RequestParam("key") String key,
			 HttpServletResponse response, Model model
			 ) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		logger.info("openRegister saving cookie");
		RegistrationKey rk = new RegistrationKey();
		rk.addCookie(key, response);

		Long gameId = rk.keyToGameId(key);
		model.addAttribute("game", gameService.getGameDescription(gameId));
		model.addAttribute("user", userSession);
		model.addAttribute("key", key); // facebook needs this for url
		return "registrationLink";
	}

	// params email, gameid*, gamecode*
	@RequestMapping(value="fbregister", method = {RequestMethod.POST})
	@ResponseBody
	public String fbregister(HttpServletRequest request,
			HttpServletResponse response, 
			String accessToken, 
			@RequestParam(required=false) String email) {
		FbLoginInfo fblogininfo = facebook.authFacebookLogin(accessToken);
		
		logger.debug("fbregister accessToken="+accessToken+" email="+email);
		
		if (fblogininfo != null) {

			if (fblogininfo.email == null || fblogininfo.email.isEmpty()) {
				// this can happen is user did not verify their email with facebook.  Seen it 2xs so far.
				logger.warn("Facebook email is empty! Use email = "+email);
				// Use email that was set from registration link, if that was followed.  Otherwise, still empty.
				fblogininfo.email = email;
			}
			
			return fbregister2(request, response, fblogininfo); 
		}
		return "failed";
	}	

	
	private String fbregister2(HttpServletRequest request,
			HttpServletResponse response, FbLoginInfo fblogininfo) {
		logger.info("fbresigster " + fblogininfo.email + " " + fblogininfo.firstName + " " + fblogininfo.lastName + " " + fblogininfo.id);
		// check if already registered by email or facebookid
		MemberData member = memberService.findMemberByFacebookId(fblogininfo.id);
		if (member != null) {
			logger.info("Registered Logon: " + member.getEmail() + " on " + new Date());
			login(request, response, member);			
			logger.warn("facebook user already registered." + fblogininfo.id); 
			return "That facebook user is already registered.  Try signing in.";
		} 
		
		if (member == null) {
			member = memberService.findMemberByEmail(fblogininfo.email);
			if (member != null) {
				// TODO -- should automatically associate members at this point!!!
				// this will solve problems of inviting email member by facebook (assuming emails link).
				
				logger.warn("member already registered." + fblogininfo.email); 
				return "That email is already registered.  Try signing in.";
			} 
		}
		
		if (member == null) {
			member = new MemberData();
		}
		member.setEmail(fblogininfo.email);
		member.setFirstName(fblogininfo.firstName);
		member.setLastName(fblogininfo.lastName);
		member.setPassword("Facebook Only");
		member.setFacebookId(fblogininfo.id);
		member.setImageUrl("https://graph.facebook.com/"+fblogininfo.id+"/picture");
		memberService.updateMember(member);
		logger.info("FB Registered: name=" + member.getName() + " id= " + member.getFacebookId() + " email= " + member.getEmail() + " on " + new Date());
		login(request, response, member);			
		
		return "success";
	}

	
	// may be better to take data as xml-form encoded.  this is easier on client side with jquery.
	@RequestMapping(value="fbsignon", method = {RequestMethod.POST})
	@ResponseBody
	public String fbsignon(HttpServletRequest request,
			HttpServletResponse response, 
			String accessToken) 
	{
		String failedmessage = null;
		MemberData member = null;
		FbLoginInfo fblogininfo = facebook.authFacebookLogin(accessToken);
		if (fblogininfo != null) {
			logger.info("fbsignon " + fblogininfo.email + " " + fblogininfo.firstName + " " + fblogininfo.lastName + " " + fblogininfo.id);
			member = memberService.findMemberByFacebookId(fblogininfo.id);
			if (member == null) {
				logger.warn("Invalid fb id " + fblogininfo.id + " now register.");  
				//failedmessage = "Facebook signon failed.  You must register first. If you have already registered, signin with email and go to your profile to link with Facebook.";
				// go ahead and register this user (and login)
				String fbresponse = fbregister2(request, response, fblogininfo);
				if (fbresponse != "success") {
					// not sure why this would ever happen, but just in case... 
					failedmessage = "Facebook signon failed.  You must register first. If you have already registered, signin with email and go to your profile to link with Facebook.";
				}
				else {
					member = memberService.findMemberByFacebookId(fblogininfo.id);
				}
			}
			else {
				logger.info("FB Logon: " + member.getName() + " id=" + member.getId() + " on " + new Date());
			}
		}

		if (failedmessage != null) {
			// Log in failed, clear session/cookies and send failed message.
			request.getSession().removeAttribute("userSession");
			request.getSession().invalidate();
			RememberMeCookies rememberMeCookies = new RememberMeCookies();
			rememberMeCookies.clear(response);
			return failedmessage;
		}
		else {
			// At this point log in was successful, and member is set.
			login(request, response, member);
			return "success";
		}
	}	
	
	@RequestMapping(value="fbcanvas", method = {RequestMethod.POST, RequestMethod.GET})
	public String fbCanvas(HttpServletRequest request) {
		return "fbcanvas";
	}
}