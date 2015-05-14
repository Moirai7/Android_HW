package com.moirai.view;

import java.util.LinkedList;
import com.iflytek.speech.SpeechRecognizer;
import com.iflytek.speech.SpeechSynthesizer;
import com.moirai.client.Config;
import com.moirai.client.Conmmunication;
import com.moirai.client.Constant;
import com.moirai.client.R;
import com.moirai.util.Database;
import com.moirai.voice.VoiceService;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Toast;

public abstract class BaseActivity extends FragmentActivity  {
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
    private Intent intent_main_service;
    public VoiceService.MyBinder voice_binder;
    public static boolean voice_flag = false;


    public ServiceConnection connection_voice = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        con = Conmmunication.newInstance();
        db = Database.getInstance(this);
        // 判断该Activity是否在LinkedList中，没有在的话就添加上
        if (!queue.contains(this)) {
            queue.add(this);
            System.out.println("将" + queue.getLast() + "添加到list中去"+"queue : " + queue.toString());
        }
        if(Constant.ID=="1"){
            connection_voice = new ServiceConnection() {

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
                    msg.what = Config.ACK_CON_SUCCESS;
                    BaseActivity.sendMessage(msg);
                }
            };

            Intent intent_voice_service = new Intent(this, VoiceService.class);
            startService(intent_voice_service);
            bindService(intent_voice_service, connection_voice, BIND_AUTO_CREATE);
        }


	}

    protected void StopListen() {
        voice_binder.StopListen();
    }

    protected void StartListen(int ackSayEnd) {
        voice_binder.SetACK(ackSayEnd);
        voice_binder.StartListen();

    }

    protected void StopRead(){
        if(voice_binder!=null)
           voice_binder.StopRead();
    }

    protected void StartRead(String string, int ackListenStart) {
        if(voice_binder!=null){
            voice_binder.SetACK(ackListenStart);
            voice_binder.StartRead(string);
        }
    }

	public abstract void processMessage(Message message);

	private static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			default:
				 System.out.println(queue.getLast()+"执行到了控制信息处理handle");
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

    protected void removeActivity(){
        queue.remove(queue.getLast());
    }
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onRestart() {
		super.onResume();
	}

    class mGesture  extends GestureDetector.SimpleOnGestureListener {
        // 双击的第二下Touch down时触发
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Message msg = Message.obtain();
            msg.what = Config.ACK_DOUBLE_CLICK;
            BaseActivity.sendMessage(msg);
            Log.i("lanlan","double click");
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Message msg = Message.obtain();
            msg.what = Config.ACK_CLICK;
            BaseActivity.sendMessage(msg);
            Log.i("lanlan","1 click");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Message msg = Message.obtain();
            msg.what = Config.ACK_LONG_CLICK;
            BaseActivity.sendMessage(msg);
            Log.i("lanlan","long click");
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > 120) {
                Message msg = Message.obtain();
                msg.what = Config.ACK_LEFT;
                BaseActivity.sendMessage(msg);
                Log.i("lanlan","left");
                return true;
            } else if (e1.getX() - e2.getX() < -120) {
                Message msg = Message.obtain();
                msg.what = Config.ACK_RIGHT;
                BaseActivity.sendMessage(msg);
                Log.i("lanlan","right");
                return true;
            }
            if(e1.getY() - e2.getY() >120){
                Message msg = Message.obtain();
                msg.what = Config.ACK_TOP;
                BaseActivity.sendMessage(msg);
                Log.i("lanlan","top");
                return true;
            }else if(e1.getY() - e2.getY() <-120){
                Message msg = Message.obtain();
                msg.what = Config.ACK_DOWN;
                BaseActivity.sendMessage(msg);
                Log.i("lanlan","down");
                return true;
            }
            return false;
        }
    }
}
