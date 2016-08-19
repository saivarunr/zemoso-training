package com.example.zemoso.whatsapp;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ClientRes.ServerDetails;

/**
 * Created by zemoso on 13/8/16.
 */
public class ReadTheseMessages extends Service {
    ThisClassThread thisClassThread=null;
    String target=null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        thisClassThread=new ThisClassThread();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        target=intent.getStringExtra("targetUsername");
        if(thisClassThread.getState()== Thread.State.NEW)
            thisClassThread.start();
        return super.onStartCommand(intent, flags, startId);
    }
boolean flag=true;
    @Override
    public void onDestroy() {
        super.onDestroy();
        flag=false;
    }

    class ThisClassThread extends Thread{
        @Override
        public void run() {
            super.run();
            while (flag){try {
                new MessageReadStateUpadter().execute(target);
                Thread.sleep(4000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }}

        }

    }

    class MessageReadStateUpadter extends AsyncTask<String,Void,Boolean>{
        Boolean completionFlag=false;
        HttpURLConnection httpURLConnection=null;
        JSONArray jsonArray=null;
        SharedPreferences sharedPreferences = getSharedPreferences("zemoso_whatsapp",MODE_PRIVATE);
        String token=sharedPreferences.getString("token","");
        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                String serverAddress= ServerDetails.getServerAddress();
                String newUrl=serverAddress+"/readMessages?target="+strings[0];
                URL url=new URL(newUrl);
                httpURLConnection= (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.addRequestProperty("Authorization",token);
                httpURLConnection.addRequestProperty("Content-Type","application/json");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                if(httpURLConnection.getResponseCode()==200){
                    completionFlag=true;
                }


            }
            catch (Exception e){
                Log.e("ex",e.toString());
            }
            finally {
                httpURLConnection.disconnect();
            }

            return  completionFlag;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);


        }
    }

}
