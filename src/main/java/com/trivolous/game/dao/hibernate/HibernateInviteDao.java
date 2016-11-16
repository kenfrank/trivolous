package com.trivolous.game.dao.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.trivolous.game.dao.InviteDao;
import com.trivolous.game.domain.Game;
import com.trivolous.game.domain.Invite;

// maybe start here:
// http://static.springsource.org/spring/docs/2.0.x/reference/transaction.html
// http://stackoverflow.com/questions/1079114/spring-transactional-annotation-best-practice
// look in springapp-dao.xml to see how it is configured.
@Repository
public class HibernateInviteDao implements InviteDao {
	private SessionFactory sessionFactory;
	
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public HibernateInviteDao()
	{
	}
		
	@Override
	public void create(Invite invite)
	{
		sessionFactory.getCurrentSession().saveOrUpdate(invite);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Invite>  findByGame(Game game)
	{
		Query query =  sessionFactory.getCurrentSession().createQuery(
			"from Invite where game_fk=?")
			.setEntity(0, game);
		return query.list();
	}

//	@Override
//	public Member findById(long id)
//	{
//		return (Member)sessionFactory.getCurrentSession().get(Member.class, id);
//	}


}
