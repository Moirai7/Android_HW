package com.navi.model;

public class RequestFriend {
	private String sendName;
	private String sendTime;
	
	public RequestFriend(String s,String t){
		this.sendName = s;
		this.sendTime = t;
	}
	
	public String getSendName() {
		return sendName;
	}
	public void setSendName(String sendName) {
		this.sendName = sendName;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	
}
