package com.moirai.view;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.moirai.client.Config;
import com.moirai.client.Constant;
import com.moirai.client.R;
import com.moirai.model.Info;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TalkActivity extends BaseActivity {

    private ImageButton cameraIBbtn;
    private ImageButton changeIBtn;
    private EditText msgEdit;
    private Button sendBtn;
    private ImageView jblind;

    private ListView mListView;
    private TalkMsgViewAdapter mAdapter;
    private List<Info> mDataArrays = new ArrayList<Info>();
    private int mInfoNews=0;
    private String currFriend;//当前聊天的朋友
    /*private String[]msgArray = new String[]{"请叫我漂漂的岚岚姐", "叫你谁？", "漂漂的岚岚姐啊", "什么的岚岚姐？",
            "漂漂的岚岚姐", "叫姐有饭吃吗？",
            "想吃什么都可以", "okay~"};*/
 //   private String[]msgArray;
 // private String[]dataArray;
/*    private String[]dataArray = new String[]{"2015-03-01 18:00", "2015-03-01 18:10",
            "2015-03-01 18:11", "2015-03-01 18:20",
            "2015-03-01 18:30", "2015-03-01 18:35",
            "2015-03-01 18:40", "2015-03-01 18:50"};*/
  //  private final static int COUNT = 8;

    @Override
    public void processMessage(Message message) {
        // TODO Auto-generated method stub
        switch(message.what){
            case Config.ACK_CON_SUCCESS:
                StartRead(getString(R.string.ack_talk_start),Config.ACK_TALK_START);
                break;
            case Config.ACK_TALK_START:
                if(mDataArrays.isEmpty()){

                }
                break;
            case Config.ACK_SERVICE:

                break;
            case Config.ACK_DOWN:
                mInfoNews++;
                if(mInfoNews>=mDataArrays.size()){
                    //当前消息已经读完，需要从服务器下载新消息
                  updateData();
                }
                    //mInfoNews--;
                StartRead(mDataArrays.get(mInfoNews).getDetail(),Config.ACK_NONE);
                break;
            case Config.ACK_TOP:
                mInfoNews--;
                if(mInfoNews<0)
                    mInfoNews=0;
                StartRead(mDataArrays.get(mInfoNews).getDetail(),Config.ACK_NONE);
                break;
            case Config.ACK_LONG_CLICK:
                StartListen(Config.ACK_TALKING);
                break;
            case Config.ACK_TALKING:
                String str = (String)message.obj;
                msgEdit.setText(str);
                send();
                break;
            default:
                break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_talk);

        if(Constant.ID.equals("1")){
            //设置事件监听，要修改ImageView的值
            final GestureDetectorCompat mGesturedetector;
            mGesture gesture = new mGesture();
            mGesturedetector = new GestureDetectorCompat (this,gesture);//这里要先设置监听的哦,不然的话会报空指针异常.
            ImageView iv = (ImageView)findViewById(R.id.talk_jblind);
            iv.setVisibility(View.VISIBLE);
            iv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mGesturedetector.onTouchEvent(event);
                    return true;
                }
            });
        }

        ActionBar actionBar = getActionBar();
        actionBar.setIcon(null);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        currFriend=(String)this.getIntent().getSerializableExtra("title");//当前聊天的朋友
        actionBar.setTitle(getString(R.string.talking_with)+ currFriend);

        cameraIBbtn = (ImageButton) findViewById(R.id.talk_cameraIbtn);
     //   changeIBtn = (ImageButton) findViewById(R.id.talk_text_voice_imageBtn);
        msgEdit = (EditText) findViewById(R.id.talk_editView);
        sendBtn = (Button) findViewById(R.id.talk_sendBtn);
        mListView = (ListView) findViewById(R.id.listview);

        cameraIBbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(TalkActivity.this,CameraActivity.class);
                startActivity(intent);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });

        initData();
    }

    public void initData()
    {

/*       for(int i = 0; i < COUNT; i++)
        {
            Info entity = new Info();
            entity.setTime(dataArray[i]);
            if (i % 2 == 0)
            {
                entity.setSendUser(Constant.USERNAME);
                entity.setReceiver("fish");
               // entity.setMsgType(true);
            }else{
                entity.setSendUser("fish");
                entity.setReceiver(Constant.USERNAME);
              //  entity.setMsgType(false);
            }

            entity.setDetail(msgArray[i]);
            mDataArrays.add(entity);
            mInfoNews=mDataArrays.size()-1;
        }*/
       //从本地数据库获取消息，显示在聊天页面
        List<Info> tempList = db.getFriendHistory(currFriend);
        for(int i=0;i< tempList.size();i++){
            mDataArrays.add(tempList.get(i));
            mInfoNews=mDataArrays.size()-1;
        }
        mAdapter = new TalkMsgViewAdapter(this, mDataArrays);
        mListView.setAdapter(mAdapter);
    }

    /**
     * 下滑更新数据（从服务器下载最新消息）
     */
    public void updateData(){

        con.getmessage(Constant.USERNAME,currFriend);

        //TODO
//        for(int i=0;i< tempList.size();i++){
//            mDataArrays.add(tempList.get(i));
//            mInfoNews=mDataArrays.size()-1;
//        }
//        mAdapter = new TalkMsgViewAdapter(this, mDataArrays);
//        mListView.setAdapter(mAdapter);
    }
    private void send()
    {
        String contString = msgEdit.getText().toString();
        if (contString.length() > 0)
        {
            Info entity = new Info();
            entity.setTime(getDate());
            entity.setSendUser(Constant.USERNAME);
            entity.setReceiver(currFriend);//发送消息时，currFriend 作为接受方
            entity.setDetail(contString);

            mDataArrays.add(entity);
            mAdapter.notifyDataSetChanged();

            msgEdit.setText("");
            db.saveFriendHistory(entity);

            mListView.setSelection(mListView.getCount() - 1);
        }
    }

    private String getDate() {
        Calendar c = Calendar.getInstance();

        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH));
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String mins = String.valueOf(c.get(Calendar.MINUTE));

        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":" + mins);

        return sbBuffer.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO ICON
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //当点击不同的menu item 是执行不同的操作
        switch (id) {
            case R.id.action_settings:
                  Intent intent = new Intent();
                  intent.setClass(TalkActivity.this,SettingActivity.class);
                  startActivity(intent);
                  break;
            case android.R.id.home:
                queue.getLast().finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
