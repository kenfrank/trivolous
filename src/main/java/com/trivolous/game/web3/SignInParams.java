package com.trivolous.game.web3;

public class SignInParams {
	private String email;
	private String password;
	private Boolean remember = false;
	
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
	public Boolean getRemember() {
		return remember;
	}
	public void setRemember(Boolean remember) {
		this.remember = remember;
	}
}
