package com.trivolous.game.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Member generated by hbm2java
 */
@Entity
@Table
public class Invite implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private long id;

	@Column
	private String name = "";
	
	// note unique does not apply to null.
	@Column
	private String email = null;
	
	@Column
	private String facebookId = null;

	@Column
	private Boolean hasJoined = null;

	@ManyToOne
	@JoinColumn(name="member_fk")
	private Member member = null;

	@ManyToOne
	@JoinColumn(name="game_fk")
	private Game game = null;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}
	
	// null if not responded, false if declined, true if accepted.
	// NOTE: could also just remove once not null, if no need to track who declined.   Accepted invites will become players.
	public Boolean getHasJoined() {
		return hasJoined;
	}

	public void setHasJoined(Boolean hasJoined) {
		this.hasJoined = hasJoined;
	}

	// this is a hack so that refactoring doesnt have to extend everywhere and places that used to use member, can still do so.
	public Member toMember() {
		Member m = new Member();
		m.setEmail(email);
		m.setFacebookId(facebookId);
		if (name != null) {
			String[] names = name.split(" ");
			m.setFirstName(names[0]);
			// just in case name doesnt have space for some reason.
			String last = (name.length() > 1) ? names[1] : names[0]; 
			m.setLastName(last);
		}
		return m;
	}

	public boolean matchesMember(Member m) {

		if ((member != null) && (member.getId() == m.getId())) 
			return true;
		if ((email != null) && !email.isEmpty() && email.equals(m.getEmail()))
			return true;
		if ((facebookId != null) && !facebookId.isEmpty() && facebookId.equals(m.getFacebookId()))
			return true;
		
		return false;
	}
}
