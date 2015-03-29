package com.moirai.view;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Message;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.games.Player;
import com.moirai.client.R;

import java.io.IOException;

public class GestureActivity extends BaseActivity
        implements View.OnTouchListener,SurfaceHolder.Callback{
    MediaPlayer player;
    SurfaceView surface;
    SurfaceHolder surfaceHolder;
    @Override
    public void processMessage(Message message) {
        // TODO Auto-generated method stub
        switch(message.what){

//            case Config.REQUEST_GATHISTORY:
//
//                break;
//            case Config.REQUEST_SAVEHISTORY:
//                break;
//            case Config.ACK_SERVICE:
//
//                break;

            default:
                break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture);

        surface=(SurfaceView)findViewById(R.id.gesture_surfaceView);
        surfaceHolder=surface.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setFixedSize(320, 220);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        RelativeLayout layout = (RelativeLayout)findViewById(R.id.gesture_layout);
        layout.setOnTouchListener(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setDisplay(surfaceHolder);
        player.reset();
        try {
            Uri uri = Uri.parse( "android.resource://" + getPackageName()
                    + "/" + R.raw.guesture_demo);
            player.setDataSource(this,uri);
            player.prepare();
            player.start();
            player.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(player.isPlaying()){
            player.stop();
        }
        player.release();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        System.out.printf("别摸我。。");
        Intent intent = new Intent();
        intent.setClass(GestureActivity.this, TalkActivity.class);
        //  startActivityForResult(intent, my_requestCode);
        startActivity(intent);
        GestureActivity.this.finish();
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode,event);

        return true;
    }
}
