package com.moirai.client;

import java.util.List;

public class Constant {
	
	public static String ID="1";
    public static String USERNAME="emma";
    public static String PASSWORD;
    public static int first=0;
    public static boolean isSetting = false;
    public static boolean setBlind = true;
    //0==看得见，手操�?
    //1==看不见，语音操作
    //2==看得见，手语操作

    //1、先去对应xml加一个imgageview，设置为fill_parent和gone
    //2、再在对应java里判断id是否==1，再设置手势和imageview可见
    //3、再消息处理里添加事件处�?
}
