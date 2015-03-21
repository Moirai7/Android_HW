package com.moirai.client;

import android.content.Context;

public class Conmmunication {
	Context context;
	private NetWorker netWorker;
	public static Conmmunication instance;

	// 生成Conmmunication通信类的实例
	public static Conmmunication newInstance() {
		if (instance == null) {
			instance = new Conmmunication();
			System.out.println("连接到服务器!!! ");
		}
		return instance;
	}

	// 将构造函数私有化，使其不能生成多个实例，防止多次连接连接服务器
	private Conmmunication() {
		netWorker = new NetWorker();
		netWorker.start();
	}

	/**
	 * 登录
	 */
	public void login(String userName, String password) {
		netWorker.login(userName, password);
	}

	/**
	 * 注册
	 */
	public void register(String userName, String password) {
		netWorker.register(userName, password);
	}

	/**
	 * 下载
	 */
	public void download(){
		netWorker.download();
	}
	/**
	 * 查询一个点
	 */
	public void pathInfo(String point){
		netWorker.pathInfo(point);
	}
	/**
	 * 设置info
	 */
	public void setRequestInfo(String username,String reciver,String detail){
		netWorker.setRequestInfo(username,reciver,detail);
	}
	// 发送退出游戏请求
	public void exitGame() {
		netWorker.exitGame();
	}

	/**
	 * 退出连接后，清空资源
	 */
	public void clear() {
		netWorker.setOnWork(false);
		instance = null;
	}
	public void sendOffLine(String userName) {
		netWorker.sendOffLine(userName);
	}
	
	/**
	 * 下载历史
	 */
	public void getHistory(String username){
		netWorker.getHistory(username);
	}
	/**
	 * 下载历史
	 */
	public void setHistory(String userid,String pointID,String time){
		netWorker.saveHistory(userid,pointID,time);
	}

}
