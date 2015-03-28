package com.moirai.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Message;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;

import com.moirai.client.Config;
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
    /**
     * Called when the activity is first created.
     */
    private RadioGroup rgs;
    public List<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    public void processMessage(Message message) {
        switch(message.what){
            case Config.REQUEST_DOWNLOAD_INFO:
                list = (List<Info>) message.obj;
                //db.saveDownloadInfo(list);
                break;
            case Config.ACK_CON_SUCCESS:
                StartRead("漂漂的岚岚姐",Config.ACK_NONE);
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
       // con.downloadInfo("lanlan");
        //Fragment
        fragments.add(new MainFragment());
        fragments.add(new ContactFragment());
        fragments.add(new ShareFragment());

        rgs = (RadioGroup) findViewById(R.id.tabs_rg);

        adapter = new SimpleAdapter(this,getData(),R.layout.list_item,
                new String[]{"title","img"},
                new int[]{R.id.nameView,R.id.photoView});

        FragmentTabAdapter tabAdapter = new FragmentTabAdapter(this, fragments, R.id.tab_content, rgs);
        tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener(){
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
                switch(index){
                    case 0:
                        // create ArrayAdapter and use it to bind tags to the ListView
                        adapter = new SimpleAdapter(getApplicationContext(),getData(),R.layout.list_item,
                            new String[]{"title","img"},
                            new int[]{R.id.nameView,R.id.photoView});
                        break;
                    case 1:
                        adapter = new SimpleAdapter(getApplicationContext(),getData(),R.layout.list_item,
                                new String[]{"title","img"},
                                new int[]{R.id.nameView,R.id.photoView});
                        break;
                    case 2:
                        adapter = new SimpleAdapter(getApplicationContext(),getData(),R.layout.list_item,
                                new String[]{"title","img"},
                                new int[]{R.id.nameView,R.id.photoView});
                        break;
                }
                System.out.println("Extra---- " + index + " checked!!! ");
            }
        });
    }
    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if(fm.getBackStackEntryCount()>0){
            fm.popBackStack();
        }else{
            super.onBackPressed();
        }

    }
    // ADDED to set up the ListFragment
    public SimpleAdapter getAdapter(){return adapter;}

    @Override
    protected void onStart() {
        Intent intent_voice_service = new Intent(this, VoiceService.class);
        startService(intent_voice_service);
        bindService(intent_voice_service, connection_voice, BIND_AUTO_CREATE);
        super.onStart();
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "G1");
        map.put("img", R.drawable.tababoutus);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "G2");
        map.put("img", R.drawable.tabconfigicon);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "G3");
        map.put("img", R.drawable.tablatestalert);
        list.add(map);

        return list;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("您是否要退出？")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    //con.exitGame();
                                   // con.clear();
                                    finish();
                                    int siz = BaseActivity.queue.size();
                                    for (int i = 0; i < siz; i++) {
                                        if (BaseActivity.queue.get(i) != null) {
                                            System.out
                                                    .println((Activity) BaseActivity.queue
                                                            .get(i) + "退出程序");
                                            ((Activity) BaseActivity.queue
                                                    .get(i)).finish();
                                        }
                                    }
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {


                                }
                            });
            dialog.create().show();

            return true;
        }

        else
            return false;

    }


    @Override
    public void onMainFragmentInteraction(int position) {

    }

    @Override
    public void onContactFragmentInteraction(int position) {

    }

    @Override
    public void onShareFragmentInteraction(int position) {

    }
}
