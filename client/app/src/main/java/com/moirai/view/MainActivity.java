package com.navi.blind;


import java.util.ArrayList;
import java.util.List;

import com.navi.baidu.BaiduApplication;
import com.navi.baidu.LocationActivity;
import com.navi.baidu.RoutePlanActivity;
import com.navi.client.Config;
import com.navi.client.Conmmunication;
import com.navi.client.Constant;
import com.navi.model.History;
import com.navi.util.Database;
import com.navi.util.UploadHistoryService;
import com.navi.voice.VoiceService;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements OnClickListener {  
	  
    private Button startService;  
    
    private Button stopService;  
  
    private Button bindService;  
  
    private Button unbindService;  
    private boolean con_flag = false;
  
    private static VoiceService.MyBinder myBinder;  
    
    private Intent intent_main_service;
    
    private BaiduApplication app;
    
    
//    private Handler handler = new Handler(){
//    	@Override
//    	public void handleMessage(Message msg) {
//		
//			switch(msg.what){
//			case Config.INTENT_LOC:
//            	Intent intent_loc = new Intent(MainActivity.this,
//						LocationActivity.class);// ƽ：
//				startActivityForResult(intent_loc, Config.REQ_LOC);  
//				break;
//			default:
//				break;
//			}
//    	}
//    };	
  
    private ServiceConnection connection = new ServiceConnection() {  
  
        @Override  
        public void onServiceDisconnected(ComponentName name) {  
        	con_flag = false;
        }  
  
        @Override  
        public void onServiceConnected(ComponentName name, IBinder service) {  
            myBinder = (VoiceService.MyBinder) service;  
            
            app.setBinder(myBinder);
            
            myBinder.setBinderFlagOn();  
            con_flag = true;
            Log.v("tag", "bind");
        }  
    };  
  
	private Toast mToast;


    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_main);  
        startService = (Button) findViewById(R.id.start_service);  
        stopService = (Button) findViewById(R.id.stop_service);  
        bindService = (Button) findViewById(R.id.bind_service);  
        unbindService = (Button) findViewById(R.id.unbind_service);  
        startService.setOnClickListener(this);  
        stopService.setOnClickListener(this);  
        bindService.setOnClickListener(this);  
        unbindService.setOnClickListener(this);  
        
        intent_main_service=  new Intent(this, VoiceService.class);  
        app = (BaiduApplication) getApplication();
        con = Conmmunication.newInstance();
        
    }  
  
    @Override
    protected void onStart() {

        // bind
        startService(intent_main_service);
        bindService(intent_main_service, connection, BIND_AUTO_CREATE);  
    	super.onStart();
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	//unbindService(connection); 
		if(con_flag){
			//unbindService(connection);
		}
		
    	super.onPause();
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
        //bindService(intent_main_service, connection, BIND_AUTO_CREATE);  
    	super.onResume();
    }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	//unbindService(connection); 
    	super.onStop();
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	//unregisterReceiver(msgReceiver);
    	stopService(intent_main_service);
		if(con_flag){
			unbindService(connection);
		}
    	super.onDestroy();
    }
    @Override  
    public void onClick(View v) {  
        switch (v.getId()) {  
        case R.id.start_service:  
//            Intent startIntent = new Intent(this, UploadHistoryService.class);  
//            startService(startIntent);  
//        	Database db = Database.getInstance(this);
//        	db.getChild("007");
        	//con.getHistory("2");
        	Intent intent_route = new Intent(MainActivity.this,
					RoutePlanActivity.class);//
        	
			startActivity(intent_route);        	
            break;  
        case R.id.stop_service:  
           // Intent stopIntent = new Intent(this, VoiceService.class);  
           // stopService(stopIntent);  
        	con.getHistory("2");
            break;  
        case R.id.bind_service:  
        	con.download();
        	myBinder.StopListen();
        	myBinder.StartListen();
            break;  
        case R.id.unbind_service:  
        	Intent intent_loc = new Intent(MainActivity.this,
					LocationActivity.class);// ƽ：
        	intent_loc.putExtra("requestCode", Config.REQ_SM);
			startActivityForResult(intent_loc, Config.REQ_SM);
            break;  

        default:  
            break;  
        }  
    }  
    
	private void showTip(final String str) {
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == Config.REQ_LOC && resultCode == Config.RES_LOC) {			
			Bundle bundle = data.getExtras();
			String positionInfo = bundle.getString("positionInfo");
			myBinder.StartRead(positionInfo);
		} else if(requestCode == Config.REQ_SM && resultCode == Config.RES_SM){
			Bundle bundle = data.getExtras();
			String positionInfo = bundle.getString("positionInfo");
			Toast.makeText(this, positionInfo, Toast.LENGTH_LONG).show();
			SendAMessage(positionInfo);
		} 
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void processMessage(Message message) {
		// TODO Auto-generated method stub
		switch(message.what){
//		case Config.INTENT_LOC:
//        	Intent intent_loc = new Intent(MainActivity.this,
//					LocationActivity.class);// ƽ：
//			startActivityForResult(intent_loc, Config.REQ_LOC);  
//			break;
//		case Config.ACK_START_ROUTE:
//			Intent intent_route = new Intent(MainActivity.this,
//					RoutePlanActivity.class);// ƽ：
//			startActivity(intent_route);
		case Config.REQUEST_GATHISTORY:
			List<History> history = (ArrayList) message.obj;

			ArrayList<String> loc = new ArrayList<String>();
			ArrayList<String> time = new ArrayList<String>();
			
			for(History h: history){
				loc.add(h.getPointID());
				time.add(h.getTime());
			}
			
			Bundle bd = new Bundle();
			bd.putStringArrayList("loc", loc);
			bd.putStringArrayList("time", time);
			
			Intent intent = new Intent(this,ShowPositionActivity.class);
			intent.putExtra("bd_history", bd);
			startActivity(intent);
			
			break;
		case Config.REQUEST_SAVEHISTORY:
			showTip("上传成功");
			break;
		case Config.ACK_SERVICE:
            String finalresult = (String) message.obj;
            
            //Log.v("语音识别结果", finalresult);
            
            if(finalresult.indexOf("定位") >= 0 || finalresult.indexOf("另外") >= 0 
					|| finalresult.indexOf("因为")>=0
					|| finalresult.indexOf("并未") >= 0
					|| finalresult.indexOf("地位") >= 0
					|| finalresult.indexOf("并非") >= 0){
            	
            	Intent intent_loc = new Intent(MainActivity.this,
    					LocationActivity.class);// ƽ：
            	intent_loc.putExtra("requestCode", Config.REQ_LOC); 
    			startActivityForResult(intent_loc, Config.REQ_LOC);  
        	
            } else if(finalresult.indexOf("路线") >= 0 || finalresult.indexOf("规划") >= 0 
					|| finalresult.indexOf("公交")>=0){
            	Intent intent_route = new Intent(MainActivity.this,
    					RoutePlanActivity.class);// ƽ：
            	
    			startActivity(intent_route);
            }  else if(finalresult.indexOf("发")>=0 || finalresult.indexOf("短信")>=0){
            	Intent intent_loc = new Intent(MainActivity.this,
    					LocationActivity.class);// ƽ：
            	intent_loc.putExtra("requestCode", Config.REQ_SM);
    			startActivityForResult(intent_loc, Config.REQ_SM);
    			
            }
            break;
			
		default:
			break;
		}		
	}
	
	public void SendAMessage(String location){
		
		PendingIntent paIntent;
	    SmsManager smsManager;
		paIntent = PendingIntent.getBroadcast(this, 0, new Intent(), 0); 
        smsManager = SmsManager.getDefault();
        
        String a1 = Constant.detail;
        String a2 = Constant.receiver;
        
        smsManager.sendTextMessage("18811442491", "tom", "http://blind.moirai.cn/?id=" + location, paIntent, 
                null); 
	}
	
	public static VoiceService.MyBinder getBinder(){
		return myBinder;
	}
  
}  