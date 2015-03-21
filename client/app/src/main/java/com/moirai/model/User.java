package com.moirai.model;

//这是�?��实体�?
public class User {
	private String username;//用户�?
	private String password;//密码
	private int state; //在不在线的状�?
	private String recevier;
	private String detail;
	
	public String getRecevier() {
		return recevier;
	}

	public void setRecevier(String recevier) {
		this.recevier = recevier;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
