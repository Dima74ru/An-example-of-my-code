package com.karbox.carspeed;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class MyTimer extends AppCompatActivity {
    final Handler handler = new Handler();

    long seconds = 0;
    int delay = 0;

    MyTimer(int del)
    {
        delay = del;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                seconds++;
                handler.postDelayed(this, delay);
            }
        },0);  //the time is in miliseconds
    }

    public long getSeconds()
    {
        return seconds;
    }


}
