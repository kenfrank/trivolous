package com.trivolous.game.data;

import com.trivolous.game.domain.Member;


public class MemberData {

	private long id;
	private String firstName = "";
	private String lastName = "";
	private String name = "";
	private String email = null;
	private String password = "";
	private String facebookId = null;
	private long defaultGameId = -1;
	private String imageUrl = "";	

	public MemberData() {}	
	public MemberData(Member m) {
		this.id = m.getId();
		this.firstName = m.getFirstName();
		this.lastName = m.getLastName();
		this.email = m.getEmail();
		this.password = m.getPassword();
		this.facebookId = m.getFacebookId();
		this.defaultGameId = m.getDefaultGameId();
		this.name = m.getName();
		this.imageUrl = m.getImageUrl();
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFacebookId() {
		return facebookId;
	}
	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}
	public long getDefaultGameId() {
		return defaultGameId;
	}
	public void setDefaultGameId(long defaultGameId) {
		this.defaultGameId = defaultGameId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}
