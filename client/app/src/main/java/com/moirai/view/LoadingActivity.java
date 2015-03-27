package com.moirai.view;

import android.app.Activity;
import android.os.Message;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.moirai.client.R;


public class LoadingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
    }
}