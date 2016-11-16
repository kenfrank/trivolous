package com.trivolous.game.dao.memory;

import java.util.ArrayList;

import com.trivolous.game.dao.QuestionDao;
import com.trivolous.game.domain.Question;

public class MemoryQuestionDao implements QuestionDao {
	
	static ArrayList<Question> questions = new ArrayList<Question>();
	static int id = 1;


	@Override
	public void create(Question q)
	{
		q.setId(id++);
		questions.add(q);
	}

	@Override
	public Question findById(long id) {
		for (Question q : questions)
		{
			if (q.getId() == id) 
			{
				return q;
			}
		}
		return null;
	}
}
