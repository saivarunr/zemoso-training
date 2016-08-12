package com.example.zemoso.whatsapp;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ClientRes.DatabaseHelper;
import ClientRes.ServerDetails;

/**
 * Created by zemoso on 5/8/16.
 */
public class MessageGetterService extends Service {
    Thread thread=null;
    Intent intent=null;
    String serverAddress= ServerDetails.getServerAddress();
    String route=null;
    public final static  String BroadcastFilter="MessageGetter";
    String targetUsername=null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e("onCreate","Created");
        thread=new HttpMessageGetter();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        targetUsername=intent.getStringExtra("target");
        if(thread.getState()==Thread.State.NEW)
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }
    class HttpMessageGetter extends Thread{
        @Override
        public void run() {
            super.run();
            try{
                while (true){
                    Log.e("From Service","Service in loop");

                    intent=new Intent();
                    intent.setAction(BroadcastFilter);
                   // new MessageGetterHttpService().execute(targetUsername);
                    Thread.sleep(5000);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }


}
