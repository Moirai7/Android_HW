package com.moirai.view;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Message;
import android.os.Vibrator;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.moirai.client.Config;
import com.moirai.client.Conmmunication;
import com.moirai.client.Constant;
import com.moirai.client.R;

public class ShakeActivity extends BaseActivity
        implements SensorEventListener {
    private boolean state = true;
    private AnimationSet animUp;
    private AnimationSet animDown;
    private ImageView  upImage;
    private ImageView  downImage;
    private ProgressBar progressBar;
    private SensorManager sensorManager;
    private Vibrator vibrator;
    private ImageView blindView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        upImage = (ImageView)findViewById(R.id.shake_up_imageView);
        downImage = (ImageView)findViewById(R.id.shake_down_imageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        blindView = (ImageView) findViewById(R.id.shake_jBlindView);
        if(Constant.ID.equals("1")){
            //设置事件监听，要修改ImageView的值
            final GestureDetectorCompat mGesturedetector;
            mGesture gesture = new mGesture();
            mGesturedetector = new GestureDetectorCompat (this,gesture);//这里要先设置监听的哦,不然的话会报空指针异常.
            blindView.setVisibility(View.VISIBLE);
            blindView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mGesturedetector.onTouchEvent(event);
                    return true;
                }
            });
        }

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);  //首先得到传感器管理器对象
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
    }

    public void upStartAnim() {
         // 定义摇一摇动画动画上部分图片移动
        animUp = new AnimationSet(true);
        TranslateAnimation mup0 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,-0.5f);

        mup0.setDuration(1000);
        TranslateAnimation mup1 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, +0.5f);

        mup1.setDuration(1000);
        //延迟执行1秒
        mup1.setStartOffset(1000);
        animUp.addAnimation(mup0);
        animUp.addAnimation(mup1);
    }

    // 定义摇一摇动画动画下部分图片移动
    public void downStartAnim() {
        animDown = new AnimationSet(true);
        TranslateAnimation  mdn0 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, +0.5f);

        mdn0.setDuration(1000);
        TranslateAnimation  mdn1 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,-0.5f);

        mdn1.setDuration(1000);

        //延迟执行1秒
        mdn1.setStartOffset(1000);
        animDown.addAnimation( mdn0);
        animDown.addAnimation( mdn1);
        //下图片动画效果的添加
        //mImgDn.startAnimation(animdn);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //注册监听器
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                sensorManager.SENSOR_DELAY_NORMAL);
        //第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        sensorManager.unregisterListener(this);//解绑定Listener
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        //传感器信息改变时执行该方法
        float[] values = event.values;
        float x = values[0]; //x轴方向的重力加速度，向右为正
        float y = values[1]; // y轴方向的重力加速度，向前为正
        float z = values[2]; // z轴方向的重力加速度，向上为正
        if(x>15 || x<-15 || y>15 || y<-15 || z>15 || z<-15){
            //一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。当然这个值可以根据需要，自己定义。
            if(state){
                state = false;
                vibrator.vibrate(200);
                upStartAnim();
                downStartAnim();
                upImage.startAnimation(animUp);
                downImage.startAnimation(animDown);
                progressBar.setVisibility(View.VISIBLE);
                //摇完给服务器发送请求
                System.out.println("shakeActivity摇一摇已经检测到");
                con.requireFriend(Constant.USERNAME);
            }
        }
    }

    private void confirmAddFriend(String userName){
        String message = getString(R.string.message1_shake_confirm)
                + " "
                + userName
                + " "
                +getString(R.string.message2_shake_confirm);
        final String friendName = userName;
        //TODO 语音提示：message
        new AlertDialog.Builder(ShakeActivity.this)
            .setCancelable(true)
            .setMessage(message)
            .setPositiveButton(getString(R.string.yes_shake_confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Todo 语音提示： 您已添加 userName 为好友
                    con.addFriend(Constant.USERNAME,friendName,Config.SUCCESS);
                 }
              })
        .setNegativeButton(getString(R.string.no_shake_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Todo 语音提示： 取消添加好友请求
                progressBar.setVisibility(View.INVISIBLE);
                con.addFriend(Constant.USERNAME,friendName,Config.FAIl);
                state = true;
            }
        }).show();
    }
    private boolean checkInfoAdd = false;
    @Override
    public void processMessage(Message message) {
        switch (message.what) {
            case Config.RESULT_YAOYIYAO:
                //判断是否有同时摇的用户先，待完善
                int result_Yao = message.arg1;
               if (result_Yao == Config.SUCCESS) {
                   StartRead("检测到摇一摇好友了",Config.ACK_NONE);
                    String userName = (String)message.obj;
                    if(!Constant.ID.equals("1")){
                        confirmAddFriend(userName);
                    }else{
                        String msg = getString(R.string.message1_shake_confirm)
                                + " "
                                + userName
                                + " "
                                +getString(R.string.message2_shake_confirm)
                                +getString(R.string.message3_shake_confirm);
                        StartRead(msg, Config.ACK_NONE);
                        checkInfoAdd=true;
                    }

                } else {
                   //Todo 语音提示： 没有用户同时摇一摇
                    StartRead(getString(R.string.no_shake_friend_tip),Config.ACK_SHAKE_TIP);
                   Toast.makeText(ShakeActivity.this, getString(R.string.no_shake_friend_tip), Toast.LENGTH_SHORT).show();
                    state = true;
                }
                break;
            case Config.ACK_CON_SUCCESS:
                StartRead(getString(R.string.voice_shake_tip),Config.ACK_SHAKE_test);
                break;
            //确认添加对方为好友
            case Config.ACK_DOUBLE_CLICK:
                if(checkInfoAdd) {
                    StartRead(getResources().getString(R.string.voice_shake_result_success),Config.ACK_NONE);
                    finish();
                }
                break;
            //取消添加好友
            case Config.ACK_LONG_CLICK:
                if(checkInfoAdd) {
                    checkInfoAdd = false;
                    state=true;
                    StartRead(getResources().getString(R.string.voice_shake_result_cancel),Config.ACK_NONE);
                }
                break;
      /*      case Config.ACK_SHAKE_TIP_CANCEL:
                StartRead(getString(R.string.voice_shake_tip),Config.ACK_SHAKE_TIP);
                break;*/
            case Config.REQUEST_ADDFRIEND:
                int result_Add = message.arg1;
                if(result_Add == Config.SUCCESS){
                    StartRead(getResources().getString(R.string.voice_shake_result_success),Config.ACK_NONE);
                    gotoFriendActivity();
                }else{
                    StartRead(getResources().getString(R.string.voice_shake_result_success),Config.ACK_NONE);
                }
                state=true;
                break;
            default:
                break;
        }
    }

    private void gotoFriendActivity(){
        Intent intent = new Intent();
        intent.setClass(ShakeActivity.this, MainActivity.class);
        startActivity(intent);
        ShakeActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        if (queue.contains(this)) {
            queue.remove(this);
            System.out.println("将" + this + "移出list"+"queue : " + queue.toString());
        }
        if(connection_voice!=null)
            unbindService(connection_voice);
        super.onDestroy();
    }
}
