package com.trivolous.game.dao;

import java.util.List;

import com.trivolous.game.domain.Game;

public interface GameDao {

	public abstract void create(Game game);

	public abstract Game findById(long id);

	public abstract List<Game> findAllActive();

	public abstract List<Game> findAll();

	void delete(Game game);

	
}