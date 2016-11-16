package com.trivolous.game.dao.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.trivolous.game.dao.PlayerDao;
import com.trivolous.game.domain.Member;
import com.trivolous.game.domain.Player;

@Repository
public class HibernatePlayerDao implements PlayerDao {
	private SessionFactory sessionFactory;
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}	
	
	@Override
	public void create(Player player)
	{
		sessionFactory.getCurrentSession().saveOrUpdate(player);
	}
	
	@Override
	public void remove(Player player)
	{
		sessionFactory.getCurrentSession().createQuery("delete Turn where player_fk=?").
		setLong(0,player.getId()).executeUpdate();

		sessionFactory.getCurrentSession().createQuery("delete QueuedQuestion where player_fk=?").
		setLong(0,player.getId()).executeUpdate();

		sessionFactory.getCurrentSession().createQuery("delete GameComment where player_fk=?").
		setLong(0,player.getId()).executeUpdate();

		sessionFactory.getCurrentSession().createQuery("delete Round where player_fk=?").
		setLong(0,player.getId()).executeUpdate();

		sessionFactory.getCurrentSession().createQuery("delete Player where id=?").
			setLong(0,player.getId()).executeUpdate();
		
	}
	
	@Override
	public Player findById(long id)
	{
		return (Player)sessionFactory.getCurrentSession().get(Player.class, id);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Player> findByMember(Member member) {
		Query query = sessionFactory.getCurrentSession().
			createQuery("from Player where member_fk=?").
			setEntity(0, member);
		return query.list();
	}
	
	
	
	
}
