package com.moirai.view;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.moirai.client.Config;
import com.moirai.client.Constant;
import com.moirai.client.R;
import com.moirai.model.Moments;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SendMomentActivity extends BaseActivity {
    EditText et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_send_moment);

        et = (EditText)findViewById(R.id.editMoment);

        if(Constant.ID.equals("1")){
            //设置事件监听，要修改ImageView的值
            final GestureDetectorCompat mGesturedetector;
            mGesture gesture = new mGesture();
            mGesturedetector = new GestureDetectorCompat (this,gesture);//这里要先设置监听的哦,不然的话会报空指针异常.
            ImageView iv = (ImageView)findViewById(R.id.moment_zlimage);
            iv.setVisibility(View.VISIBLE);
            iv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mGesturedetector.onTouchEvent(event);
                    return true;
                }
            });
        }

        ActionBar actionBar = getActionBar();
        actionBar.setIcon(null);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setIcon(R.drawable.tabchat_selected);
        actionBar.setTitle(getString(R.string.send_moments));
    }

    @Override
    public void processMessage(Message message) {
        switch (message.what) {
            //长按发送消息
            case Config.ACK_LONG_CLICK:
                StartListen(Config.ACK_TALKING);
                break;
            case Config.ACK_TALKING:
                String str = (String)message.obj;
                et.setText(str);
                send();
                break;
        }
    }

    private void send(){
        Moments m = new Moments();
        m.setSendUser(Constant.USERNAME);
        m.setContent(et.getText().toString());
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        m.setTime(df.format(new Date()));
        db.saveMoments(m);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //当点击不同的menu item 是执行不同的操作
        switch (id) {
            case R.id.action_sending:
                String s = et.getText().toString();
                send();
                break;
            case android.R.id.home:
                queue.getLast().finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
