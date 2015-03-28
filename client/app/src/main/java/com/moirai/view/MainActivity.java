package com.moirai.view;

import android.os.Message;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.moirai.client.Config;
import com.moirai.client.Constant;
import com.moirai.client.R;
import com.moirai.model.Info;
import com.moirai.model.User;

import java.util.List;

public class MainActivity extends BaseActivity {
    private List<Info> list;

    @Override
    public void processMessage(Message message) {
        switch(message.what){
            case Config.REQUEST_DOWNLOAD_INFO:
                list = (List<Info>) message.obj;
                db.saveDownloadInfo(list);
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
        //TODO 使用USER创建并调用downInfo();
        con.downloadInfo("lanlan");
    }

}
