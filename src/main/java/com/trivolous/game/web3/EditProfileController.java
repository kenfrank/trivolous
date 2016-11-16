package com.trivolous.game.web3;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.WebUtils;

import com.trivolous.game.data.MemberData;
import com.trivolous.game.domain.logic.MemberService;

/**
 */
@Controller
@RequestMapping(value="member/editProfile")
public class EditProfileController {

	@Autowired
	private MemberService memberService;
	protected final Log logger = LogFactory.getLog(getClass());

	@ModelAttribute
	public MemberData addAccount(HttpServletRequest request) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		MemberData member = memberService.findMemberById(userSession.getMemberId());
		return member; // will default to 'memberData'
	}	
	
	@RequestMapping(method=RequestMethod.POST)
	public String handlePostRequest(MemberData member)  {
		// note : model attribute will backfill any values not set by form!  No need for hidden fields.
		boolean valid = true;
		if (member.getPassword().isEmpty() || 
			member.getFirstName() == null || 
			member.getFirstName().isEmpty() || 
			member.getLastName() == null || 
			member.getLastName().isEmpty())
		{
			valid = false;
			logger.warn("member profile data invalid");
		}		
		if (valid) {
			if (member.getFacebookId() != null) {
				// ensure it is not already in use
				MemberData fbmember = memberService.findMemberByFacebookId(member.getFacebookId());
				if (fbmember != null) {
					if (fbmember.getId() != member.getId()) {
						logger.warn("Fb members dont match " + fbmember.getId() + " " + member.getId());
						member.setFacebookId(null); 
					}
				}
			}
			if (member.getFacebookId() != null) {
				logger.debug("update URL");
				member.setImageUrl("https://graph.facebook.com/"+member.getFacebookId()+"/picture");
			}
			memberService.updateMember(member);
		}
		return "redirect:/member/editProfile"; 
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public String handleGetRequest(HttpServletRequest request, ModelMap model) {
		// note model is filled using model attribute.
		return "member/editProfile"; 
	}
}
