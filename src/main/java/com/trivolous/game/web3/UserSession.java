package com.trivolous.game.web3;

import java.io.Serializable;
import java.util.HashMap;

import com.trivolous.game.data.GameDescriptionData;
import com.trivolous.game.data.MemberData;
import com.trivolous.game.data.PlayerData;

/**
 * TODO (high) -- this is only used to get gameid and memberid.  just put those in the 
 * session, and remove this class?  I suppose it is ok to leave this for future use.
 * But I want to take a good look at it again.  Also look in game interceptor to see 
 * what can be done.
 */

public class UserSession implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long memberId;
	private long gameId;
	private boolean isProfileComplete = false;
	private String memberFirstName;
	private GameDescriptionData gameDesc = null;
	private MemberData memberData = null;
	private PlayerData playerData = null;
	private HashMap<String,String>  temps = new HashMap<String, String>();
	
	public GameDescriptionData getGameDesc() {
		return gameDesc;
	}

	public void setGameDesc(GameDescriptionData gameDesc) {
		this.gameDesc = gameDesc;
	}

	public UserSession(MemberData memberData)
	{
		this.memberId = memberData.getId();
		gameId = memberData.getDefaultGameId();
		isProfileComplete = !memberData.getPassword().isEmpty();
		memberFirstName = memberData.getFirstName();
	}

	public long getMemberId() {
		return memberId;
	}

	public void setMemberId(long memberId) {
		this.memberId = memberId;
	}
	public void setGameId(long gameId) {
		this.gameId = gameId;
	}
	public long getGameId() {
		return gameId;
	}
	public void setIsProfileComplete(boolean isProfileComplete) {
		this.isProfileComplete = isProfileComplete;
	}
	public boolean getIsProfileComplete() {
		return isProfileComplete;
	}
	public String getMemberFirstName() {
		return memberFirstName;
	}
	public void setMemberFirstName(String memberFirstName) {
		this.memberFirstName = memberFirstName;
	}

	public PlayerData getPlayerData() {
		return playerData;
	}

	public void setPlayerData(PlayerData playerData) {
		this.playerData = playerData;
	}

	public MemberData getMemberData() {
		return memberData;
	}

	public void setMemberData(MemberData memberData) {
		this.memberData = memberData;
	}

	public HashMap<String, String> getTemps() {
		return temps;
	}
}
