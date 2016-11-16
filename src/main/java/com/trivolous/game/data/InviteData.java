package com.trivolous.game.data;

import com.trivolous.game.domain.Invite;


public class InviteData {

	private long id;
	// may be null if player invited by email
	private String name; 
	// may be null if player invited by FB
	private String email;
	// may be null if player invited by member or email
	private String facebookId;
	// may be null if invited player is not a member
	private Long memberId;
	// is always set to invited 
	private Long gameId;

	public InviteData(Invite i) {
		this.id = i.getId();
		this.name = i.getName();
		this.email = i.getEmail();
		this.facebookId = i.getFacebookId();
		this.memberId = i.getMember() == null ? null : i.getMember().getId();
		this.gameId = i.getGame() == null ? null : i.getGame().getId();
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public Long getGameId() {
		return gameId;
	}	
}
