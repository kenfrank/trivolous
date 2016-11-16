package com.trivolous.game.dao;

import java.util.List;

import com.trivolous.game.domain.Game;
import com.trivolous.game.domain.Invite;

public interface InviteDao {

	public abstract void create(Invite invite);
	
	public abstract List<Invite> findByGame(Game game);
}