package com.trivolous.game.dao.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.trivolous.game.dao.GameCommentDao;
import com.trivolous.game.domain.GameComment;

@Repository
public class HibernateGameCommentDao implements GameCommentDao {
	private SessionFactory sessionFactory;
	
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void create(GameComment gameComment) {
		sessionFactory.getCurrentSession().saveOrUpdate(gameComment);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<GameComment> findAll() {
		Query query = sessionFactory.getCurrentSession().
			createQuery("from GameComment as comment where comment.player.game.id=?").setInteger(0, 1);
		// createQuery("from GameComment");
		return query.list();
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<GameComment> findByGame(long gameId) {
		Query query = sessionFactory.getCurrentSession().
			createQuery("from GameComment as comment where comment.player.game.id=? order by comment.created").setLong(0, gameId);
		// createQuery("from GameComment");
		return query.list();
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<GameComment> findAllByRound(long gameId, int roundNum) {
		Query query = sessionFactory.getCurrentSession().
				createQuery("from GameComment as comment where comment.player.game.id=? " + 
							"and roundNumber=? order by comment.created").
							setLong(0, gameId).setInteger(1, roundNum);
		return query.list();
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<GameComment> findBeforeQuestionByRound(long gameId, int roundNum) {
		Query query = sessionFactory.getCurrentSession().
				createQuery("from GameComment as comment where comment.player.game.id=? " + 
							"and roundNumber=? and afterQuestion=? order by comment.created").
							setLong(0, gameId).
							setInteger(1, roundNum).
							setBoolean(2, false);
		return query.list();
	}

	
	
}
