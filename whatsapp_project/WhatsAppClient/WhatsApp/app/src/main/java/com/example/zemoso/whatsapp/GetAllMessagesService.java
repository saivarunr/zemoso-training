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
import java.text.SimpleDateFormat;

import ClientRes.DatabaseHelper;
import ClientRes.ServerDetails;

/**
 * Created by zemoso on 12/8/16.
 */
public class GetAllMessagesService extends Service {
    DatabaseHelper databaseHelper;
    ThisClassThread thisClassThread=null;
    public static final String BroadcastReceiver="MessageGetterHost";
    String username=null;
    String token=null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e("created","yes");
        thisClassThread=new ThisClassThread();
        SharedPreferences sharedPreferences = getSharedPreferences("zemoso_whatsapp",MODE_PRIVATE);
        username=sharedPreferences.getString("username","");
        token=sharedPreferences.getString("token","");
        databaseHelper=new DatabaseHelper(getApplicationContext(),username);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(thisClassThread.getState()== Thread.State.NEW)
            thisClassThread.start();
       return super.onStartCommand(intent, flags, startId);
    }
    class ThisClassThread extends Thread{
        int i=0;
        @Override
        public void run() {
            super.run();
            while (true){
                try {
                    new MessageGetterHttpService().execute();
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class MessageGetterHttpService extends AsyncTask<String,Void,Boolean> {
        Boolean completionFlag=false;
        HttpURLConnection httpURLConnection=null;
        JSONArray jsonArray=null;


        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                String serverAddress= ServerDetails.getServerAddress();
                String newUrl=serverAddress+"/getAllMessages";
                URL url=new URL(newUrl);
                httpURLConnection= (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.addRequestProperty("Authorization",token);
                httpURLConnection.addRequestProperty("Content-Type","application/json");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                if(httpURLConnection.getResponseCode()==200){
                    InputStreamReader inputStreamReader=new InputStreamReader(httpURLConnection.getInputStream());
                    JSONParser jsonParser=new JSONParser();
                    jsonArray= (JSONArray) jsonParser.parse(inputStreamReader);

                    inputStreamReader.close();

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
            Intent intent=new Intent();
            intent.setAction(BroadcastReceiver);
            if(aBoolean){
                try {
                    int arraySize=jsonArray.size();
                    Log.e("array size",""+arraySize);
                    for(int i=0;i<arraySize;i++){
                        org.json.simple.JSONObject jsonObject= (org.json.simple.JSONObject) jsonArray.get(i);
                        String message=jsonObject.get("message").toString();
                        String senderName=jsonObject.get("senderName").toString();
                        databaseHelper.addMessage(senderName,username,message,jsonObject.get("timestamp").toString());
                    }
                    intent.putExtra("data","saved");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("GAMS",e.toString());
                }

            }
            else{

                Log.e("json Array","No data");
                intent.putExtra("data","nosaved");
            }
            sendBroadcast(intent);
        }
    }

}