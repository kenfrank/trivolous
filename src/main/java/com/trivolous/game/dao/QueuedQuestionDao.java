package com.trivolous.game.dao;

import com.trivolous.game.domain.QueuedQuestion;

public interface QueuedQuestionDao {

	public abstract void create(QueuedQuestion queuedQuestion);

	public abstract void delete(QueuedQuestion queuedQuestion);

	//	public abstract List<QueuedQuestion> findByPlayer(Player player);
//	
//	public abstract void remove(QueuedQuestion queuedQuestion);
	
}