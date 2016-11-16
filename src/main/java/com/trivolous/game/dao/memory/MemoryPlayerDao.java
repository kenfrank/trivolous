package com.trivolous.game.dao.memory;

import java.util.ArrayList;
import java.util.List;

import com.trivolous.game.dao.PlayerDao;
import com.trivolous.game.domain.Member;
import com.trivolous.game.domain.Player;

public class MemoryPlayerDao implements PlayerDao {
	
	static ArrayList<Player> players = new ArrayList<Player> ();
	static int id = 1;
	@Override
	public void create(Player player)
	{
		player.setId(id++);
		players.add(player);
	}

	@Override
	public void remove(Player player)
	{
		// remove from game also.  I assume in hibernate this happens automatically?
		player.getGame().getPlayers().remove(player);
		players.remove(player);
	}

	@Override
	public Player findById(long id)
	{
		for (Player p : players)
		{
			if (p.getId() == id) return p;
		}
		return null;
	}
	
	List<Player> findAll() 
	{
		return players;
	}

	@Override
	public List<Player> findByMember(Member member) {
		return null;
	}

}
