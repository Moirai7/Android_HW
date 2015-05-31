package com.moirai.client;

import android.content.Context;

import com.moirai.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;

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

	// 将构造函数私有化，使其不能生成多个实例，防止多次连接连接服务�?
	private Conmmunication() {
		netWorker = new NetWorker();
		netWorker.start();
	}

	/**
	 * 登录 服务器要设置状�??
	 */
	public void login(User user) {
		netWorker.login(user.getUsername(), user.getPassword());
	}

	/**
	 * 注册
	 */
	public void register(User user) {
		netWorker.register(user);
	}

	// 获取和某人的最新消息
	public void getnewmessage(String name) {
		netWorker.getnewmessage(name);
	}

	/**
	 * 下载消息 //这个是获取和某一个人的消息列�?
	 */
	public void getmessage(String userid1, String userid2) {
		netWorker.getmessage(userid1, userid2);
	}

	/*
	 * 发�?�消�?,4-14
	 */
	public void sendInfo(String sendid, String receiverid, String message) {

		netWorker.sendInfo(sendid, receiverid, message);

	}

	/**
	 * 设置消息状�??
	 */
	public void setInfo(String infoID) {

	}

	private SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

	/**
	 * 请求联系�?
	 */
	public void requireFriend(String id) {
		netWorker.sendRequestFriend(id);
	}

    /**
     * 下载朋友列表
     */
    public void downloadFriend(String userName) {
        netWorker.downloadFriend(userName);
    }

	/**
	 * 添加朋友
	 */
	public void addFriend(String username, String otherID, int answer) {
		netWorker.addFriend(username, otherID, answer);
	}

	/**
	 * 下载朋友圈
	 */
	public void downloadMoments(String username) {
        netWorker.downloadMoments(username);
	}

    // 发布朋友圈
    public void uploadMoments(String name, String detail) {

        netWorker.uploadMoments(name, detail);
    }

	/**
	 * 刷新朋友圈
	 */
	public void queryMoments(String userName, String start, String end) {

	}
    // 下载新闻
    public void downloadnews() {

        netWorker.downloadnews();
    }

	/**
	 * �?出连接后，清空资�?
	 */
	public void clear() {
		netWorker.setOnWork(false);
		instance = null;
	}

	public void sendOffLine(String userName) {
		netWorker.sendOffLine(userName);
	}

}
