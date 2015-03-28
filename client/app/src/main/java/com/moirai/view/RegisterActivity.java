package com.moirai.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText pwEditText;
    private Spinner typeSpinner;
    @Override
    public void processMessage(Message message) {
        switch(message.what){
            case Config.REQUEST_REGISTER:
                int result = message.arg1;
                if(result == Config.SUCCESS){
                    //TODO 成功要得到name等值并写到数据库
                    db.setUserInfo(Constant.USERNAME,Constant.PASSWORD,Constant.ID);
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                    //TODO 注意编辑框的值要设置为空，像下面一样
                    idEditText.setText("");
                    pwEditText.setText("");
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
        registerButton = (Button) findViewById(R.id.register_createButton);
        idEditText = (EditText) findViewById(R.id.register_id_EditView);
        pwEditText = (EditText) findViewById(R.id.register_password1_EditView);
        typeSpinner = (Spinner) findViewById(R.id.register_type);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = idEditText.getText().toString();
                String password = pwEditText.getText().toString();
                String type =String.valueOf(typeSpinner.getSelectedItemPosition());
                System.out.println("获取注册数据： "+id+password+type);

                if(id == null || id == ""){

                }else if(password == null || password == ""){

                }else{
                    //TODO 使用USER创建并调用register();
                      User user = new User();
                      user.setUsername(id);
                      user.setPassword(password);
                      user.setType(type);
                      con.register(user);
                }
            }
        });

    }
}
