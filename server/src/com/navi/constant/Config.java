package com.navi.constant;

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
	
	public static final int CON_SUCCESS = 2002;
	public static final int SUCCESS = 2000;  
	public static final int FAIl = 2001;    
	
	public static final int USER_STATE_ONLINE = 3000;  
	public static final int USER_STATE_NON_ONLINE = 3001; 
	
	public static final String RESULT = "result";
	public static final String REQUEST_TYPE = "requestType";

        public static final String TAG = "lanlan";
}
