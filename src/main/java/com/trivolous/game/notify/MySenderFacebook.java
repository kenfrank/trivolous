package com.trivolous.game.notify;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trivolous.game.domain.Member;
import com.trivolous.game.web3.Facebook;


public class MySenderFacebook implements MySenderInterface {
	protected final Log logger = LogFactory.getLog(getClass());
	
	@Override
	public void send(List<Member> toList, String subject, String textbody, String htmlbody) {

		for (Member m : toList) {
			String fbid = m.getFacebookId();
			if ((fbid != null) && (!fbid.isEmpty())) {
				logger.debug("Facebook notification to : " + fbid + ":"+m.getName());
				Facebook.sendNotification(fbid, subject);
			}
		}
	}

}
