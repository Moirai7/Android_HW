package com.moirai.model;

/**
 * Created by moirai on 2015/3/21.
 */
public class Moments {
    private String sendUser;
    private String time;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;

    public Moments(){
    	
    }
    public Moments(String s,String r){
    	this.sendUser=s;
    	this.time=r;
    }

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

}
