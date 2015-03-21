package com.navi.model;

public class History {

	private String userID;
	private String pointID;
	private String time;
	public History(){
		
	}
	public History(String userid,String pointid,String time){
		this.userID = userid;
		this.pointID = pointid;
		this.time = time;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getPointID() {
		return pointID;
	}
	public void setPointID(String pointID) {
		this.pointID = pointID;
	}

}
