package com.trivolous.game.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.trivolous.game.dao.ImageDao;
import com.trivolous.game.domain.QuestionImage;

@Repository
public class HibernateImageDao implements ImageDao {
	private SessionFactory sessionFactory;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void create(QuestionImage image) {
		sessionFactory.getCurrentSession().saveOrUpdate(image);
	}

	@Override
	public QuestionImage findById(long id) {
		return (QuestionImage)sessionFactory.getCurrentSession().get(QuestionImage.class, id);
	}
}
