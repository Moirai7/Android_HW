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

    /**
     * 下载消息   //这个应该是获取消息列�?
     */
    public void downloadInfo(String userName) {
        netWorker.downloadInfo(userName);
    }

    /**
     * 下载消息   //这个是获取和某一个人的消息列�?
     */
    public void getmessage(int userid1, int userid2) {
        netWorker.getmessage(userid1, userid2);
    }

    /*
    发�?�消�?,4-14
      */
    public void sendInfo(int sendid, int receiverid, String message) {

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
        netWorker.sendRequestFriend(id, df.format(new Date()));
    }

    /**
     * 下载朋友
     */
    public void downloadFriend(String userName) {

    }

    /**
     * 添加朋友
     */
    public void addFriend(String username, String otherID, int answer) {
        netWorker.addFriend(username, otherID, answer);
    }

    /**
     * 下载朋友�?
     */
    public void downloadMoments(String username) {

    }

    /**
     * 刷新朋友�?
     */
    public void queryMoments(String userName, String start, String end) {

    }

    /**
     * 上传朋友�?
     */
    public void uploadMoments(String userName, String detail) {

    }

    // 发�?��??出游戏请�?
    public void exitGame() {
        netWorker.exitGame();
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
