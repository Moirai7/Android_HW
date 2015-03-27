package com.moirai.voice;

import com.iflytek.speech.ErrorCode;
import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;
import com.iflytek.speech.RecognizerListener;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechRecognizer;
import com.iflytek.speech.SpeechSynthesizer;
import com.iflytek.speech.SynthesizerListener;
import com.moirai.view.BaseActivity;
import com.moirai.client.Config;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class VoiceService extends Service {  
	
	private static String TAG = "Voice_Module";
	
	private Toast mToast;


	// 语音识别对象。
	private SpeechRecognizer mIat;
	// 语音合成对象
	private SpeechSynthesizer mTts;

	private SharedPreferences mSharedPreferences;
	
	private boolean flag_iat,flag_tts,flag_binder = false;
	
	private int CURRENT_ACK = -1;
	
    private Handler handler = new Handler(){
    	@Override
    	public void handleMessage(Message msg) {
			if(flag_iat && flag_tts && flag_binder){
				//mBinder.StartRead("语音启动");
				flag_iat = false;
				flag_tts = false;
				flag_binder = false;
				
				// broad cast
				try {
					SendServiceBroadCast("语音启动");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
			switch(msg.what){
			case Config.ACK_SERVICE:
				// broad cast
				Log.v(TAG, (String) msg.obj);
				try {
					SendServiceBroadCast((String)msg.obj);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
    	}
    };	
    
    private Context context;
      
    private MyBinder mBinder = new MyBinder();  
  
    @Override  
    public void onCreate() {  
        super.onCreate();  
        Log.d(TAG, "onCreate() executed");  
        		
		mIat = new SpeechRecognizer(this, mInitListener);
		// 初始化合成对象
		mTts = new SpeechSynthesizer(this, mTtsInitListener);
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
		mIat.cancel(mRecognizerListener);
		mIat.destory();

		mTts.stopSpeaking(mTtsListener);
		mTts.destory();
    }  
  
    @Override  
    public IBinder onBind(Intent intent) {  
        return mBinder;  
    }  
  
    public class MyBinder extends Binder {  
    	
    	public void setBinderFlagOn(){
    		flag_binder = true;
    		Message msg = Message.obtain();
    		msg.what = Config.ACK_NONE;
			handler.sendMessage(msg);   		
    	}
  
    	/**
    	 * 开始语音录入
    	 * 
    	 *
    	 */
    	public void StartListen() {

    		// GLOBAL_MS = ack;
    		//
    		// et_command.setText("");
    		//
    		// setParam_Iat();

    		mIat.startListening(mRecognizerListener);
    	}

    	/**
    	 * 停止语音录入
    	 */
    	public void StopListen() {
    		mIat.stopListening(mRecognizerListener);
    	}

    	/**
    	 * 开始语音播报
    	 * 
    	 * @param content
    	 *            播报内容
    	 *
    	 */
    	public void StartRead(String content) {

    		// GLOBAL_MS = ack;
    		//
    		// setParam_Tts();
    		// 设置参数
    		int code = mTts.startSpeaking(content, mTtsListener);
    		if (code != 0) {
    			showTip("start speak error : " + code);
    		} else
    			showTip("start speak success.");

    	}
    	
    	public void SetACK(int ack){
    		CURRENT_ACK = ack;
    	}
    }  
    
	/**
	 * 初期化监听器。
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(ISpeechModule module, int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
			if (code == ErrorCode.SUCCESS) {

				flag_iat = true;

				Message msg = Message.obtain();
				msg.what = Config.ACK_MAIN_WELCOME;
				handler.sendMessage(msg);

			}
		}
	};

	/**
	 * 识别回调。
	 */
	private RecognizerListener mRecognizerListener = new RecognizerListener.Stub() {

		String finalresult = "";

		@Override
		public void onVolumeChanged(int v) throws RemoteException {
			//showTip("onVolumeChanged：" + v);
			Log.d(TAG, "onVolumeChanged：" + v);
		}

		@Override
		public void onResult(final RecognizerResult result, boolean isLast) {

			String text = JsonParser.parseIatResult(result.getResultString());
			finalresult += text;

			if (isLast) {
				showTip(finalresult);

				Message msg = Message.obtain();
				msg.what = CURRENT_ACK;
				msg.obj = finalresult;
				BaseActivity.sendMessage(msg);
				
				finalresult = "";
			}

		}

		@Override
		public void onError(int errorCode) throws RemoteException {
			//showTip("onError Code：" + errorCode);
			Log.d(TAG, "onError Code：" + errorCode);
		}

		@Override
		public void onEndOfSpeech() throws RemoteException {
			//showTip("onEndOfSpeech");
			Log.d(TAG, "onEndOfSpeech.");
		}

		@Override
		public void onBeginOfSpeech() throws RemoteException {
			Log.d(TAG, "onBeginOfSpeech.");
			//showTip("onBeginOfSpeech");
		}
	};

	/**
	 * 初期化监听。
	 */
	private InitListener mTtsInitListener = new InitListener() {

		@Override
		public void onInit(ISpeechModule arg0, int code) {
			Log.d(TAG, "InitListener init() code = " + code);
			if (code == ErrorCode.SUCCESS) {

				flag_tts = true;

				Message msg = Message.obtain();
				msg.what = Config.ACK_NONE;
				handler.sendMessage(msg);
			}
		}
	};

	/**
	 * 合成回调监听。
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener.Stub() {
		@Override
		public void onBufferProgress(int progress) throws RemoteException {
			Log.d(TAG, "onBufferProgress :" + progress);
			// showTip("onBufferProgress :" + progress);
		}

		@Override
		// tts结束位置
		public void onCompleted(int code) throws RemoteException {

			Log.d(TAG, "onCompleted code =" + code);
			showTip("onCompleted code =" + code);
			
			Message msg = Message.obtain();
			msg.what = CURRENT_ACK;
			BaseActivity.sendMessage(msg);
		}

		@Override
		public void onSpeakBegin() throws RemoteException {
			Log.d(TAG, "onSpeakBegin");
			showTip("onSpeakBegin");
		}

		@Override
		public void onSpeakPaused() throws RemoteException {
			Log.d(TAG, "onSpeakPaused.");
			showTip("onSpeakPaused.");
		}

		@Override
		public void onSpeakProgress(int progress) throws RemoteException {
		//	Log.d(TAG, "onSpeakProgress :" + progress);
		//	showTip("onSpeakProgress :" + progress);
		}

		@Override
		public void onSpeakResumed() throws RemoteException {
			Log.d(TAG, "onSpeakResumed.");
			showTip("onSpeakResumed");
		}
	};



	private void showTip(final String str) {
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				mToast.setText(str);
//				mToast.show();
//			}
//		});
	}

	/**
	 * 参数设置
	 * 
	 * @param
	 * @return
	 */
	public void setParam_Iat() {

		mIat.setParameter(SpeechConstant.LANGUAGE, mSharedPreferences
				.getString("iat_language_preference", "zh_cn"));
		mIat.setParameter(SpeechConstant.VAD_BOS,
				mSharedPreferences.getString("iat_vadbos_preference", "4000"));
		// mIat.setParameter(SpeechConstant.ACCENT,
		// mSharedPreferences.getString("accent_preference", "mandarin"));
		// mIat.setParameter(SpeechConstant.DOMAIN,
		// mSharedPreferences.getString("domain_perference", "iat"));
		mIat.setParameter(SpeechConstant.VAD_EOS,
				mSharedPreferences.getString("iat_vadeos_preference", "1000"));

		String param = null;
		param = "asr_ptt="
				+ mSharedPreferences.getString("iat_punc_preference", "1");
		mIat.setParameter(SpeechConstant.PARAMS, param
				+ ",asr_audio_path=/sdcard/iflytek/wavaudio.pcm");

	}

	public void setParam_Tts() {
		mTts.setParameter(SpeechConstant.ENGINE_TYPE,
				mSharedPreferences.getString("engine_preference", "local"));

		if (mSharedPreferences.getString("engine_preference", "local")
				.equalsIgnoreCase("local")) {
			mTts.setParameter(SpeechSynthesizer.VOICE_NAME, mSharedPreferences
					.getString("role_cn_preference", "xiaoyan"));
		} else {
			mTts.setParameter(SpeechSynthesizer.VOICE_NAME, mSharedPreferences
					.getString("role_cn_preference", "xiaoyan"));
		}
		mTts.setParameter(SpeechSynthesizer.SPEED,
				mSharedPreferences.getString("speed_preference", "50"));

		mTts.setParameter(SpeechSynthesizer.PITCH,
				mSharedPreferences.getString("pitch_preference", "50"));

		mTts.setParameter(SpeechSynthesizer.VOLUME,
				mSharedPreferences.getString("volume_preference", "50"));
	}

	
    public void SendServiceBroadCast(String content) throws InterruptedException {  
        Log.d(TAG, "ServiceThread===>>startDownload() executed===>>线程ID:"+Thread.currentThread().getId());  
        //Toast.makeText( VoiceService.this, "Send BroadCast now...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setAction("com.navi.blind.VoiceBroadCast");
        intent.putExtra("value", content);
        sendBroadcast(intent);
        //Toast.makeText( VoiceService.this, "Sent! Did you receive?", Toast.LENGTH_SHORT).show();
    }
  
}  