package com.trivolous.game.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
public class GameComment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private long id = -1;
	

	@Column
	private Date created = new Date();

	@Column(length=1000)
	private String text = "";

	@Column
	private Integer roundNumber;

	@ManyToOne
	@JoinColumn(name="player_fk")
	private Player player;
	
	@Column
	private boolean afterQuestion;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isAfterQuestion() {
		return afterQuestion;
	}

	public void setAfterQuestion(boolean afterQuestion) {
		this.afterQuestion = afterQuestion;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Integer getRoundNumber() {
		return roundNumber;
	}

	public void setRoundNumber(Integer roundNumber) {
		this.roundNumber = roundNumber;
	}


}
