package com.moirai.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.moirai.client.Config;
import com.moirai.client.Conmmunication;
import com.moirai.client.R;
import com.moirai.model.User;

import java.util.LinkedList;

public class TestActivity extends BaseActivity {

    public static Conmmunication con;
    // public static NetWorker net;
    private Button register; // 注册
    private Button login; // 登陆
    private Button sendmessage; // 发送消息
    private Button getmessageforone; // 获取和某个人的消息列表
    // private Button getmessageforall;// 全部
    private Button getnewmessage;
    private Button requireFriend;// 发送摇一摇
    private Button addFriend;// 是否添加好友的结果
    private Button downloadFriend;// 下载朋友列表
    private Button downloadMoments;// 下载朋友圈

    protected static LinkedList<BaseActivity> queue = new LinkedList<BaseActivity>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        if (!queue.contains(this)) {
            queue.add(this);
            // System.out.println("��" + queue.getLast() + "��ӵ�list��ȥ");
        }
        // 获取单实例
        con = Conmmunication.newInstance();

        // 注册
        register = (Button) findViewById(R.id.btn_send_register);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                User user = new User();
                user.setPassword("123123");
                user.setUsername("Test001");
                user.setType("1");
                con.register(user);
            }

        });

        // 登陆
        login = (Button) findViewById(R.id.btn_send_login);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                User user = new User();
                user.setPassword("123123");
                user.setUsername("Test001");
                con.login(user);
            }

        });

        // 发送消息
        sendmessage = (Button) findViewById(R.id.btn_send_message);
        sendmessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String name1 = "Test001";
                String name2 = "Test002";
                String mess = "今天是个好日子啊啊啊啊 啊 ";
                con.sendInfo(name1, name2, mess);
            }

        });

        // 获取和某个人的消息
        getmessageforone = (Button) findViewById(R.id.btn_get_message1);
        getmessageforone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String name1 = "Test001";
                String name2 = "Test002";
                con.getmessage(name1, name2);
            }

        });
		/*
		 * // 获取和所有人的消息 // getmessageforall = (Button)
		 * findViewById(R.id.btn_get_message2); //
		 * getmessageforall.setOnClickListener(new OnClickListener() { // public
		 * void onClick(View view) { // // String name1 = "Test001"; //
		 * con.downloadInfo(name1); // } // // });
		 */
        getnewmessage = (Button) findViewById(R.id.btn_get_newmessage);
        getnewmessage.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String name = "Test002";
                con.getnewmessage(name);

            }
        });
        //摇一摇
        requireFriend = (Button) findViewById(R.id.btn_requireFriend);
        requireFriend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String name = "Test001";
                con.requireFriend(name);

            }
        });
        addFriend = (Button) findViewById(R.id.btn_addFriend);
        addFriend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String name1 = "Test001";
                String name2 = "Test002";
                int answer = 1;
                con.addFriend(name1, name2, answer);
            }
        });

    }

    @Override
    public void processMessage(Message message) {

    }
}
