package com.trivolous.game.data;

import java.util.Date;

import com.trivolous.game.domain.Game;
import com.trivolous.game.domain.Game.PRIVACY;

// This is stuff that pertains to all games active/inactive.
public class GameDescriptionData {

	private long gameId;
	// Configuration -- all writable, and not changed by server.
	private String name;
	private String description;
	private Integer totalAuthorCycles;
	private Integer maxPlayers; 
	private Boolean allowOpenRegistration; // true  just for debug --  false;
	private com.trivolous.game.domain.Game.PRIVACY privacy;
	private Long masterId;
	private Integer countDown;
	
	private Date created;
	private Date endDate;
	private boolean isActive;
	private long masterPlayerId;
	private String masterName;
	private Integer numPlayers;
	private com.trivolous.game.domain.Game.STATUS status;
	
	public GameDescriptionData() {
		this.gameId=-1;
		this.name="";
		this.description="";
		this.totalAuthorCycles=1;
		this.maxPlayers=0;
		this.allowOpenRegistration=true;
		this.privacy = PRIVACY.PLAYERS_ONLY;
		this.masterId= (long) -1;
    	this.countDown = 3;		
	}	
	public GameDescriptionData(Game g) {
		this.gameId=g.getId();
		this.name=g.getName();
		this.description=g.getDescription();
		this.totalAuthorCycles=g.getTotalAuthorCycles();
		this.maxPlayers=g.getMaxPlayers();
		this.allowOpenRegistration=g.getAllowOpenRegistration();
		this.privacy = g.getPrivacy();
		this.masterId=g.getMaster().getId();
    	this.countDown = g.getCountDown();	
    	this.created = g.getCreated();
    	this.endDate = g.getEndDate();
    	this.isActive = g.getIsActive();
    	if (g.getMasterPlayer() != null) {
	    	this.masterPlayerId = g.getMasterPlayer().getId();
	    	this.masterName = g.getMasterPlayer().getName();
    	} 
    	else {
    		// this can happen if game is deleted and players are removed...
        	this.masterPlayerId = -1;
        	this.masterName = "NONE";
    	}
	    this.numPlayers = g.getPlayers().size();
       	this.status = g.getStatus();
	}
	public long getId() {
		return gameId;
	}

	public void setGameId(long gameId) {
		this.gameId = gameId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getTotalAuthorCycles() {
		return totalAuthorCycles;
	}
	public void setTotalAuthorCycles(Integer totalAuthorCycles) {
		this.totalAuthorCycles = totalAuthorCycles;
	}
	public Integer getMaxPlayers() {
		return maxPlayers;
	}
	public void setMaxPlayers(Integer maxPlayers) {
		this.maxPlayers = maxPlayers;
	}
	public Boolean getAllowOpenRegistration() {
		return allowOpenRegistration;
	}
	public void setAllowOpenRegistration(Boolean allowOpenRegistration) {
		this.allowOpenRegistration = allowOpenRegistration;
	}
	public com.trivolous.game.domain.Game.PRIVACY getPrivacy() {
		return privacy;
	}
	public void setPrivacy(com.trivolous.game.domain.Game.PRIVACY privacy) {
		this.privacy = privacy;
	}
	public Long getMasterId() {
		return masterId;
	}
	public void setMasterId(Long masterId) {
		this.masterId = masterId;
	}
	public Integer getCountDown() {
		return countDown;
	}
	public void setCountDown(Integer countDown) {
		this.countDown = countDown;
	}
	public Date getCreated() {
		return created;
	}
	public Date getEndDate() {
		return endDate;
	}
	public boolean isActive() {
		return isActive;
	}
	public long getMasterPlayerId() {
		return masterPlayerId;
	}
	public String getMasterName() {
		return masterName;
	}
	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}
	public Integer getNumPlayers() {
		return numPlayers;
	}
	public com.trivolous.game.domain.Game.STATUS getStatus() {
		return status;
	}
}
