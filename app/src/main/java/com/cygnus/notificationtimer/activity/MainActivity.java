package com.cygnus.notificationtimer.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.cygnus.notificationtimer.BuildConfig;
import com.cygnus.notificationtimer.notification.NotificationService;
import com.cygnus.notificationtimer.R;
import com.cygnus.notificationtimer.constant.Constants;


public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findId();
        startBackgroundService();
    }

    /**
     * find view id
     * set selected flavor to textview
     */
    private void findId() {
        TextView txt_flavorname = findViewById(R.id.txt_flavorname);
        txt_flavorname.setText(BuildConfig.FLAVOR);
    }

    /**
     * Start background service for timer
     */
    public void startBackgroundService() {
        Intent serviceIntent = new Intent(MainActivity.this, NotificationService.class);
        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(serviceIntent);
    }
}
