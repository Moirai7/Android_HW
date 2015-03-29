package com.moirai.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.moirai.client.Config;
import com.moirai.client.Conmmunication;
import com.moirai.client.Constant;
import com.moirai.client.R;
import com.moirai.model.User;

public class RegisterActivity extends BaseActivity {
    private Button registerButton;
    private EditText idEditText;
    private EditText pwEditText1;
    private EditText pwEditText2;
    private ImageView jBlindView;
    private String userName;
    private String pw1;
    private String pw2;
    @Override
    public void processMessage(Message message) {
        switch(message.what){
            case Config.REQUEST_REGISTER:
                int result = message.arg1;
                if(result == Config.SUCCESS){
                    //TODO 成功要得到name等值并写到数据库
                    StartRead(getString(R.string.tip_register_successfully),Config.ACK_REGISTER_SUCCESS);
                    db.setUserInfo(Constant.USERNAME,Constant.PASSWORD,Constant.ID);
                    gotoLogin();
                    Toast.makeText(RegisterActivity.this, getString(R.string.tip_register_successfully), Toast.LENGTH_SHORT).show();
                }
                else{
                    StartRead(getString(R.string.tip_register_failed),Config.ACK_REGISTER_FAILED);
                    Toast.makeText(RegisterActivity.this, getString(R.string.tip_register_failed), Toast.LENGTH_SHORT).show();
                    //TODO 注意编辑框的值要设置为空，像下面一样
                    idEditText.setText("");
                    pwEditText1.setText("");
                    pwEditText2.setText("");
                }
                break;
            case Config.ACK_CON_SUCCESS:
                StartRead(getString(R.string.register_start),Config.ACK_START_REGISTER);
                break;
            case Config.ACK_START_REGISTER:
                StartRead(getString(R.string.voice_enter_id),Config.ACK_REGISTER_USERNAME_TIP);
                break;
            case Config.ACK_REGISTER_USERNAME_TIP:
                StartListen(Config.ACK_REGISTER_USERNAME);
                break;
            case Config.ACK_REGISTER_USERNAME:
                userName = (String)message.obj;
                if(userName.isEmpty()){
                    StartRead(getString(R.string.tip_register_id),Config.ACK_REGISTER_USERNAME_TIP);
                    break;
                }
                StartRead(getString(R.string.voice_enter_pw1),Config.ACK_REGISTER_PASSWORD_1_TIP);
                break;
            case Config.ACK_REGISTER_PASSWORD_1_TIP:
                StartListen(Config.ACK_REGISTER_PASSWORD_1);
                break;
            case Config.ACK_REGISTER_PASSWORD_1:
                pw1 = (String)message.obj;
                StartRead(getString(R.string.voice_enter_pw2),Config.ACK_REGISTER_PASSWORD_2_TIP);
                break;
            case Config.ACK_REGISTER_PASSWORD_2_TIP:
                StartListen(Config.ACK_REGISTER_PASSWORD_2);
                break;
            case Config.ACK_REGISTER_PASSWORD_2:
                pw2 = (String)message.obj;
                if(!pw1.equals(pw2)){
                    StartRead(getString(R.string.tip_regieter_pw_null)+getString(R.string.voice_enter_pw1),Config.ACK_REGISTER_PASSWORD_1_TIP);
                }else{
//                    User user = new User();
//                    user.setUsername(userName);
//                    user.setPassword(pw1);
//                    user.setType(Constant.ID);
//                    con.register(user);
                    StartRead(getResources().getString(R.string.login_success), Config.ACK_LOGIN_SUCCESS_RETURN);
                }
                break;
            case Config.ACK_LOGIN_SUCCESS_RETURN:
                gotoLogin();
                break;
            case Config.ACK_CLICK:
                StopRead();
                StartRead(getResources().getString(R.string.username),Config.ACK_REGISTER_USERNAME_TIP);
                break;
            case Config.ACK_LONG_CLICK:
                StopRead();
                removeActivity();
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //TODO 得到编辑框里的值
        registerButton = (Button) findViewById(R.id.register_createButton);
        idEditText = (EditText) findViewById(R.id.register_id_EditView);
        pwEditText1 = (EditText) findViewById(R.id.register_password1_EditView);
        pwEditText2 = (EditText) findViewById(R.id.register_password2_EditView);
        jBlindView = (ImageView) findViewById(R.id.register_jBlindView);

        if(Constant.ID.equals("1")){
            //设置事件监听，要修改ImageView的值
            final GestureDetectorCompat mGesturedetector;
            mGesture gesture = new mGesture();
            mGesturedetector = new GestureDetectorCompat (this,gesture);//这里要先设置监听的哦,不然的话会报空指针异常.
            jBlindView.setVisibility(View.VISIBLE);
            jBlindView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mGesturedetector.onTouchEvent(event);
                    return true;
                }
            });
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = idEditText.getText().toString();
                String password1 = pwEditText1.getText().toString();
                String password2 = pwEditText2.getText().toString();
                String type = Constant.ID;
                System.out.println("获取注册数据： "+id+password1+type);

                if(id == null || id == ""){
                    Toast.makeText(RegisterActivity.this,"",Toast.LENGTH_SHORT).show();
                }else if(password1 == null || password1 == "" || password1 == null || password1 == ""){
                    Toast.makeText(RegisterActivity.this,"",Toast.LENGTH_SHORT).show();
                }else{
                    //TODO 使用USER创建并调用register();
//                      User user = new User();
//                      user.setUsername(id);
//                      user.setPassword(password1);
//                      user.setType(type);
//                      con.register(user);
                         gotoLogin();
                }
            }
        });
    }

    private void gotoLogin(){
        StopRead();
        removeActivity();
        Intent intent = new Intent();
        intent.setClass(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
