package com.moirai.view;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Message;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.moirai.client.R;

import java.io.IOException;

public class CameraActivity extends BaseActivity {

    private Camera camera;
    private Camera.CameraInfo cameraInfo;
    private int cameraCount = 0;
    private Button takePhoto;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaRecorder recorder;
    private static int flag=0;
    boolean isPreview;
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
         //设置窗口的显示方式
           Window window = getWindow();
           //requestWindowFeature(Window.FEATURE_NO_TITLE);
           window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
           window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            setContentView(R.layout.activity_camera);

            takePhoto = (Button)findViewById(R.id.takePhoto);
            recorder = new MediaRecorder();
            takePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(flag==0){
                        //startRecord();
                        flag =1;
                        takePhoto.setText("Start");
                    }else{
                       /* recorder.stop();
                        recorder.reset();
                        recorder.release();
                        recorder = null;*/
                        flag = 0;
                        takePhoto.setText("Stop");
                    }
                }
            });
        surfaceView=(SurfaceView)findViewById(R.id.videoView);
        surfaceHolder=surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceCallback());

    }
    /**
     * 开始录像
     */
    public void startRecord(){
       // initCamera();
    }

    /**
     * 打开前置摄像头
     */
    private Camera openFrontCamera(){
        cameraInfo = new Camera.CameraInfo();
        cameraCount = camera.getNumberOfCameras();
        Camera cam = null;
        for(int camId=0;camId < cameraCount;camId++){
            Camera.getCameraInfo(camId,cameraInfo);
            if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                try{
                     cam = Camera.open(camId);
                }catch(RuntimeException e){
                    e.printStackTrace();
                }
            }
        }
        return cam;
    }
    private class SurfaceCallback implements SurfaceHolder.Callback{

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            camera = openFrontCamera();
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);//得到窗口管理器
            Display display  = wm.getDefaultDisplay();//得到当前屏幕
            Camera.Parameters parameters = camera.getParameters();//得到摄像头的参数
            camera.setParameters(parameters);
            camera.setDisplayOrientation(90);
            //parameters.setPreviewFrameRate(3);//设置每秒3帧
            try {
                camera.setPreviewDisplay(surfaceHolder);//通过SurfaceView显示取景画面
                camera.startPreview();//开始预览
                isPreview = true;//设置是否预览参数为真
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
              camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}
