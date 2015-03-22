package com.moirai.view;

import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.moirai.client.Config;
import com.moirai.client.Constant;
import com.moirai.client.R;
import com.moirai.model.User;

public class MainActivity extends BaseActivity {

    @Override
    public void processMessage(Message message) {
        // TODO Auto-generated method stub
        switch(message.what){
            case Config.REQUEST_LOGIN:
                int result = message.arg1;
                if(result == Config.SUCCESS){
                    //TODO 得到编辑框里的值

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
        setContentView(R.layout.activity_main);
        //TODO 得到编辑框里的值
        //TODO 使用USER创建并调用login();
        User user = new User();
        user.setUsername("lanlan");
        user.setPassword("123");
        con.login(user);
    }

}
