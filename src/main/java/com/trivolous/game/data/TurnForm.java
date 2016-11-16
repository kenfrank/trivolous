package com.trivolous.game.data;

import java.io.Serializable;


public class TurnForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Integer getAnswer() {
		return answer;
	}
	public void setAnswer(Integer answer) {
		this.answer = answer;
	}
	
	public void setSecondsLeft(long secondsLeft) {
		this.secondsLeft = secondsLeft;
	}
	public long getSecondsLeft() {
		return secondsLeft;
	}

	private Integer answer = -1;
	private long secondsLeft = 0;
}
