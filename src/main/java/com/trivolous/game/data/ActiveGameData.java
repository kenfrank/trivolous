package com.trivolous.game.data;

import java.util.Date;

import com.trivolous.game.domain.Game;
import com.trivolous.game.domain.Player;
import com.trivolous.game.domain.Round;

// TODO -- I think will need to player counter and active player count.
// this information is only valid when game is active.
public class ActiveGameData {
	// dynamic properties
	private long gameId;

	private int playersLeftToAnswer;
	private String askerPlayerName;
	private Integer roundNumber;
	private long secondsSinceTurn;
	private long secondsSinceQuestion;
	private long secondsWaiting;
	private Date waitDate;
	private long lastCompletedRoundId;
	private long currentRoundId;
	private Integer askerCycle;
	private boolean areAllPlayersRegistered;
	private boolean openRegistrationAvailable;
	// TODO -- moved to description also.  remove from here if not used.
	private com.trivolous.game.domain.Game.STATUS status;
	private String statusString;
	private int totalRounds;
		
    public ActiveGameData(Game g) {
    	this.gameId = g.getId();

    	this.playersLeftToAnswer = g.getPlayersLeftToAnswer();
    	Player asker = g.getAsker();
    	this.askerPlayerName = asker!=null ? asker.getName() : null;
    	this.roundNumber = g.getRoundNumber();
    	this.secondsSinceTurn = g.getSecondsSinceTurn();
    	this.secondsSinceQuestion = g.getSecondsSinceQuestion();
    	this.secondsWaiting = g.getSecondsWaiting();
    	this.waitDate = g.getWaitDate();
    	Round round = g.getLastCompletedRound();
    	this.lastCompletedRoundId = round != null ? round.getId() : -1;
    	round = g.getCurrentRound();
    	this.currentRoundId = round != null ? round.getId() : -1;
    	this.askerCycle = g.getAskerCycle();
    	this.areAllPlayersRegistered = g.getAreAllPlayersRegistered();
    	this.openRegistrationAvailable = g.getOpenRegistrationAvailable();
       	this.status = g.getStatus();
    	this.statusString = g.getStatusString();
    	this.totalRounds = g.getTotalRounds();
    }

	public long getId() {
		return gameId;
	}

	public long getGameId() {
		return gameId;
	}


	public int getPlayersLeftToAnswer() {
		return playersLeftToAnswer;
	}

	public String getAskerPlayerName() {
		return askerPlayerName;
	}

	public Integer getRoundNumber() {
		return roundNumber;
	}

	public long getSecondsSinceTurn() {
		return secondsSinceTurn;
	}
	public long getSecondsSinceQuestion() {
		return secondsSinceQuestion;
	}
	public long getSecondsWaiting() {
		return secondsWaiting;
	}
	public Date getWaitDate() {
		return waitDate;
	}
	public long getLastCompletedRoundId() {
		return lastCompletedRoundId;
	}
	public long getCurrentRoundId() {
		return currentRoundId;
	}
	public Integer getAskerCycle() {
		return askerCycle;
	}
	public boolean isAreAllPlayersRegistered() {
		return areAllPlayersRegistered;
	}
	public boolean isOpenRegistrationAvailable() {
		return openRegistrationAvailable;
	}
	public com.trivolous.game.domain.Game.STATUS getStatus() {
		return status;
	}
	public String getStatusString() {
		return statusString;
	}	
	public int getTotalRounds() {
		return totalRounds;
	}
}
