package com.moirai.view;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;

import com.moirai.client.Config;
import com.moirai.client.Conmmunication;
import com.moirai.client.Constant;
import com.moirai.client.R;
import com.moirai.model.Friend;
import com.moirai.model.Info;
import com.moirai.model.Moments;
import com.moirai.model.News;
import com.moirai.voice.VoiceService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements MainFragment.OnFragmentInteractionListener,ContactFragment.OnFragmentInteractionListener,ShareFragment.OnFragmentInteractionListener,NewsFragment.OnFragmentInteractionListener{

    private SimpleAdapter adapter; // binds tags to ListView
    private int[][] position = new int[4][2];
    private RadioButton[] rb = new RadioButton[4];
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
                System.out.println("message的信息"+message.toString());
                List<Info> list_Info=(List<Info>)message.obj;
                if(!list_Info.isEmpty())
                    db.saveAllHistory(list_Info);
                //首页显示从本地数据库读取
                getData();
                data = list1;
                    SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), list1, R.layout.list_item,
                            new String[]{"title", "img","date","content"},
                            new int[]{R.id.nameView, R.id.photoView,R.id.dateView,R.id.contentView});
                    ((MainFragment)fragments.get(0)).setmAdapter(adapter);
                    if(Constant.ID.equals("1")) {
                        StartListRead();
                    }
                break;
            case Config.REQUEST_DOWNLOAD_FRIEND:
                //从服务器下载新朋友
                List<Friend> list_Friend = (List<Friend>)message.obj;
                if(list_Friend!=null)
                    db.saveFriends(list_Friend);
                getData2();
                if(((ContactFragment)fragments.get(1)).checkView()) {
                    data = list2;
                    SimpleAdapter adapter2 = new SimpleAdapter(getApplicationContext(), data, R.layout.list_item,
                            new String[]{"title", "img", "date", "content"},
                            new int[]{R.id.nameView, R.id.photoView, R.id.dateView, R.id.contentView});
                    ((ContactFragment) fragments.get(1)).setmAdapter(adapter2);
                    if (Constant.ID.equals("1")) {
                        StartListRead();
                    }
                }
                break;
            case Config.REQUEST_DOWNLOAD_MOMENTS:
                //下载最新的朋友圈消息
                List<Moments> list_Moment = (List<Moments>)message.obj;
                if(list_Moment!=null)
                    db.saveAllMoments(list_Moment);
                   getData3();
                if(((ShareFragment)fragments.get(2)).checkView()) {
                    data = list3;
                    SimpleAdapter adapter3 = new SimpleAdapter(getApplicationContext(), data, R.layout.list_item,
                            new String[]{"title", "img", "date", "content"},
                            new int[]{R.id.nameView, R.id.photoView, R.id.dateView, R.id.contentView});
                    ((ShareFragment) fragments.get(2)).setmAdapter(adapter3);
                    if (Constant.ID.equals("1")) {
                        StartListRead();
                    }
                }
                break;
            case Config.REQUEST_DOWNLOAD_NEWS:
                List<News> list_news = (List<News>)message.obj;
                getData4(list_news);
                if(((NewsFragment)fragments.get(3)).checkView()) {
                    data = list4;
                    SimpleAdapter adapter4 = new SimpleAdapter(getApplicationContext(), data, R.layout.newlist_item,
                            new String[]{"title","date"},
                            new int[]{R.id.nameView1, R.id.dateView1});
                    ((NewsFragment) fragments.get(3)).setmAdapter(adapter4);
                    if (Constant.ID.equals("1")) {
                        StartListRead();
                    }
               }
            case Config.ACK_DOWN:
                if(rgs.getVisibility()!=View.GONE){
                    if(!data.isEmpty()) {
                        theInfoId++;
                        if (theInfoId >= data.size())
                            theInfoId = 0;
                        if(Constant.ID.equals("1"))
                            StartListRead();
                    }else{
                        StartRead(getResources().getString(R.string.noNewMessage), Config.ACK_NONE);
                    }
                }
                break;
            case Config.ACK_TOP:
                if(rgs.getVisibility()!=View.GONE){
                    theInfoId--;
                    if(theInfoId<0)
                        theInfoId=0;
                    if(Constant.ID.equals("1"))
                        StartListRead();
                }
                break;
            case Config.ACK_LEFT://0是首页，1是contact，2是朋友圈
                if(rgs.getVisibility()!=View.GONE) {
                    int p = (theFragment + 3) % 4;
                    //theFragment=p;
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
                        case 3:
                            StartRead(getResources().getString(R.string.main_newsPage), Config.ACK_NONE);
//                           if(checkfirst) {
//                               con.downloadnews();
//                               checkfirst = false;
//                           }
                            break;
                    }
                }
                break;
            case Config.ACK_RIGHT://0是首页，1是contact，2是朋友圈
                if(rgs.getVisibility()!=View.GONE) {
                    int q = (theFragment + 1) % 4;
                    //theFragment=q;
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
                        case 3:
                            StartRead(getResources().getString(R.string.main_newsPage), Config.ACK_NONE);
//                            if(checkfirst) {
//                                con.downloadnews();
//                                checkfirst=false;
//                            }
                            break;
                    }
                }
                break;
            case Config.ACK_LONG_CLICK:
                if(rgs.getVisibility()!=View.GONE) {
                    if (theFragment == 0 || theFragment == 1) {
                        if(!data.isEmpty()){
                            Intent intent = new Intent();
                            //设置传递方向
                            intent.setClass(MainActivity.this, TalkActivity.class);
                            intent.putExtra("username", (String) data.get(theInfoId).get("title"));
                            this.startActivity(intent);
                        }else{
                            StartRead(getResources().getString(R.string.noNewMessage), Config.ACK_NONE);
                        }
                    } else if (theFragment == 2) {
                        onShareFragmentInteraction(theInfoId);
                    } else if (theFragment == 3) {
                        onBookFragmentInteraction(theInfoId);
                    }
                }
                break;
            case Config.ACK_LIST_READ:
                String content;
                Log.i("lanlan",String.valueOf(theInfoId));
                switch(theFragment){
                    case 0:
                        if(!data.isEmpty()) {
                            //如果data里的这条消息发送人不是用户，则是新消息
                            if(!((String) data.get(theInfoId).get("title")).equals(Constant.USERNAME)){
                                content = (String) data.get(theInfoId).get("title")
                                        + getString(R.string.main_chats_tip)
                                        + (String) data.get(theInfoId).get("content");
                                StartRead(content,Config.ACK_NONE);
                            }else{
                                StartRead("你和朋友"+(String) data.get(theInfoId).get("title")+"的历史消息", Config.ACK_NONE);
                            }
                        }else{
                            StartRead(getResources().getString(R.string.noNewMessage), Config.ACK_NONE);
                        }
                        break;
                    case 1:
                        if(!data.isEmpty()) {
                            content = (String) data.get(theInfoId).get("title");
                            StartRead(content, Config.ACK_NONE);
                        }else{
                            StartRead(getResources().getString(R.string.noNewMessage), Config.ACK_NONE);
                        }
                        break;
                    case 2:
                        if(!data.isEmpty()) {
                            content = (String) data.get(theInfoId).get("title")
                                    + getString(R.string.main_share_tip)
                                    + (String) data.get(theInfoId).get("content");
                            StartRead(content, Config.ACK_NONE);
                        }else{
                            StartRead(getResources().getString(R.string.noNewMessage), Config.ACK_NONE);
                        }
                        break;
                    case 3:
                        if(!data.isEmpty()) {
                            Log.i("lanlan", "fragment 3: " + String.valueOf(theInfoId));
                            content = getString(R.string.main_news_title)
                                    + (String) data.get(theInfoId).get("title")
                                    + getString(R.string.main_news_time)
                                    + (String) data.get(theInfoId).get("date")
                                    + getString(R.string.main_news_content)
                                    + (String) data.get(theInfoId).get("content");
                            StartRead(content, Config.ACK_NONE);
                        }else{
                            StartRead(getResources().getString(R.string.noNewMessage), Config.ACK_NONE);
                        }
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
                    }else if(theFragment == 2){
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, SendMomentActivity.class);
                        startActivity(intent);
                    }else if(theFragment == 3) {
                        con.downloadnews();
                    }
                }
                break;
            case Config.ACK_CON_SUCCESS:
                StartRead(getResources().getString(R.string.main_welcome),Config.ACK_NONE);//欢迎
                break;
            default:
                break;
        }
    }
   // private boolean checkfirst = true;
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
                    if(!data.isEmpty()) {
                        adapter = new SimpleAdapter(getApplicationContext(), data, R.layout.list_item,
                                new String[]{"title", "img", "date", "content"},
                                new int[]{R.id.nameView, R.id.photoView, R.id.dateView, R.id.contentView});
                    }
                    break;
                case 1:
                    theFragment = 1;
                    data = list2;
                    if(!data.isEmpty()) {
                        adapter = new SimpleAdapter(getApplicationContext(), data, R.layout.list_item,
                                new String[]{"title", "img", "date", "content"},
                                new int[]{R.id.nameView, R.id.photoView, R.id.dateView, R.id.contentView});
                    }
                    break;
                case 2:
                    theFragment = 2;
                    data = list3;
                    if(!data.isEmpty()) {
                        adapter = new SimpleAdapter(getApplicationContext(), data, R.layout.list_item,
                                new String[]{"title", "img", "date", "content"},
                                new int[]{R.id.nameView, R.id.photoView, R.id.dateView, R.id.contentView});
                    }
                    break;
                case 3:
                    theFragment = 3;
                    data = list4;
                    theInfoId=-1;
                    System.out.println("data:"+data.toString());
                  if(!data.isEmpty()) {
                       adapter = new SimpleAdapter(getApplicationContext(), data, R.layout.newlist_item,
                               new String[]{"title", "date"},
                               new int[]{R.id.nameView1, R.id.dateView1});
                    }
                    break;
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

        //Fragment
        fragments.add(new MainFragment());
        fragments.add(new ContactFragment());
        fragments.add(new ShareFragment());
        fragments.add(new NewsFragment());
        con.getnewmessage(Constant.USERNAME);
        con.downloadFriend(Constant.USERNAME);
        con.downloadMoments(Constant.USERNAME);
        con.downloadnews();

        rgs = (RadioGroup) findViewById(R.id.tabs_rg);
        rb[0] = (RadioButton)findViewById(R.id.tab_rb_a);
        rb[1] = (RadioButton)findViewById(R.id.tab_rb_b);
        rb[2] = (RadioButton)findViewById(R.id.tab_rb_c);
        rb[3] = (RadioButton)findViewById(R.id.tab_rb_d);//TODO LL
        rb[0].getLocationInWindow(position[0]);
        rb[1].getLocationInWindow(position[1]);
        rb[2].getLocationInWindow(position[2]);
        rb[3].getLocationInWindow(position[3]);

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
        theFragment = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private int flag_Resume=0;//用来标识第几次调用onResume
    @Override
    protected void onResume() {
        StartRead(getResources().getString(R.string.welcome_back),Config.ACK_NONE);
        if(!Constant.setBlind){
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
        if(flag_Resume!=0)//TODO FRAGEMENT
        {
            switch (theFragment){
                case 0:
                    con.getnewmessage(Constant.USERNAME);
                    break;
                case 1:
                    con.downloadFriend(Constant.USERNAME);
                    break;
                case 2:
                    con.downloadMoments(Constant.USERNAME);
                    break;
            }
        }

        System.out.println("back更新");
        flag_Resume++;
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
                    }else if(theFragment==2) {
                        Intent intent = new Intent();
                        intent.setClass(queue.getLast(),SendMomentActivity.class);
                        startActivity(intent);
                    }else if(theFragment==3) {
                      con.downloadnews();
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
    /**
     * 首页
     * 已测（贺明慧）
     */
    private List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
    private void getData() {
        //InfoList 是从服务器传来的全部消息
        List<Info> InfoList = db.getAllHistory();
        list1.clear();
        for (int i = 0; i < InfoList.size(); i++) {
            String sendUser = InfoList.get(i).getSendUser();
            String receiver = InfoList.get(i).getReceiver();
            String time = InfoList.get(i).getTime();
            String detail = InfoList.get(i).getDetail();
            //只显示最新的那条消息
            Map<String, Object> map = new HashMap<String, Object>();
            if(list1.isEmpty()) {
                if (sendUser.equals(Constant.USERNAME)) {
                    map.put("title", receiver);
                } else {
                    map.put("title", sendUser);
                }
                map.put("img", R.mipmap.pic1);
                map.put("date", time);
                map.put("content", detail);//最新的一条消息
                list1.add(map);
            }else {
                int count = 0;
                for (int j = 0; j < list1.size(); j++) {
                    if (!sendUser.equals(list1.get(j).get("title")) && !receiver.equals(list1.get(j).get("title"))) {
                        count++;
                    }
                }//for 循环结束 判断是否已经存在list1中
                //如果相等，说明可以加进list1中
                if (count == list1.size()) {
                    if (sendUser.equals(Constant.USERNAME)) {
                        map.put("title", receiver);
                    } else {
                        map.put("title", sendUser);
                    }
                    map.put("img", R.mipmap.pic1);
                    map.put("date", time);
                    map.put("content", detail);//最新的一条消息
                    list1.add(map);
                }
            }//判断count是否等于list1大小的if结束
            System.out.println("list1的大小" + list1.size());
        }
    }
//contact
    private List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
    private void getData2() {
        System.out.println("getData2获取朋友列表");
        List<String > list_Friends= db.getAllFriend();
        list2.clear();
        for(int i=0;i<list_Friends.size();i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map = new HashMap<String, Object>();
            map.put("title",list_Friends.get(i));
            map.put("img", R.mipmap.pic6);
            list2.add(map);
        }
    }
//share
    private List<Map<String, Object>> list3 = new ArrayList<Map<String, Object>>();
    private void getData3() {
        System.out.println("getData3获取本地朋友圈列表");
        List<Moments> list_Moments= db.getMoments();
        list3.clear();
        for(int i=0;i<list_Moments.size();i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map = new HashMap<String, Object>();
            map.put("title",list_Moments.get(i).getSendUser());
            map.put("img", R.mipmap.pic1);
            map.put("date",list_Moments.get(i).getTime());
            map.put("content",list_Moments.get(i).getContent());
            int count = 0;
            for(int j=0;j<list3.size();j++){
                if(!map.equals(list3.get(j))){
                    count++;
                }
            }
            if(count==list3.size()){
            list3.add(map);
            }
        }
    }

    private List<Map<String, Object>> list4 = new ArrayList<Map<String, Object>>();
    private void getData4(List<News> newsList) {
        list4.clear();
        System.out.println("mainActivity新闻列表");
        if(newsList.size()>10){
            for(int i=0;i<10;i++){
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("date",newsList.get(i).getTime());
                map.put("title",newsList.get(i).getTitle().substring(8,20)+"...");
                map.put("content", newsList.get(i).getContent());
                list4.add(map);
            }
        }else{
            for(int i=0;i<newsList.size();i++){
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("date",newsList.get(i).getTime());
                map.put("title",newsList.get(i).getTitle());
                map.put("content",newsList.get(i).getContent());
                list4.add(map);
            }
        }
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
        getFragmentManager().beginTransaction().replace(R.id.tab_content,DetailFragment.newInstance((String)list3.get(position).get("content"))).addToBackStack(null).commit();
    }

    @Override
    public void onBookFragmentInteraction(int position) {
        Message msg = Message.obtain();
        if(position==-1)
            position=0;
        getFragmentManager().beginTransaction().replace(R.id.tab_content,DetailFragment.newInstance((String)list4.get(position).get("content"))).addToBackStack(null).commit();
    }

    public static Conmmunication getCon(){
        return con;
    }
}
