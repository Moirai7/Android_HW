package com.moirai.view;

import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.moirai.client.Config;
import com.moirai.client.Constant;
import com.moirai.client.R;

public class FriendActivity extends BaseActivity {

    @Override
    public void processMessage(Message message) {
        switch(message.what){
            case Config.REQUEST_REQUIRE_FRIEND:
                //TODO 弹出dlg框，提示要不要添加
                String name = (String)message.obj;
                //TODO 点击添加
                con.addFriend(Constant.USERNAME,name,Config.SUCCESS);
                //TODO 点击拒绝
                con.addFriend(Constant.USERNAME,name,Config.FAIl);
                break;
            case Config.REQUEST_ADDFRIEND:
                int result = message.arg1;
                if(result==Config.SUCCESS)
                    //TODO 提示成功
                    ;
                else
                //TODO 提示失败
                    ;
                break;
            default:
                break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        con.requireFriend(Constant.USERNAME);
    }
}
