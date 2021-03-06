package com.moirai.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.Gravity;
import android.view.KeyEvent;
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
    private Button retriveToRegister;
    @Override
    public void processMessage(Message message) {
        switch(message.what){
            case Config.REQUEST_LOGIN:
                int result = message.arg1;
                if(result == Config.SUCCESS){
                    //TODO 得到编辑框里的值
                    USERNAME = username_edit.getText().toString();
                    PASSWORD = password_edit.getText().toString();
                    Constant.USERNAME = USERNAME;
                    Constant.PASSWORD = PASSWORD;
                    Constant.ID = "1";
                    db.setUserInfo(USERNAME,PASSWORD,"1");
                    //如果是看不见，就语音提示登录结果
                    if(Constant.ID.equals("1")) {
                        StartRead(getResources().getString(R.string.login_success), Config.ACK_LOGIN_SUCCESS_RETURN);
                    }else {
                        Toast.makeText(LoginActivity.this, USERNAME + getResources().getString(R.string.login_success),
                                Toast.LENGTH_SHORT).show();
                        StopRead();
                        removeActivity();
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
                StartRead(getResources().getString(R.string.loginStart),Config.ACK_NONE);
                //请说出用户名
               // StartRead(getResources().getString(R.string.username),Config.ACK_TALK_USERNAME);
                break;
            case Config.ACK_CLICK:
                StopRead();
                StartRead(getResources().getString(R.string.username),Config.ACK_TALK_USERNAME);
                break;
            case Config.ACK_TALK_USERNAME:
                StartListen(Config.ACK_LISTEN_USERNAME);
                break;
            case Config.ACK_LISTEN_USERNAME:
                USERNAME = ((String)message.obj);
                USERNAME=USERNAME.substring(0,USERNAME.length()-1);
                if(USERNAME.equals("LANLANERROR"))
                    StartRead(getResources().getString(R.string.username),Config.ACK_TALK_USERNAME);
                username_edit.setText(USERNAME);
                //说出密码
                StartRead(getResources().getString(R.string.password),Config.ACK_TALK_PASSWORD);

                break;
            case Config.ACK_TALK_PASSWORD:
                StartListen(Config.ACK_LISTEN_PASSWORD);
                break;
            case Config.ACK_LISTEN_PASSWORD:
                PASSWORD = ((String)message.obj);
                PASSWORD=PASSWORD.substring(0,PASSWORD.length()-1);
                password_edit.setText(PASSWORD);

                Message msg = Message.obtain();
                msg.what = Config.REQUEST_LOGIN;
                msg.arg1 = Config.SUCCESS;
                BaseActivity.sendMessage(msg);
                User user = new User();
                user.setUsername(USERNAME);
                user.setPassword(PASSWORD);
                con.login(user);
                break;
            case Config.ACK_LOGIN_SUCCESS_RETURN:
                StopRead();
                removeActivity();
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case Config.ACK_LONG_CLICK:
                StopRead();
                removeActivity();
                Intent mintent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(mintent);
                finish();
                break;
            default:
                break;
        }
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
        retriveToRegister = (Button)findViewById(R.id.retrieveToRegister);

        //TODO 自动登录方法
        if(db.getUserInfo()) {
            username_edit.setText(Constant.USERNAME);
            password_edit.setText(Constant.PASSWORD);
            User user = new User();
            user.setUsername(Constant.USERNAME);
            user.setPassword(Constant.PASSWORD);
            con.login(user);
        }

        //TODO 使用USER创建并调用login();
       login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = username_edit.getText().toString();
                String password = password_edit.getText().toString();
                System.out.println("username:"+username);
                System.out.println("password:"+password);
                if(username==null || username.equals("")|| password==null||password.equals("")){
                   Toast toast = Toast.makeText(LoginActivity.this,"Please input username or password",Toast.LENGTH_LONG);
                   toast.setGravity(Gravity.CENTER,0,0);
                   toast.show();
                }else {
                    removeActivity();
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        });

        retriveToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeActivity();
                Intent mintent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(mintent);
                finish();
            }
        });
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("您是否要退出？")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    //con.exitGame();
                                    // con.clear();
                                    finish();
                                    int siz = BaseActivity.queue.size();
                                    for (int i = 0; i < siz; i++) {
                                        if (BaseActivity.queue.get(i) != null) {
                                            System.out
                                                    .println((Activity) BaseActivity.queue
                                                            .get(i) + "退出程序");
                                            ((Activity) BaseActivity.queue
                                                    .get(i)).finish();
                                        }
                                    }
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {


                                }
                            });
            dialog.create().show();

            return true;
        }

        else
            return false;

    }
}
