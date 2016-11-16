package com.trivolous.game.dao.memory;

import java.util.ArrayList;
import java.util.List;

import com.trivolous.game.dao.GameDao;
import com.trivolous.game.domain.Game;

public class MemoryGameDao implements GameDao {
	
	static ArrayList<Game> games = new ArrayList<Game> ();
	static int id = 1;

	@Override
	public void create(Game game)
	{
		game.setId(id++);
		games.add(game);
	}

	@Override
	public Game findById(long id)
	{
		for (Game g : games)
		{
			if (g.getId() == id) 
			{
				return g;
			}
		}
		return null;
	}

	@Override
	public List<Game> findAllActive() {
		List<Game> gl = new ArrayList<Game>();
		for (Game g : games)
		{
			if (g.getIsActive()) 
			{
				gl.add(g);
			}
		}
		return gl;
	}

	@Override
	public List<Game> findAll() {
		return games;
	}

	@Override
	public void delete(Game game) {
		games.remove(game);
	}

}
