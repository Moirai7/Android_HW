package com.moirai.view;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Switch;

import com.moirai.client.Config;
import com.moirai.client.Conmmunication;
import com.moirai.client.Constant;
import com.moirai.client.R;
import com.moirai.model.Info;
import com.moirai.voice.VoiceService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements MainFragment.OnFragmentInteractionListener,ContactFragment.OnFragmentInteractionListener,ShareFragment.OnFragmentInteractionListener {
    private List<Info> list;
    private SimpleAdapter adapter; // binds tags to ListView
    private int[][] position = new int[3][2];
    private RadioButton[] rb = new RadioButton[3];
    /**
     * Called when the activity is first created.
     */
    private RadioGroup rgs;
    public List<Fragment> fragments = new ArrayList<Fragment>();
    private int theFragment=0;
    private int theInfoId=0;

    private void setSimulateClick(View view, float x, float y) {
        long downTime = SystemClock.uptimeMillis();
        final MotionEvent downEvent = MotionEvent.obtain(downTime, downTime,
                MotionEvent.ACTION_DOWN, x, y, 0);
        downTime += 1000;
        final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime,
                MotionEvent.ACTION_UP, x, y, 0);
        view.onTouchEvent(downEvent);
        view.onTouchEvent(upEvent);
        downEvent.recycle();
        upEvent.recycle();
    }
    @Override
    public void processMessage(Message message) {
        switch(message.what){
            case Config.REQUEST_DOWNLOAD_INFO:
                list = (List<Info>) message.obj;
                //db.saveDownloadInfo(list);
                break;
            case Config.ACK_CON_SUCCESS:
                StartRead(getResources().getString(R.string.main_welcome),Config.ACK_NONE);//欢迎
                break;
            case Config.ACK_DOWN:
                if(rgs.getVisibility()!=View.GONE){
                    theInfoId++;
                    if(theInfoId>=data.size())
                        theInfoId=0;
                    StartListRead();

                }
                break;
            case Config.ACK_TOP:
                if(rgs.getVisibility()!=View.GONE){
                    theInfoId--;
                    if(theInfoId<0)
                        theInfoId=0;
                    StartListRead();

                }
                break;
            case Config.ACK_LEFT://0是首页，1是contact，2是朋友圈
                if(rgs.getVisibility()!=View.GONE) {
                    int p = (theFragment + 2) % 3;
                    setSimulateClick(rb[p], position[p][0], position[p][1]);
                    //tabListener.OnRgsExtraCheckedChanged(rgs,(theFragment+2)%3,(theFragment+2)%3);
                    switch (p) {
                        case 0:
                            StartRead(getResources().getString(R.string.main_mainPage), Config.ACK_NONE);
                            break;
                        case 1:
                            StartRead(getResources().getString(R.string.main_contactPage), Config.ACK_NONE);
                            break;
                        case 2:
                            StartRead(getResources().getString(R.string.main_sharePage), Config.ACK_NONE);
                            break;
                    }
                }
                break;
            case Config.ACK_RIGHT://0是首页，1是contact，2是朋友圈
                if(rgs.getVisibility()!=View.GONE) {
                    int q = (theFragment + 1) % 3;
                    setSimulateClick(rb[q], position[q][0], position[q][1]);
                    switch (q) {
                        case 0:
                            StartRead(getResources().getString(R.string.main_mainPage), Config.ACK_NONE);
                            break;
                        case 1:
                            StartRead(getResources().getString(R.string.main_contactPage), Config.ACK_NONE);
                            break;
                        case 2:
                            StartRead(getResources().getString(R.string.main_sharePage), Config.ACK_NONE);
                            break;
                    }
                }
                break;
            case Config.ACK_LONG_CLICK:
                if(rgs.getVisibility()!=View.GONE) {
                    if (theFragment == 0 || theFragment == 1) {
                        Intent intent = new Intent();
                        //设置传递方向
                        intent.setClass(MainActivity.this, TalkActivity.class);
                        intent.putExtra("username", (String) data.get(theInfoId).get("title"));
                        this.startActivity(intent);
                    } else if (theFragment == 2) {
                        onShareFragmentInteraction(theInfoId);
                    }
                }
                break;
            case Config.ACK_LIST_READ:
                String content;
                switch(theFragment){
                    case 0:
                         content =(String)data.get(theInfoId).get("title")
                                 + getString(R.string.main_chats_tip)
                                 +(String)data.get(theInfoId).get("content");
                         StartRead(content,Config.ACK_NONE);
                         break;
                    case 1:
                        content = (String)data.get(theInfoId).get("title");
                        StartRead(content,Config.ACK_NONE);
                        break;
                    case 2:
                        content = (String)data.get(theInfoId).get("title")
                                + getString(R.string.main_share_tip)
                                + (String)data.get(theInfoId).get("content");
                        StartRead(content,Config.ACK_NONE);
                        break;
                    default:
                        break;

                 }
                break;
            case Config.ACK_DOUBLE_CLICK:
                if(rgs.getVisibility()!=View.GONE) {
                    if (theFragment == 0) {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, SettingActivity.class);
                        startActivity(intent);
                        Constant.isSetting = true;
                    } else if (theFragment == 1) {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, ShakeActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            default:
                break;
        }
    }
    private List<Map<String, Object>> data=null;
    FragmentTabAdapter.OnRgsExtraCheckedChangedListener tabListener= new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
        @Override
        public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
            theInfoId=0;
            switch (index) {
                case 0:
                    theFragment = 0;
                    data = list1;
                    // create ArrayAdapter and use it to bind tags to the ListView
                    adapter = new SimpleAdapter(getApplicationContext(), data, R.layout.list_item,
                            new String[]{"title", "img","date","content"},
                            new int[]{R.id.nameView, R.id.photoView,R.id.dateView,R.id.contentView});
                    break;
                case 1:
                    theFragment = 1;
                    data = list2;
                    adapter = new SimpleAdapter(getApplicationContext(), data, R.layout.list_item,
                            new String[]{"title", "img","date","content"},
                            new int[]{R.id.nameView, R.id.photoView,R.id.dateView,R.id.contentView});
                    break;
                case 2:
                    theFragment = 2;
                    data = list3;
                    adapter = new SimpleAdapter(getApplicationContext(), data, R.layout.list_item,
                            new String[]{"title", "img","date","content"},
                            new int[]{R.id.nameView, R.id.photoView,R.id.dateView,R.id.contentView});
                    break;
            }
            if(Constant.ID.equals("1")) {
                StartListRead();
            }
            System.out.println("Extra---- " + index + " checked!!! ");
        }
    };
    private void StartListRead(){
        StopRead();
        Message msg = Message.obtain();
        msg.what = Config.ACK_LIST_READ;
        BaseActivity.sendMessage(msg);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Constant.ID.equals("1")){
            //设置事件监听，要修改ImageView的值
            final GestureDetectorCompat mGesturedetector;
            mGesture gesture = new mGesture();
            mGesturedetector = new GestureDetectorCompat (this,gesture);//这里要先设置监听的哦,不然的话会报空指针异常.
            ImageView iv = (ImageView)findViewById(R.id.llblindView);
            iv.setVisibility(View.VISIBLE);
            iv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mGesturedetector.onTouchEvent(event);
                    return true;
                }
            });
        }

        //TODO 得到编辑框里的值
        //TODO 使用USER创建并调用downInfo();
       // con.downloadInfo("lanlan");
        //Fragment
        fragments.add(new MainFragment());
        fragments.add(new ContactFragment());
        fragments.add(new ShareFragment());

        rgs = (RadioGroup) findViewById(R.id.tabs_rg);
        rb[0] = (RadioButton)findViewById(R.id.tab_rb_a);
        rb[1] = (RadioButton)findViewById(R.id.tab_rb_b);
        rb[2] = (RadioButton)findViewById(R.id.tab_rb_c);
        rb[0].getLocationInWindow(position[0]);
        rb[1].getLocationInWindow(position[1]);
        rb[2].getLocationInWindow(position[2]);

        getData(con.downloadInfo(Constant.USERNAME));
        getData2();
        getData3();
        System.out.println("getLocationOnScreen:" + position[0] + "," + position[1]);
//        theFragment = 0;
//        data = getData();
//        adapter = new SimpleAdapter(this,data,R.layout.list_item,
//                new String[]{"title","img"},
//                new int[]{R.id.nameView,R.id.photoView});

        FragmentTabAdapter tabAdapter = new FragmentTabAdapter(this, fragments, R.id.tab_content, rgs);

        tabAdapter.setOnRgsExtraCheckedChangedListener(tabListener);
        ActionBar actionBar = getActionBar();
        if(!queue.getLast().toString().contains("MainActivity")){
            actionBar.setIcon(null);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }else{
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            //  actionBar.setIcon(R.drawable.tabchat_selected);
            actionBar.setIcon(null);
        }
        tabListener.OnRgsExtraCheckedChanged(rgs,0,0);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO ICON
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        StartRead(getResources().getString(R.string.welcome_back),Config.ACK_NONE);
        if(!Constant.setBlind){
            //设置事件监听，要修改ImageView的值
            removeActivity();
            final GestureDetectorCompat mGesturedetector;
            mGesture gesture = new mGesture();
            mGesturedetector = new GestureDetectorCompat (this,gesture);//这里要先设置监听的哦,不然的话会报空指针异常.
            ImageView iv = (ImageView)findViewById(R.id.llblindView);
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
                ImageView iv = (ImageView)findViewById(R.id.llblindView);
                iv.setVisibility(View.GONE);
            }
        }

        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //当点击不同的menu item 是执行不同的操作
        switch (id) {
            case R.id.action_settings:
                if(!Constant.isSetting){
                    if(theFragment==0){
                        Intent intent = new Intent();
                        intent.setClass(queue.getLast(),SettingActivity.class);
                        startActivity(intent);
                        Constant.isSetting = true;
                    }else if(theFragment==1){
                        Intent intent = new Intent();
                        intent.setClass(queue.getLast(),ShakeActivity.class);
                        startActivity(intent);
                    }
                }
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
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if(fm.getBackStackEntryCount()>0){
            fm.popBackStack();
        }else{
            super.onBackPressed();
        }
        rgs.setVisibility(View.VISIBLE);
    }
    // ADDED to set up the ListFragment
    public SimpleAdapter getAdapter(){return adapter;}
//聊天
    private List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
    private void getData(List<Info> InfoList) {

 //InfoList 是从服务器传来的全部消息
        for(int i=0;i<InfoList.size();i++){
            String sendUser = InfoList.get(i).getSendUser();
            String time =  InfoList.get(i).getTime();
            String detail = InfoList.get(i).getDetail();
            //只显示最新的那条消息
            if(i==0||!sendUser.equals(InfoList.get(i-1).getSendUser())) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("title", sendUser);
                map.put("img", R.mipmap.pic1);
                map.put("date", time);
                map.put("content", detail);//最新的一条消息
                list1.add(map);
            }
        }
       /* map.put("title", getResources().getString(R.string.main_title1));//String
        map.put("img", R.mipmap.pic1);
        map.put("date",getResources().getString(R.string.main_date1));
        map.put("content",getResources().getString(R.string.main_content1));
        list1.add(map);

        map = new HashMap<String, Object>();
        map.put("title", getResources().getString(R.string.main_title2));//String
        map.put("img", R.mipmap.pic2);
        map.put("date",getResources().getString(R.string.main_date2));
        map.put("content",getResources().getString(R.string.main_content2));
        list1.add(map);

        map = new HashMap<String, Object>();
        map.put("title", getResources().getString(R.string.main_title3));//String
        map.put("img", R.mipmap.pic3);
        map.put("date",getResources().getString(R.string.main_date3));
        map.put("content",getResources().getString(R.string.main_content3));
        list1.add(map);*/
    }
//contact
    private List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
    private void getData2() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", getResources().getString(R.string.main_contact1));
        map.put("img", R.mipmap.pic4);
        list2.add(map);

        map = new HashMap<String, Object>();
        map.put("title", getResources().getString(R.string.main_contact2));
        map.put("img", R.mipmap.pic5);
        list2.add(map);

        map = new HashMap<String, Object>();
        map.put("title", getResources().getString(R.string.main_contact3));
        map.put("img", R.mipmap.pic6);
        list2.add(map);
    }
//share
    private List<Map<String, Object>> list3 = new ArrayList<Map<String, Object>>();
    private void getData3() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", getResources().getString(R.string.main_title1));//String
        map.put("img", R.mipmap.pic1);
        map.put("date",getResources().getString(R.string.main_date1));
        map.put("content",getResources().getString(R.string.main_share1));
        list3.add(map);

        map = new HashMap<String, Object>();
        map.put("title", getResources().getString(R.string.main_title2));//String
        map.put("img", R.mipmap.pic2);
        map.put("date",getResources().getString(R.string.main_date2));
        map.put("content",getResources().getString(R.string.main_share2));
        list3.add(map);

        map = new HashMap<String, Object>();
        map.put("title", getResources().getString(R.string.main_title3));//String
        map.put("img", R.mipmap.pic3);
        map.put("date",getResources().getString(R.string.main_date3));
        map.put("content",getResources().getString(R.string.main_share3));
        list3.add(map);

        map = new HashMap<String, Object>();
        map.put("title", getResources().getString(R.string.main_contact1));//String
        map.put("img", R.mipmap.pic4);
        map.put("date",getResources().getString(R.string.main_date3));
        map.put("content",getResources().getString(R.string.main_share1));
        list3.add(map);

        map = new HashMap<String, Object>();
        map.put("title", getResources().getString(R.string.main_contact2));//String
        map.put("img", R.mipmap.pic5);
        map.put("date",getResources().getString(R.string.main_date1));
        map.put("content",getResources().getString(R.string.main_share2));
        list3.add(map);

        map = new HashMap<String, Object>();
        map.put("title", getResources().getString(R.string.main_contact3));//String
        map.put("img", R.mipmap.pic6);
        map.put("date",getResources().getString(R.string.main_date1));
        map.put("content",getResources().getString(R.string.main_share3));
        list3.add(map);

        map = new HashMap<String, Object>();
        map.put("title", getResources().getString(R.string.main_contact1));//String
        map.put("img", R.mipmap.pic7);
        map.put("date",getResources().getString(R.string.main_date3));
        map.put("content",getResources().getString(R.string.main_share1));
        list3.add(map);

        map = new HashMap<String, Object>();
        map.put("title", getResources().getString(R.string.main_contact2));//String
        map.put("img", R.mipmap.pic8);
        map.put("date",getResources().getString(R.string.main_date3));
        map.put("content",getResources().getString(R.string.main_share2));
        list3.add(map);

        map = new HashMap<String, Object>();
        map.put("title", getResources().getString(R.string.main_contact3));//String
        map.put("img", R.mipmap.pic9);
        map.put("date",getResources().getString(R.string.main_date3));
        map.put("content",getResources().getString(R.string.main_share1));
        list3.add(map);
    }

    @Override
    public void onMainFragmentInteraction(int position) {
        Message msg = Message.obtain();
        msg.what = Config.ACK_LONG_CLICK;
        theInfoId=position;
        BaseActivity.sendMessage(msg);
    }

    @Override
    public void onContactFragmentInteraction(int position) {
        Message msg = Message.obtain();
        msg.what = Config.ACK_LONG_CLICK;
        theInfoId=position;
        BaseActivity.sendMessage(msg);
    }

    @Override
    public void onShareFragmentInteraction(int position) {
        rgs.setVisibility(View.GONE);
        getFragmentManager().beginTransaction().replace(R.id.tab_content,DetailFragment.newInstance((Integer)list3.get(position).get("img"),(String)list3.get(position).get("content"))).addToBackStack(null).commit();
    }
}
