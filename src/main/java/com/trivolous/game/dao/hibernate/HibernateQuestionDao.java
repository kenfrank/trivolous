package com.trivolous.game.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.trivolous.game.dao.QuestionDao;
import com.trivolous.game.domain.Question;

@Repository
public class HibernateQuestionDao implements QuestionDao {
	private SessionFactory sessionFactory;
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}	

	@Override
	public void create(Question q)
	{
		sessionFactory.getCurrentSession().saveOrUpdate(q);
	}

	@Override
	public Question findById(long id) {
		int iid = (int)id;
		return (Question)sessionFactory.getCurrentSession().get(Question.class, iid);
	}
}
