package com.trivolous.game.dao;

import java.util.List;

import com.trivolous.game.domain.GameComment;

public interface GameCommentDao {

	public abstract void create(GameComment gameComment);

	public abstract List<GameComment> findAll();

	public abstract List<GameComment> findByGame(long gameId);

	public abstract List<GameComment> findAllByRound(long gameId, int roundNum);

	public abstract List<GameComment> findBeforeQuestionByRound(long gameId, int roundNum);

}