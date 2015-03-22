package com.moirai.model;

public class Friends {
	private String send;
	private String reciver;
	private int state;
	public Friends(){
		
	}
	public Friends(String send,String reciver,int state){
		this.send=send;
		this.reciver=reciver;
		this.state=state;
	}
	public Friends(String send,String reciver){
		this.send=send;
		this.reciver=reciver;
	}
	public String getSend() {
		return send;
	}
	public void setSend(String send) {
		this.send = send;
	}
	public String getReciver() {
		return reciver;
	}
	public void setReciver(String reciver) {
		this.reciver = reciver;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
}
