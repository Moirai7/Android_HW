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
}
