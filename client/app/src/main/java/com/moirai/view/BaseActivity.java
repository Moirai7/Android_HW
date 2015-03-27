package com.moirai.view;

import java.util.LinkedList;

import com.iflytek.speech.SpeechRecognizer;
import com.iflytek.speech.SpeechSynthesizer;
import com.moirai.client.Config;
import com.moirai.client.Conmmunication;
import com.moirai.util.Database;
import com.moirai.voice.VoiceService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

public abstract class BaseActivity extends FragmentActivity {
	// 将生成的Activity都放到LinkList集合中
	protected static LinkedList<BaseActivity> queue = new LinkedList<BaseActivity>();
	public static Conmmunication con;
	public static Database db;
    // 语音识别对象。
    private SpeechRecognizer mIat;
    private Toast mToast;
    private static final String ACTION_INPUT = "com.iflytek.speech.action.voiceinput";
    // 语音合成对象
    private SpeechSynthesizer mTts;
    private SharedPreferences mSharedPreferences;
    // private Intent intent_main_service;
    private VoiceService.MyBinder voice_binder;
    public static boolean voice_flag= false;

    public ServiceConnection connection_voice = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            voice_flag = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            voice_binder = (VoiceService.MyBinder) service;
            voice_flag = true;
            Log.v("tag", "bind");

            Message msg = Message.obtain();
            msg.what = Config.ACK_NONE;
            BaseActivity.sendMessage(msg);

            //if (path_flag)
            //StartRead("请根据提示说出终点", Config.ACK_SAY_END);
        }
    };

	// 监听home键
	HomeKeyEventBroadCastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //con = Conmmunication.newInstance();
        //db = Database.getInstance();
		// 判断该Activity是否在LinkedList中，没有在的话就添加上
        Intent intent_voice_service = new Intent(this, VoiceService.class);
        startService(intent_voice_service);
        bindService(intent_voice_service, connection_voice, BIND_AUTO_CREATE);

		if (!queue.contains(this)) {
			queue.add(this);
			System.out.println("将" + queue.getLast() + "添加到list中去");
		}

		// 监听home键
		receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(
				Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

	}

    protected void StopListen() {
        voice_binder.StopListen();
    }

    protected void StartListen(int ackSayEnd) {
        voice_binder.SetACK(ackSayEnd);
        voice_binder.StartListen();

    }

    protected void StartRead(String string, int ackListenStart) {
        voice_binder.SetACK(ackListenStart);
        voice_binder.StartRead(string);
    }

	public abstract void processMessage(Message message);

	private static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			default:
				 System.out.println("执行到了控制信息处理handle");
				 if(!queue.isEmpty()){
				 Log.i("提示","值="+msg.arg1+"!!!!!!"+"类型="+msg.what);
				 queue.getLast().processMessage(msg);
				 }
				break;
			}

		};
	};

	// 发送消息（、、、）
	public static void sendMessage(Message msg) {
		handler.sendMessage(msg);
	}

	class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {
		static final String SYSTEM_REASON = "reason";
		static final String SYSTEM_HOME_KEY = "homekey";// home key
		static final String SYSTEM_RECENT_APPS = "recentapps";// long home key

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (reason != null) {
					if (reason.equals(SYSTEM_HOME_KEY)) {
						// home key处理点

					} else if (reason.equals(SYSTEM_RECENT_APPS)) {

					}
				}
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onRestart() {
		super.onResume();

		// Toast.makeText(BaseActivity.this, "i'm back",
		// Toast.LENGTH_SHORT).show();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//TODO ICON
		menu.add(Menu.NONE, Menu.FIRST + 1, 5, "下载地图").setIcon(
				android.R.drawable.ic_menu_delete);

		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "修改信息").setIcon(//(包括联系人，detail)
				android.R.drawable.ic_menu_edit);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:

			break;
		case Menu.FIRST + 2:
			//TODO 跳转到修改联系人的界面
			break;
		}
		return false;
	}

    /**
     * 响应触屏事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {

        switch (e.getAction() & MotionEvent.ACTION_MASK) {// &
            // MotionEvent.ACTION_MASK 多点
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                // RayPickRenderer.flag = !RayPickRenderer.flag;
                long start = e.getEventTime();
                long end = e.getDownTime();
                long total = start - end;

                if (total < 100) {
                    StopListen();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                finish();
                break;
        }

        return true;

    }
}
