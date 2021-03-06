package com.moirai.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.moirai.client.Config;
import com.moirai.client.Constant;
import com.moirai.client.R;
import com.moirai.model.Info;

import java.util.List;


public class LoadingActivity extends BaseActivity {
    private String choice;//0-语音，1-文字
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        final View view = View.inflate(this,R.layout.activity_loading,null);
        setContentView(view);
        //设置事件监听，要修改ImageView的值
        final GestureDetectorCompat mGesturedetector;
        mGesture gesture = new mGesture();
        mGesturedetector = new GestureDetectorCompat (this,gesture);//这里要先设置监听的哦,不然的话会报空指针异常.
        ImageView iv = (ImageView)findViewById(R.id.zblindView);
        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGesturedetector.onTouchEvent(event);
                return true;
            }
        });
    }
    @Override
    protected void onDestroy() {
        Constant.isSetting=false;
        if (queue.contains(this)) {
            queue.remove(this);
            System.out.println("将" + this + "移出list"+"queue : " + queue.toString());
        }
        if(connection_voice!=null)
            unbindService(connection_voice);
        super.onDestroy();
    }
    @Override
    public void processMessage(Message message) {
        switch(message.what){
            case Config.ACK_CON_SUCCESS:
                StartRead(getString(R.string.loading),Config.ACK_NONE);
                break;
            case Config.ACK_LONG_CLICK :
                Constant.ID="0";
                redirectToRegister();
                break;
            case Config.ACK_CLICK:
                Constant.ID="1";
                redirectToRegister();
                break;
            case Config.ACK_DOUBLE_CLICK:
                Constant.ID="1";
                StopRead();
                Intent intent = new Intent(LoadingActivity.this,MainActivity.class);
                startActivity(intent);
                finish();//结束这个activity
            default:
                break;
        }
    }

    /**
     * 跳转到注册界面
     */
    public void redirectToRegister(){
        StopRead();
        Intent intent = new Intent(LoadingActivity.this,RegisterActivity.class);
        //Intent intent = new Intent(LoadingActivity.this,LoginActivity.class);
        startActivity(intent);
        //intent.removeCategory(Intent.category.LAUNCHER);
        finish();//结束这个activity
    }

}