package com.moirai.client;

public interface Config {
	static final int BASE = 7000;
	public static final int REQUEST_LOGIN = BASE + 1;
	public static final int REQUEST_REGISTER = BASE + 2;
	public static final int REQUEST_EXIT = BASE + 3;
	public static final int REQUEST_DOWNLOAD_INFO = BASE + 4;
	public static final int REQUEST_SET_INFO = BASE + 5;
	public static final int REQUEST_REQUIRE_FRIEND = BASE + 6;
	public static final int REQUEST_DOWNLOAD_FRIEND = BASE + 7;
	public static final int REQUEST_ADDFRIEND = BASE + 8;
	public static final int REQUEST_DOWNLOAD_MOMENTS = BASE + 9;
    public static final int REQUEST_IPLOAD_MOMENTS = BASE + 10;


    // 4月9日添加
    public static final int REQUEST_SEND_MESSAGE = BASE + 11;
    public static final int REQUEST_GET_MESSAGE = BASE + 12;



	public static final int CON_SUCCESS = 2002;
	public static final int SUCCESS = 2000;  
	public static final int FAIl = 2001;    
	
	public static final int USER_STATE_ONLINE = 3000;  
	public static final int USER_STATE_NON_ONLINE = 3001; 
	
	public static final String RESULT = "result";
	public static final String REQUEST_TYPE = "requestType";

    public static final String TAG = "lanlan";

    public static final int ACK_SERVICE = 120;
    public static final int ACK_CON_SUCCESS = 101;
    public static final int ACK_DOUBLE_CLICK = 102;
    public static final int ACK_LONG_CLICK = 103;
    public static final int ACK_CLICK = 104;
    public static final int ACK_TOP = 105;
    public static final int ACK_LEFT = 106;
    public static final int ACK_RIGHT = 107;
    public static final int ACK_DOWN = 108;
    public static final int ACK_NONE = 109;
    public static final int ACK_MAIN_WELCOME = 113;
    public static final int ACK_TALK_USERNAME = 120;
    public static final int ACK_LISTEN_USERNAME = 121;
    public static final int ACK_TALK_PASSWORD = 122;
    public static final int ACK_LISTEN_PASSWORD = 123;
    public static final int ACK_LOGIN_SUCCESS_RETURN = 124;

    public static final int ACK_REGISTER_USERNAME = 201;
    public static final int ACK_START_REGISTER = 202;
    public static final int ACK_REGISTER_PASSWORD_1 = 203;
    public static final int ACK_REGISTER_PASSWORD_2 = 204;
    public static final int ACK_REGISTER_USERNAME_TIP = 205;
    public static final int ACK_REGISTER_PASSWORD_1_TIP = 206;
    public static final int ACK_REGISTER_PASSWORD_2_TIP = 207;
    public static final int ACK_REGISTER_SUCCESS = 208;
    public static final int ACK_REGISTER_FAILED = 209;
    public static final int ACK_SHAKE_TIP = 210;
    public static final int ACK_SHAKE_RESULT = 211;
    public static final int ACK_SHAKE_test = 212;
    public static final int ACK_SHAKE_TIP_CANCEL = 213;
    public static final int ACK_SHAKE_ANSWER = 214;
    public static final int ACK_LIST_READ = 300;
    public static final int ACK_TALK_START = 215;
    public static final int ACK_TALKING = 301;

    //摇一摇
    public static final int RESULT_YAOYIYAO = 401;
    public static final int RESULT_ADDFRIEND = 402;
}
