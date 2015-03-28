package com.moirai.view;

import android.os.Message;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.moirai.client.R;

public class SettingActivity extends BaseActivity {

    @Override
    public void processMessage(Message message) {
        // TODO Auto-generated method stub
        switch(message.what){

//            case Config.REQUEST_GATHISTORY:
//
//                break;
//            case Config.REQUEST_SAVEHISTORY:
//                break;
//            case Config.ACK_SERVICE:
//
//                break;

            default:
                break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

}
