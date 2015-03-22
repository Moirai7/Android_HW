package com.moirai.model;

//杩欐槸锟�锟斤拷瀹炰綋锟�
public class User {
	private String username;//鐢ㄦ埛锟�
	private String password;//瀵嗙爜
	private String type; //鍦ㄤ笉鍦ㄧ嚎鐨勭姸锟�

	public String getType() {
		return type;
	}

	public void setType(String state) {
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
