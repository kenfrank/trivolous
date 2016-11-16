package com.trivolous.game.dao.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.trivolous.game.dao.GameDao;
import com.trivolous.game.domain.Game;

@Repository
public class HibernateGameDao implements GameDao {
	private SessionFactory sessionFactory;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void create(Game game) {
		sessionFactory.getCurrentSession().saveOrUpdate(game);

	}

	@Override
	public void delete(Game game) {
		// TODO (low) -- this needs some work, and to be tested.
		
		if (game.getPlayers().size() > 0)
		{
			// do not delete if there are any players.
			// players must be removed first.
			return;
		}

		sessionFactory.getCurrentSession().createQuery("delete Round where game_fk=?").
		setLong(0,game.getId()).executeUpdate();

		sessionFactory.getCurrentSession().createQuery("delete Game where id=?").
			setLong(0,game.getId()).executeUpdate();
	}
	
	@Override
	public Game findById(long id) {
		return (Game)sessionFactory.getCurrentSession().get(Game.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Game> findAllActive() {
		Query query =  sessionFactory.getCurrentSession().createQuery(
		"from Game where isActive=?").setBoolean(0, true);
		return query.list();
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Game> findAll() {
		Query query =  sessionFactory.getCurrentSession().createQuery("from Game");
		return query.list();
	}




}
