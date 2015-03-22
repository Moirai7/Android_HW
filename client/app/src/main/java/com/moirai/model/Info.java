package com.moirai.model;

/**
 * Created by moirai on 2015/3/21.
 */
public class Info {
    private String sendUser;
    private String receiver;
    private String detail;
    private String time;

    public Info(){
    	
    }
    
    public Info(String send,String rece,String detail){
    	this.receiver=rece;
    	this.sendUser=send;
    	this.detail=detail;
    }
    public String getSendUser() {
        return sendUser;
    }

    public void setSendUser(String sendUser) {
        this.sendUser = sendUser;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
