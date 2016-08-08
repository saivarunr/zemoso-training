package com.example.zemoso.whatsapp;

import android.app.AlarmManager;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by zemoso on 5/8/16.
 */
public class TaskAlarm extends BroadcastReceiver {
    Context context;
    Intent intent;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Do something","");
    }

}
