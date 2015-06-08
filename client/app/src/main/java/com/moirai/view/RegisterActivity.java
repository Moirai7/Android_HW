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

import static com.moirai.client.Constant.PASSWORD;
import static com.moirai.client.Constant.USERNAME;

public class RegisterActivity extends BaseActivity {
    private Button registerButton;
    private Button gotoLoginBtn;
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
                    db.setUserInfo(userName,pw1,Constant.ID);
                    Constant.USERNAME = userName;
                    Constant.PASSWORD = pw1;
                    Constant.ID = "1";
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
                StartRead(getString(R.string.register_start),Config.ACK_NONE);
                break;
            case Config.ACK_REGISTER_USERNAME_TIP:
                StartListen(Config.ACK_REGISTER_USERNAME);
                break;
            case Config.ACK_REGISTER_USERNAME:
                userName = ((String)message.obj);
                userName=userName.substring(0,userName.length()-1);
                if(userName.isEmpty()||userName.equals("LANLANERROR")){
                    StartRead(getString(R.string.tip_register_id),Config.ACK_REGISTER_USERNAME_TIP);
                    break;
                }
                idEditText.setText(userName);
                StartRead(getString(R.string.voice_enter_pw1),Config.ACK_REGISTER_PASSWORD_1_TIP);
                break;
            case Config.ACK_REGISTER_PASSWORD_1_TIP:
                StartListen(Config.ACK_REGISTER_PASSWORD_1);
                break;
            case Config.ACK_REGISTER_PASSWORD_1:
                pw1 = ((String)message.obj);
                pw1=pw1.substring(0,pw1.length()-1);
                pwEditText1.setText(pw1);
                StartRead(getString(R.string.voice_enter_pw2),Config.ACK_REGISTER_PASSWORD_2_TIP);
                break;
            case Config.ACK_REGISTER_PASSWORD_2_TIP:
                StartListen(Config.ACK_REGISTER_PASSWORD_2);
                break;
            case Config.ACK_REGISTER_PASSWORD_2:
                pw2 = ((String)message.obj);
                pw2=pw2.substring(0,pw2.length()-1);
                if(!pw1.equals(pw2)){
                    StartRead(getString(R.string.tip_regieter_pw_null)+getString(R.string.voice_enter_pw1),Config.ACK_REGISTER_PASSWORD_1_TIP);
                }else{
                    pwEditText2.setText(pw2);
                    User user = new User();
                    user.setUsername(userName);
                    user.setPassword(pw1);
                    user.setType(Constant.ID);
                    con.register(user);
                    //StartRead(getResources().getString(R.string.tip_register_successfully), Config.ACK_LOGIN_SUCCESS_RETURN);
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
        gotoLoginBtn = (Button) findViewById(R.id.register_gotoLogin_btn);
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

                if(id.isEmpty() || id.equals("")){
                    Toast.makeText(RegisterActivity.this,getString(R.string.tip_register_id),Toast.LENGTH_SHORT).show();
                }else if(password1.isEmpty() || password1.equals("") || password2.isEmpty()|| password2.equals("")){
                    Toast.makeText(RegisterActivity.this,getString(R.string.tip_regieter_pw_null),Toast.LENGTH_SHORT).show();
                }else{
                      User user = new User();
                      user.setUsername(id);
                      user.setPassword(password1);
                      user.setType(type);
                      con.register(user);
                        Toast.makeText(RegisterActivity.this,getString(R.string.tip_register_successfully),Toast.LENGTH_SHORT).show();
                        gotoLogin();
                }
            }
        });

        gotoLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
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
    private void gotoLogin(){
        StopRead();
        removeActivity();
        Intent intent = new Intent();
        intent.setClass(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
