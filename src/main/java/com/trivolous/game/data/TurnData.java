package com.trivolous.game.data;

import java.util.Date;

import com.trivolous.game.domain.Turn;
import com.trivolous.game.domain.Turn.Reason;

public class TurnData {

	public static final int GAVEUP = 99;
	public static final int TIMEOUT = 100;
	
	private long id;
	private Integer choice = -1;
	// TODO -- not sure if this should be in here, or package Turn with playerid in map
	private String playerName;
	private long playerId;
	private Integer timeToAnswer;
	private Integer score;
	private Date created;
	private Reason reason;
	private int points;
	private boolean isAsker;
	private Integer bet = 1;

	public TurnData(Turn t) {
		this.id = t.getId();
		this.choice = t.getChoice();
		// TODO -- not sure if this should be in here, or package Turn with playerid in map
		this.playerName = t.getPlayer().getName();
		this.playerId = t.getPlayer().getId();
		this.timeToAnswer = t.getTimeToAnswer();
		this.score = t.getScore();
		this.created = t.getCreated();
		this.reason = t.getReason();
		this.points = t.getPoints();
		this.isAsker = t.getIsAsker();
		this.bet = t.getBet();
	}

	public long getId() {
		return id;
	}

	public Integer getChoice() {
		return choice;
	}

	public String getPlayerName() {
		return playerName;
	}

	public long getPlayerId() {
		return playerId;
	}

	public Integer getTimeToAnswer() {
		return timeToAnswer;
	}

	public Integer getScore() {
		return score;
	}

	public Date getCreated() {
		return created;
	}

	public Reason getReason() {
		return reason;
	}

	public int getPoints() {
		return points;
	}

	public boolean isAsker() {
		return isAsker;
	}

	public Integer getBet() {
		return bet;
	}	
}
