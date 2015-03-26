package com.moirai.view;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.moirai.client.Config;
import com.moirai.client.Conmmunication;
import com.moirai.client.R;

public class ShakeActivity extends BaseActivity implements SensorEventListener,View.OnTouchListener {
    private static int role = 2;  //盲人

    private  AnimationSet animUp;
    private  AnimationSet animDown;
    private  ImageView  upImage;
    private  ImageView  downImage;
    private SensorManager sensorManager;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        /*连接服务器*/
      //  con = Conmmunication.newInstance();
        upImage = (ImageView)findViewById(R.id.shake_up_imageView);
        downImage = (ImageView)findViewById(R.id.shake_down_imageView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);  //首先得到传感器管理器对象
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shake, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        super.onResume(); //注册监听器
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
            vibrator.vibrate(200);
            upStartAnim();
            downStartAnim();
            upImage.startAnimation(animUp);
            downImage.startAnimation(animDown);

            //像服务器发送加好友请求
        //    con.addFriend();
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //屏幕点击事件监听
        if(MotionEvent.ACTION_DOWN == motionEvent.getAction()) {

        }
        return false;
    }

    private void confirmAddFriend(String userName){
        String message = getString(R.string.message1_shake_confirm)
                + " "
                + userName
                + " "
                +getString(R.string.message2_shake_confirm);
        new AlertDialog.Builder(ShakeActivity.this)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(getString(R.string.yes_shake_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setClass(ShakeActivity.this, FriendActivity.class);
                        //  startActivityForResult(intent, my_requestCode);
                        startActivity(intent);
                        ShakeActivity.this.finish();
                    }
                })
                .setNegativeButton(getString(R.string.no_shake_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    @Override
    public void processMessage(Message message) {
        switch(message.what){
            case Config.REQUEST_ADDFRIEND:
                //判断是否有同时摇的用户先，待完善
                int result = message.arg1;
                if(result == Config.SUCCESS){
                    String userName = "emma";
                    confirmAddFriend(userName);
                }else{
                    Toast.makeText(ShakeActivity.this,"",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
