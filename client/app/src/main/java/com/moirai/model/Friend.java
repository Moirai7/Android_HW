package com.moirai.model;

public class Friend {
	private String friendname;
	private int state;

	public Friend() {

	}

	public Friend(String friendname, int state) {
		this.friendname = friendname;
		this.state = state;
	}

	public Friend(String friendname) {
		this.friendname = friendname;
	}

	public String getfriendname() {
		return friendname;
	}

	public void setfriendname(String friendname) {
		this.friendname = friendname;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
