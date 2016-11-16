package com.trivolous.game.domain.logic;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trivolous.game.dao.MemberDao;
import com.trivolous.game.data.MemberData;
import com.trivolous.game.domain.Member;
import com.trivolous.game.notify.NotificationSenderInterface;

@Service
@Transactional
public class MemberService  {
	protected final Log logger = LogFactory.getLog(getClass());
	@Autowired
 	MemberDao memberDao;
	@Autowired
	NotificationSenderInterface notificationSender;
	
	public MemberData findMemberById(long id)
	{
		Member member = memberDao.findById(id);
		if (member == null) {
			logger.error("Invalid member id lookup = "+id);
		}
		return new MemberData(member);
	}

	public List<MemberData> findAllMembers()
	{
		List<Member> members = memberDao.findAll();
		ArrayList<MemberData> membersData = new ArrayList<MemberData>();
		for (Member m : members) {
			membersData.add(new MemberData(m));
		}
		return membersData;
	}

	
	public MemberData findMemberByEmail(String email)
	{
		if (email == null) return null;
		logger.debug("findMemberByEmail :"+ email);
		if (email.isEmpty()) return null;
		Member m = memberDao.findByEmail(email);
		if (m == null) return null;
		return new MemberData(m);
	}
	
	public MemberData findMemberByFacebookId(String id)
	{
		Member m = memberDao.findByFacebookId(id);

		return (m == null ? null  :new MemberData(m));
	}
		
	
	public void updateMember(MemberData memberData)
	{
		logger.debug("updateMember "+memberData.getId());
		Member member = memberDao.findById(memberData.getId());
		if (member==null) {
			member = new Member();
			logger.info("Create new member name = " + memberData.getFirstName() + " " + memberData.getLastName());
			// if new member, send welcome 
		}
		if ((member.getEmail() == null) || !member.getEmail().equals(memberData.getEmail())) {
			// send welcome email for new members, or when email changed.
			if (memberData.getEmail() != null && !memberData.getEmail().isEmpty()) {
				notificationSender.sendWelcome(memberData.getEmail());
			}
		}
		
		member.setFirstName(memberData.getFirstName());
		member.setLastName(memberData.getLastName());
		member.setPassword(memberData.getPassword());
		member.setEmail(memberData.getEmail());		
		member.setFacebookId(memberData.getFacebookId());
		member.setImageUrl(memberData.getImageUrl());
		memberDao.create(member);
	}
	
	
	public void emailMemberPassword(String email)
	{
		if (email == null) return;
		if (email.isEmpty()) return;
		Member member = memberDao.findByEmail(email);
		if (member != null)
		{
			notificationSender.sendPasswordReminder(member);
		}
	}
	
	
}