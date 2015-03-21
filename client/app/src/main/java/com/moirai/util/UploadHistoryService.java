package com.moirai.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.navi.client.Conmmunication;
import com.navi.client.Constant;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class UploadHistoryService extends Service {

	private static String TAG = "Upload";

	private Toast mToast;

	private int CURRENT_ACK = -1;

	private Queue<BDLocation> loc_q = new ArrayBlockingQueue<BDLocation>(17);
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();

	private TimerTask task;
	private Timer timer;
	private Conmmunication con= Conmmunication.newInstance();

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate() executed");

		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setAddrType("all"); // 设置有返回值
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		task = new TimerTask() {
			public void run() {
				if(loc_q.size()>0){
					float loc_fl = loc_q.poll().getRadius();
					String loc_str = String.valueOf(loc_fl);
					SimpleDateFormat formatter = new SimpleDateFormat    ("yyyy年MM月dd日    HH:mm:ss     ");     
					Date curDate = new Date(System.currentTimeMillis());//获取当前时间     
					String  str  =  formatter.format(curDate);
					con.setHistory(Constant.userName, loc_str, str);
					
					
				}
				
			}
		};
		
		timer = new Timer(true);

		new Thread( new Runnable() {     
		    public void run() {     
		    	 
		    	timer.schedule(task,1000, 60000);
				//con.setHistory("1", "1", "1");

		     }            
		}).start();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand() executed");

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy() executed");

	}

	private void showTip(final String str) {
		// runOnUiThread(new Runnable() {
		// @Override
		// public void run() {
		// mToast.setText(str);
		// mToast.show();
		// }
		// });
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			if (location == null) {
				return;
			}

			if (loc_q.size() == 5) {
				loc_q.poll();
			}

			loc_q.add(location);
			
			

		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}