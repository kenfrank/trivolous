package com.trivolous.game.data;

import java.util.Date;

import com.trivolous.game.domain.GameComment;

public class CommentData {

	private long id;
	private Date created;
	private String text;
	private String playerName;
	private String playerImage;
	private boolean afterQuestion;

	public CommentData(GameComment c) {
		this.id = c.getId();
		this.created = c.getCreated();
		this.text = c.getText();
		this.playerName = c.getPlayer().getName();
		this.playerImage = c.getPlayer().getMember().getImageUrl();
		this.afterQuestion = c.isAfterQuestion();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public long getId() {
		return id;
	}

	public Date getCreated() {
		return created;
	}

	public boolean isAfterQuestion() {
		return afterQuestion;
	}

	public String getPlayerImage() {
		return playerImage;
	}
	
	
	
}
