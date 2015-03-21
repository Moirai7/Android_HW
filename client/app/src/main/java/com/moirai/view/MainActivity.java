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

	@Override
	public void processMessage(Message message) {
		// TODO Auto-generated method stub
		switch(message.what){

		case Config.REQUEST_GATHISTORY:

			break;
		case Config.REQUEST_SAVEHISTORY:
			break;
		case Config.ACK_SERVICE:

            break;
			
		default:
			break;
		}		
	}

}  