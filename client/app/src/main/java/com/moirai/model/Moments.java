package com.moirai.model;

/**
 * Created by moirai on 2015/3/21.
 */
public class Moments {
    private String sendUser;
    private String reciver;
    public Moments(){
    	
    }
    public Moments(String s,String r){
    	this.sendUser=s;
    	this.reciver=r;
    }
    public String getReciver() {
		return reciver;
	}

	public void setReciver(String reciver) {
		this.reciver = reciver;
	}

	private String time;
    private String detail;

    public String getSendUser() {
        return sendUser;
    }

    public void setSendUser(String sendUser) {
        this.sendUser = sendUser;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
