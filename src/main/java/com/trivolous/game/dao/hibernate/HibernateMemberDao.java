package com.trivolous.game.dao.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.trivolous.game.dao.MemberDao;
import com.trivolous.game.domain.Member;

// maybe start here:
// http://static.springsource.org/spring/docs/2.0.x/reference/transaction.html
// http://stackoverflow.com/questions/1079114/spring-transactional-annotation-best-practice
// look in springapp-dao.xml to see how it is configured.
@Repository
public class HibernateMemberDao implements MemberDao {
	private SessionFactory sessionFactory;
	
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public HibernateMemberDao()
	{
	}
		
	@Override
	public void create(Member member)
	{
		sessionFactory.getCurrentSession().saveOrUpdate(member);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Member> findAll()
	{
		Query query = sessionFactory.getCurrentSession().createQuery("from Member");
		return query.list();
	}
	
	@Override
	public long find(String email, String password)
	{
		Member member = findByEmail(email);
		return (member == null ? -1 : member.getId());
	}

	// add this to interface if needed
	public Member findByName(String name)
	{
		Member member = (Member) sessionFactory.getCurrentSession().createQuery(
			"from Member where name=?")
			.setString(0, name)
			.uniqueResult();		
		return member;
	}
	
	@Override
	public Member findByEmail(String email)
	{
		Member member = (Member) sessionFactory.getCurrentSession().createQuery(
			"from Member where email=?")
			.setString(0, email)
			.uniqueResult();		
		return member;
	}
	
	@Override
	public Member findById(long id)
	{
		return (Member)sessionFactory.getCurrentSession().get(Member.class, id);
	}

	@Override
	public Member findByFacebookId(String id) {
		Member member = (Member) sessionFactory.getCurrentSession().createQuery(
				"from Member where facebookId=?")
				.setString(0, id)
				.uniqueResult();		
			return member;
	}
	

}
