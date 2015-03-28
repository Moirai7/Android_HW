package com.moirai.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;

import com.moirai.client.R;


public class LoadingActivity extends Activity {
    private Button yes_btn;
    private Button no_btn;
    private String choice;//0-语音，1-文字
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = View.inflate(this,R.layout.activity_loading,null);
        setContentView(view);
        //取消标题栏
       // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*旋转动画效果*//*
        RotateAnimation animation = new RotateAnimation(0f,360f,Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(3000);//设置动画持续时间
        animation.setFillAfter(true);//动画执行完后停留在执行完的状态*/

        //TODO 语音提示用户是否选择语音进行后续操作

        yes_btn = (Button)findViewById(R.id.YES_btn);
        no_btn = (Button)findViewById(R.id.NO_btn);
        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 语音
                choice = "0";
                redirectToRegister();
            }
        });
        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice = "1";
                redirectToRegister();
            }
        });
    }

    /**
     * 跳转到注册界面
     */
    public void redirectToRegister(){
        Intent intent = new Intent(LoadingActivity.this,RegisterActivity.class);
        startActivity(intent);
        finish();//结束这个activity
    }
}