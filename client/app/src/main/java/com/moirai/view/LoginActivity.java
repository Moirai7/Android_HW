package com.moirai.view;

import android.content.Intent;
import android.os.Message;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.moirai.client.Config;
import com.moirai.client.Constant;
import com.moirai.client.R;
import com.moirai.model.User;

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
                    Constant.USERNAME = username_edit.getText().toString();
                    Constant.PASSWORD = password_edit.getText().toString();
                    //db.setUserInfo(Constant.USERNAME,Constant.PASSWORD);

                    Toast.makeText(LoginActivity.this, "用户" + Constant.USERNAME + "登陆成功", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent();
//                    intent.setClass(LoginActivity.this,MainActivity.class);
//                    startActivity(intent);
//                    finish();
                }else{
                    Toast.makeText(LoginActivity.this, "用户" + Constant.USERNAME + "登陆失败", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
                   /* User user = new User();
                    user.setUsername(username);
                    user.setPassword(password);
                    con.login(user);
                    //TODO 自动登录方法
                    db.getUserInfo();
                    User user1 = new User();
                    user.setUsername(Constant.USERNAME);
                    user.setPassword(Constant.PASSWORD);
                   con.login(user1);*/
                }
            }
        });



    }

}
