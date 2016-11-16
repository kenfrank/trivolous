package com.trivolous.game.dao.memory;

import java.util.ArrayList;
import java.util.List;

import com.trivolous.game.dao.GameCommentDao;
import com.trivolous.game.domain.GameComment;

public class MemoryGameCommentDao implements GameCommentDao {
	static private List<GameComment> comments = new ArrayList<GameComment>();
	static int id = 1;
	
	@Override
	public void create(GameComment gameComment) {
		gameComment.setId(id++);
		comments.add(gameComment);		
	}

	@Override
	public List<GameComment> findAll() {
		return comments;
	}

	@Override
	public List<GameComment> findByGame(long gameId) {
		List<GameComment> commentsForGame = new ArrayList<GameComment>();
		for (GameComment gc : comments)
		{
			if (gc.getPlayer().getGame().getId() == gameId)
			{
				commentsForGame.add(gc);
			}
		}
		return commentsForGame;
	}

	@Override
	public List<GameComment> findAllByRound(long gameId, int roundNum) {
		return null;
	}

	@Override
	public List<GameComment> findBeforeQuestionByRound(long gameId, int roundNum) {
		return null;
	}

}
