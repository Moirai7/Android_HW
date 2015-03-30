package com.moirai.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.moirai.client.Config;
import com.moirai.client.Constant;
import com.moirai.client.R;

public class SettingActivity extends BaseActivity {
    private RadioGroup radioGroup;
    private RadioButton radioBtn0;
    private RadioButton radioBtn1;
    private RadioButton radioBtn2;
    @Override
    public void processMessage(Message message) {
        // TODO Auto-generated method stub
        switch(message.what){

          case Config.ACK_CON_SUCCESS:
                StartRead(getResources().getString(R.string.setting_welcome),Config.ACK_NONE);
              break;
          case Config.ACK_CLICK:
              Constant.ID = "0";
              Constant.setBlind=true;
              StartRead(getResources().getString(R.string.setting_choice0),Config.ACK_NONE);
              finish();
               break;
          case Config.ACK_DOUBLE_CLICK:
               Constant.ID="1";
              Constant.setBlind=false;
              StartRead(getResources().getString(R.string.setting_choice1),Config.ACK_NONE);
              finish();
               break;
            case Config.ACK_LONG_CLICK:
                Constant.ID="2";
                Constant.setBlind=true;
                StartRead(getResources().getString(R.string.setting_choice2),Config.ACK_NONE);
                finish();
                break;
            default:
                break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        if(Constant.ID.equals("1")){
            //设置事件监听，要修改ImageView的值
            final GestureDetectorCompat mGesturedetector;
            mGesture gesture = new mGesture();
            mGesturedetector = new GestureDetectorCompat (this,gesture);//这里要先设置监听的哦,不然的话会报空指针异常.
            ImageView iv = (ImageView)findViewById(R.id.hblindviewsetting);
            iv.setVisibility(View.VISIBLE);
            iv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mGesturedetector.onTouchEvent(event);
                    return true;
                }
            });
        }

        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioBtn0 = (RadioButton)findViewById(R.id.type_choice0);
        radioBtn1 = (RadioButton)findViewById(R.id.type_choice1);
        radioBtn2 = (RadioButton)findViewById(R.id.type_choice2);

        if(Constant.ID.equals("1")){
            choose1();
        }else if(Constant.ID.equals("0")){
            choose0();
        }else{
            choose2();
        }

        /**
         * 看的见的人，点击事件
         */
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==radioBtn0.getId()){
                    choose0();
                    Toast toast = Toast.makeText(SettingActivity.this,getResources().getString(R.string.setting_choice0),Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM,0,0);
                    toast.show();
                    finish();
                }else if(i==radioBtn1.getId()){
                    choose1();
                    Toast toast = Toast.makeText(SettingActivity.this,getResources().getString(R.string.setting_choice1),Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM,0,0);
                    toast.show();
                    finish();
                }else{
                    Constant.ID="2";
                    choose2();
                    Toast toast = Toast.makeText(SettingActivity.this,getResources().getString(R.string.setting_choice2),Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM,0,0);
                    toast.show();
                    finish();
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        Constant.isSetting=false;
        super.onDestroy();
    }

    /**
     * 设置0被选
     */
    public void choose0(){
        Constant.ID="0";
        Constant.setBlind=true;
        radioBtn0.setChecked(true);
        radioBtn1.setChecked(false);
        radioBtn2.setChecked(false);
    }

    /**
     * 设置1被选
     */
    public void choose1(){
        Constant.ID="1";
        Constant.setBlind=false;
        radioBtn0.setChecked(false);
        radioBtn1.setChecked(true);
        radioBtn2.setChecked(false);
    }

    /**
     * 设置2被选
     */
    public void choose2(){
        Constant.ID="2";
        Constant.setBlind=true;
        radioBtn0.setChecked(false);
        radioBtn1.setChecked(false);
        radioBtn2.setChecked(true);
    }
}
