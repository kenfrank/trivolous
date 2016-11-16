package com.trivolous.game.dao;

import java.util.List;

import com.trivolous.game.domain.Member;

public interface MemberDao {

	public abstract void create(Member member);

	public abstract List<Member> findAll();

	public abstract long find(String email, String password);

	public abstract Member findByName(String name);

	public abstract Member findById(long id);

	public abstract Member findByEmail(String email);

	public abstract Member findByFacebookId(String id);

}