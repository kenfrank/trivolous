package com.trivolous.game.domain;

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
public class QueuedQuestion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private long id;
	
	@ManyToOne
	@JoinColumn(name="player_fk")
	private Player player;

	@ManyToOne
	@JoinColumn(name="question_fk")
	private Question question;

	@Column
	private int rank;
	
	public Question getQuestion() {
		return question;
	}
	public void setQuestion(Question question) {
		this.question = question;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getId() {
		return id;
	}
	public void setPlayer(Player player) {
		this.player = player;
		player.getQueuedQuestions().add(this);
	}
	public Player getPlayer() {
		return player;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public int getRank() {
		return rank;
	}
}
