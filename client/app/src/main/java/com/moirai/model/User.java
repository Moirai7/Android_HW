package com.moirai.model;

//这是�?��实体�?
public class User {
	private String username;//用户�?
	private String password;//密码
	private int type; //鍦ㄤ笉鍦ㄧ嚎鐨勭姸锟�

	public int getType() {
		return type;
	}

	public void setType(int state) {
		this.type = state;
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
