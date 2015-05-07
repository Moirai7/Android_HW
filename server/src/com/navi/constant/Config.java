package com.navi.constant;

import java.util.Collection;

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
        public static final int REQUEST_UPLOAD_MOMENTS = BASE + 10;
	//4��23�ռӵ�
    public static final int REQUEST_SEND_MESSAGE = BASE + 11;
    public static final int REQUEST_GET_MESSAGE = BASE + 12;
    public static final int REQUEST_DOWNLOAD_NEWINFO = BASE + 13;
    public static final int RESULT_YAOYIYAO = 401;
   // public static final int RESULT_ADDFRIEND = 402;
        
	public static final int CON_SUCCESS = 2002;
	public static final int SUCCESS = 2000;  //成功
	public static final int FAIl = 2001;    
	
	public static final int USER_STATE_ONLINE = 3000;  
	public static final int USER_STATE_NON_ONLINE = 3001; 
	
	public static final String RESULT = "result";
	public static final String REQUEST_TYPE = "requestType";

        public static final String TAG = "lanlan";
}
