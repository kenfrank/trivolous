package com.trivolous.game.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Member generated by hbm2java
 */
@Entity
@Table
public class Member implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private long id;

	@Column
	private String firstName = "";
	
	@Column
	private String lastName = "";
	
	// note unique does not apply to null.
	@Column(unique=true)
	private String email = null;
	
	// may be set to 'Facebook Login Only', if user registered FB.
	// cannot be empty if registered
	@Column
	private String password = "";
	
	@OneToMany(mappedBy="member")
	private List<Player> players  = new ArrayList<Player>();

	@Column(unique=true)
	private String facebookId = null;
	
	@Column
	private String imageUrl = "";
	
		
	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public Member() {
		email = null;
		password = "";
		id = -1;
	}

	public boolean getIsRegistered() {
		return password!=null && !password.isEmpty();
	}
	
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		if (getIsNew()) {
			if (facebookId != null && !facebookId.isEmpty()) {
				return this.firstName + " " + this.lastName;
			}
			//return "Invite to " + this.email;
			return "";
		}
		return this.firstName + " " + this.lastName;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	// return true if member is new and has not been configured
	public boolean getIsNew() {
		return password.isEmpty();
	}

	// empty if not associated with facebook
	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}
	// if no game specified choose auotmatically in this order:
	// 	- game if only one
	// 	- game needing action if more than one
	// 	- active game if only one
	// 	NO - any game
	public long getDefaultGameId() {
		long gameId=0;

		if (players.size() == 1) {
			gameId = players.get(0).getGame().getId();
		} 
		else {
			for (Player p : players) {
				if (p.getIsActionRequired()) {
					gameId = p.getGame().getId();
					break;
				}
			}
		}	
		if (gameId <= 0) {
			long activeId = 0;
			long activeCount = 0;
			for (Player p : players) {
				if (p.getGame().getIsActive())	 {
					activeId = p.getGame().getId();
					activeCount++;
				}
			}
			if (activeCount == 1) gameId = activeId;
		}	
		
		return gameId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}	

}
