package com.navi.model;

public class Path {

	private String pointID;
	private String streetID;
	private String pointname;
	private String pointSurroundingInfo;
	private String pointSurroundingStreet;
	private String pointLongitude;
	private String pointLatitude;
	private String type;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPointID() {
		return pointID;
	}
	public void setPointID(String pointID) {
		this.pointID = pointID;
	}
	public String getStreetID() {
		return streetID;
	}
	public void setStreetID(String streetID) {
		this.streetID = streetID;
	}
	public String getPointname() {
		return pointname;
	}
	public void setPointname(String pointname) {
		this.pointname = pointname;
	}
	public String getPointSurroundingInfo() {
		return pointSurroundingInfo;
	}
	public void setPointSurroundingInfo(String pointSurroundingInfo) {
		this.pointSurroundingInfo = pointSurroundingInfo;
	}
	public String getPointSurroundingStreet() {
		return pointSurroundingStreet;
	}
	public void setPointSurroundingStreet(String pointSurroundingStreet) {
		this.pointSurroundingStreet = pointSurroundingStreet;
	}
	public String getPointLongitude() {
		return pointLongitude;
	}
	public void setPointLongitude(String pointLongitude) {
		this.pointLongitude = pointLongitude;
	}
	public String getPointLatitude() {
		return pointLatitude;
	}
	public void setPointLatitude(String pointLatitude) {
		this.pointLatitude = pointLatitude;
	}

}
