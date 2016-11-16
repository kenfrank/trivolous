package com.trivolous.game.dao;

import java.util.List;

import com.trivolous.game.domain.Member;
import com.trivolous.game.domain.Player;

public interface PlayerDao {

	public abstract void create(Player player);

	public abstract void remove(Player player);

	public abstract Player findById(long id);

	public abstract List<Player> findByMember(Member member);

}