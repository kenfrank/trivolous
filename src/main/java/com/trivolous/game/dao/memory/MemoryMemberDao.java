package com.trivolous.game.dao.memory;

import java.util.ArrayList;
import java.util.List;

import com.trivolous.game.dao.MemberDao;
import com.trivolous.game.domain.Member;

public class MemoryMemberDao implements MemberDao {
	static private List<Member> members = new ArrayList<Member>();
	static int id = 1;
	
	public MemoryMemberDao()
	{
	}
		
	@Override
	public void create(Member member)
	{
		member.setId(id++);
		members.add(member);
	}

	@Override
	public List<Member> findAll()
	{
		return members;
	}
	
	@Override
	public long find(String email, String password)
	{
		for (Member m : members)
		{
			if (m.getEmail() != null && m.getEmail().equals(email)) return m.getId();
		}
		return -1;
	}
	
	@Override
	public Member findById(long id)
	{
		for (Member m : members)
		{
			if (m.getId() == id) return m;
		}
		return null;
	}

	@Override
	public Member findByName(String name) {
		return null;
	}

	@Override
	public Member findByEmail(String email) {
		for (Member m : members)
		{
			if ((m.getEmail() != null) && m.getEmail().equals(email)) return m;
		}
		return null;
	}

	@Override
	public Member findByFacebookId(String id) {
		return null;
	}
	

}
