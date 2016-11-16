package com.trivolous.game.dao;

import com.trivolous.game.domain.Question;

public interface QuestionDao {

	public abstract void create(Question q);
	public abstract Question findById(long id);

}