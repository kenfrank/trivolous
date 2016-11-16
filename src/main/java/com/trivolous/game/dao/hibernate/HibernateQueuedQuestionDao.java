package com.trivolous.game.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.trivolous.game.dao.QueuedQuestionDao;
import com.trivolous.game.domain.QueuedQuestion;

@Repository
public class HibernateQueuedQuestionDao implements QueuedQuestionDao {
	private SessionFactory sessionFactory;
	
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public void create(QueuedQuestion qq)
	{
		sessionFactory.getCurrentSession().saveOrUpdate(qq);
	}

	@Override
	public void delete(QueuedQuestion queuedQuestion) {
		sessionFactory.getCurrentSession().createQuery("delete QueuedQuestion where id=?").
			setLong(0,queuedQuestion.getId()).executeUpdate();
	}
	

	
}
