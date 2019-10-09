package com.example.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    boolean paused = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "I am toast", Toast.LENGTH_LONG).show();
        paused = false;
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                doSth();
            }
        });
        th.start();
    }


    private void doSth(){
        for (int i = 0; i < 1000; i++) {
            if (!paused) {
                Log.e("service",i+"run");
                try {
                    Thread.sleep(2*1000);
                } catch (Exception e) {
                    Log.e("OUTPUT_ERROR", e.getMessage());
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this,"Service stoped",Toast.LENGTH_LONG).show();
        paused = true;
    }
}
