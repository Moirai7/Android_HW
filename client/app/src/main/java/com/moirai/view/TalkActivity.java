package com.moirai.view;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
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
import com.moirai.voice.VoiceService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TalkActivity extends BaseActivity {

    private ImageButton refreshIBbtn;
    private ImageButton changeIBtn;
    private EditText msgEdit;
    private Button sendBtn;
    private ImageView jblind;

    private ListView mListView;
    private TalkMsgViewAdapter mAdapter;
    private List<Info> mDataArrays = new ArrayList<Info>();
    private int mInfoNews=0;
    private String currFriend;//当前聊天的朋友
    @Override
    public void processMessage(Message message) {
        // TODO Auto-generated method stub
        switch(message.what){
            case Config.ACK_CON_SUCCESS:
                StartRead(getString(R.string.ack_talk_start),Config.ACK_TALK_START);
                break;
            case Config.ACK_DOWN:
                mInfoNews++;
                if(mInfoNews>=mDataArrays.size()){
                    //当前消息已经读完，需要从服务器下载新消息
                    con.getmessage(Constant.USERNAME,currFriend);
                }else {
                    //加一个判断
                    StartRead( (String) mDataArrays.get(mInfoNews).getSendUser()
                            + getString(R.string.main_chats_tip)
                            + (String) mDataArrays.get(mInfoNews).getDetail(),Config.ACK_NONE);
                }
                break;
            case Config.ACK_TOP:
                mInfoNews--;
                if(mInfoNews<0) {
                    mInfoNews = 0;
                    StartRead(mDataArrays.get(mInfoNews).getDetail(), Config.ACK_NONE);
                    StartRead("没有更多消息记录了", Config.ACK_NONE);
                }else{
                    StartRead(mDataArrays.get(mInfoNews).getDetail(), Config.ACK_NONE);
                }
                break;
            //长按发送消息
            case Config.ACK_LONG_CLICK:
                StartListen(Config.ACK_TALKING);
                break;
            case Config.ACK_TALKING:
                String str = (String)message.obj;
                msgEdit.setText(str);
                send();
                break;
            //服务器传来的更新消息
            case Config.REQUEST_GET_MESSAGE:
                List<Info> list = (List<Info>)message.obj;
                if(list.isEmpty()){
                    mInfoNews--;
                    StartRead(getResources().getString(R.string.noNewMessage), Config.ACK_NONE);
                }else {
                    updateData(list);
                }
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
        currFriend=(String)this.getIntent().getSerializableExtra("username");//当前聊天的朋友
        actionBar.setTitle(getString(R.string.talking_with)+ currFriend);

        refreshIBbtn = (ImageButton) findViewById(R.id.talk_refreshIbtn);
     //   changeIBtn = (ImageButton) findViewById(R.id.talk_text_voice_imageBtn);
        msgEdit = (EditText) findViewById(R.id.talk_editView);
        sendBtn = (Button) findViewById(R.id.talk_sendBtn);
        mListView = (ListView) findViewById(R.id.listview);
        //正常人点击刷新按钮，刷新消息
        refreshIBbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        con.getmessage(Constant.USERNAME,currFriend);
                /*Intent intent = new Intent();
                intent.setClass(TalkActivity.this,CameraActivity.class);
                startActivity(intent);*/
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
       //从本地数据库获取消息，显示在聊天页面
        List<Info> tempList = db.getFriendHistory(currFriend);
        for(int i=0;i< tempList.size();i++){
            mDataArrays.add(tempList.get(i));
            mInfoNews=mDataArrays.size()-1;
        }
        System.out.println("从本地数据库获取消息的数量"+tempList.size());
        mAdapter = new TalkMsgViewAdapter(this, mDataArrays);
        mListView.setAdapter(mAdapter);
    }
    @Override
    protected void onDestroy() {
        if (queue.contains(this)) {
            queue.remove(this);
            System.out.println("将" + this + "移出list"+"queue : " + queue.toString());
        }
        if(connection_voice!=null)
            unbindService(connection_voice);
        super.onDestroy();
    }
    /**
     * 盲人下滑更新数据（从服务器下载最新消息）
     */
    public void updateData(List<Info> tempList){
        //先将从服务器下载的新消息保存到本地（下载朋友发送的新消息）
        for(int i=0;i<tempList.size();i++){
            db.saveFriendHistory(tempList.get(i));
        }
         mDataArrays = null;
        List<Info> list = db.getFriendHistory(currFriend);
        for(int i=0;i< list.size();i++){
            mDataArrays.add(tempList.get(i));
            mInfoNews=mDataArrays.size()-1;
        }
        mAdapter = new TalkMsgViewAdapter(this, mDataArrays);
        mListView.setAdapter(mAdapter);
    }
    //发消息给朋友
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
            con.sendInfo(Constant.USERNAME,currFriend,contString);
            //db.saveFriendHistory(entity);

            mListView.setSelection(mListView.getCount() - 1);
        }
    }

    private String getDate() {
        Calendar c = Calendar.getInstance();

        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH)+1);
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
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

    @Override
    protected void onResume() {
        //StartRead(getResources().getString(R.string.welcome_back),Config.ACK_NONE);
        if(!Constant.setBlind){
            //设置事件监听，要修改ImageView的值
            //removeActivity();
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
            if(voice_binder==null){
                connection_voice = new ServiceConnection() {

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        voice_flag = false;
                    }

                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        voice_binder = (VoiceService.MyBinder) service;
                        voice_flag = true;
                        Log.v("tag", "bind");

                        Message msg = Message.obtain();
                        msg.what = Config.ACK_CON_SUCCESS;
                        BaseActivity.sendMessage(msg);
                    }
                };

                Intent intent_voice_service = new Intent(this, VoiceService.class);
                startService(intent_voice_service);
                bindService(intent_voice_service, connection_voice, BIND_AUTO_CREATE);
            }
        }else{
            if(!Constant.ID.equals("1")){
                ImageView iv = (ImageView)findViewById(R.id.talk_jblind);
                iv.setVisibility(View.GONE);
            }
        }

        super.onResume();
    }

    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
