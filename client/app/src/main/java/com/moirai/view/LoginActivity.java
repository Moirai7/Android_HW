package com.moirai.view;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.moirai.client.Config;
import com.moirai.client.Constant;
import com.moirai.client.R;

public class LoginActivity extends BaseActivity {

    @Override
    public void processMessage(Message message) {
        switch(message.what){
            case Config.REQUEST_LOGIN:
                int result = message.arg1;
                if(result == Config.SUCCESS){
                    //TODO 得到编辑框里的值
                    //Constant.USERNAME = edit_username_log.getText().toString();
                    //Constant.PASSWORD = edit_password_log.getText().toString();
                    //db.setUserInfo(Constant.USERNAME,Constant.PASSWORD);

                    Toast.makeText(LoginActivity.this, "用户" + Constant.USERNAME + "登陆成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
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
    }

}
