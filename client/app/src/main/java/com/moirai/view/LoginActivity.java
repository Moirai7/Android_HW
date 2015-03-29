package com.moirai.view;

import android.content.Intent;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.moirai.client.Config;
import com.moirai.client.Constant;
import com.moirai.client.R;
import com.moirai.model.User;

import static com.moirai.client.Constant.*;

public class LoginActivity extends BaseActivity {
   private EditText username_edit;
   private EditText password_edit;
   private Button login_btn;
    @Override
    public void processMessage(Message message) {
        switch(message.what){
            case Config.REQUEST_LOGIN:
                int result = message.arg1;
                if(result == Config.SUCCESS){
                    //TODO 得到编辑框里的值
                    USERNAME = username_edit.getText().toString();
                    PASSWORD = password_edit.getText().toString();
                    //db.setUserInfo(Constant.USERNAME,Constant.PASSWORD);
                    //如果是看不见，就语音提示登录结果
                    if(Constant.ID.equals("1")) {
                        StartRead(getResources().getString(R.string.login_success), Config.ACK_LOGIN_SUCCESS_RETURN);
                    }else {
                        Toast.makeText(LoginActivity.this, USERNAME + getResources().getString(R.string.login_success),
                                Toast.LENGTH_SHORT).show();
                        StopRead();
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }else{
                    if(Constant.ID.equals("1")){
                        //信息输入错误，重新输入
                        StartRead(getResources().getString(R.string.login_failure),Config.ACK_CON_SUCCESS);
                    }
                    Toast.makeText(LoginActivity.this, USERNAME + getResources().getString(R.string.login_failure),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case Config.ACK_CON_SUCCESS:
                //请说出用户名
                StartRead(getResources().getString(R.string.username),Config.ACK_TALK_USERNAME);
                break;
            case Config.ACK_TALK_USERNAME:
                StartListen(Config.ACK_LISTEN_USERNAME);
                break;
            case Config.ACK_LISTEN_USERNAME:
                USERNAME = (String)message.obj;
                username_edit.setText(USERNAME);
                //说出密码
                StartRead(getResources().getString(R.string.password),Config.ACK_TALK_PASSWORD);

                break;
            case Config.ACK_TALK_PASSWORD:
                StartListen(Config.ACK_LISTEN_PASSWORD);
                break;
            case Config.ACK_LISTEN_PASSWORD:
                PASSWORD = (String)message.obj;
                password_edit.setText(PASSWORD);

                Message msg = Message.obtain();
                msg.what = Config.REQUEST_LOGIN;
                msg.arg1 = Config.SUCCESS;
                BaseActivity.sendMessage(msg);
                //User user = new User();
                //user.setUsername(username);
                //user.setPassword(password);
                //con.login(user);
                break;
            case Config.ACK_LOGIN_SUCCESS_RETURN:
                StopRead();
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case Config.ACK_LONG_CLICK:
                StopRead();
                Intent mintent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(mintent);
                finish();
                break;
            default:
                break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
      //ID为1，user不可见
        if(Constant.ID.equals("1")){
            //设置事件监听，要修改ImageView的值
            final GestureDetectorCompat mGesturedetector;
            mGesture gesture = new mGesture();
            mGesturedetector = new GestureDetectorCompat (this,gesture);//这里要先设置监听的哦,不然的话会报空指针异常.
            ImageView iv = (ImageView)findViewById(R.id.hblindviewlogin);
            iv.setVisibility(View.VISIBLE);
            iv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mGesturedetector.onTouchEvent(event);
                    return true;
                }
            });
        }

        //TODO 得到编辑框里的值
        username_edit = (EditText)findViewById(R.id.username_edit);
        password_edit = (EditText)findViewById(R.id.password_edit);
        login_btn = (Button)findViewById(R.id.login_btn);
        //TODO 使用USER创建并调用login();
       login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = username_edit.getText().toString();
                String password = password_edit.getText().toString();
                System.out.println("username:"+username);
                System.out.println("password:"+password);
                if(username==null || username==""|| password==null||password==""){
                   Toast toast = Toast.makeText(LoginActivity.this,"Please input username or password",Toast.LENGTH_LONG);
                   toast.setGravity(Gravity.CENTER,0,0);
                   toast.show();
                }else {
                    //TODO 自动登录方法
                    /*db.getUserInfo();
                    User user1 = new User();
                    user.setUsername(Constant.USERNAME);
                    user.setPassword(Constant.PASSWORD);
                   con.login(user1);*/
                }
            }
        });
    }

}
