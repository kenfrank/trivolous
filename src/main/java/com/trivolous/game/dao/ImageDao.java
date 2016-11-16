package com.trivolous.game.dao;

import com.trivolous.game.domain.QuestionImage;

public interface ImageDao {

	public abstract void create(QuestionImage image);

	public abstract QuestionImage findById(long id);

}