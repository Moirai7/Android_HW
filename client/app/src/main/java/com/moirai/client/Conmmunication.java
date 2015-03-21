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
     * 服务器要设置状态
	 */
	public void login(String userName, String password) {

	}

	/**
	 * 注册
	 */
	public void register(String userName, String password,String type) {

	}

	/**
	 * 下载消息
     *
	 */
	public void downloadInfo(String userName){

	}
	/**
	 * 设置消息状态
	 */
	public void setInfo(String infoID){

	}

    /**
     * 确认联系人
     */
    public void requireFriend(String ID1,String ID2){
        netWorker.download();
    }
    /**
     * 下载朋友
     */
    public void downloadFriend(String userName){

    }
	/**
	 *  添加朋友
	 */
	public void addFriend(String username,String otherID){

	}

    /**
     * 下载朋友圈
     */
    public void downloadMoments(String username){

    }

    /**
     * 刷新朋友圈
     */
    public void queryMoments(String userName,String start,String end){

    }

    /**
     * 上传朋友圈
     */
    public void uploadMoments(String userName,String detail){

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

}
