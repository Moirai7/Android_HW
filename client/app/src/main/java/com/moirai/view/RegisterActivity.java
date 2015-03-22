package com.moirai.view;

import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.moirai.client.Config;
import com.moirai.client.Conmmunication;
import com.moirai.client.R;
import com.moirai.model.User;

public class RegisterActivity extends BaseActivity {

    @Override
    public void processMessage(Message message) {
        switch(message.what){
            case Config.REQUEST_REGISTER:
                int result = message.arg1;
                if(result == Config.SUCCESS){
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                    //TODO 注意编辑框的值要设置为空，像下面一样
//                    edit_username_reg.setText("");
//                    edit_password1_reg.setText("");
//                    edit_password2_reg.setText("");
                }
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
        //TODO 使用USER创建并调用register();
        User user = new User();
        user.setUsername("lanlan");
        user.setPassword("123");
        user.setType("1");
        con.register(user);
    }

}
