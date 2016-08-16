package com.example.zemoso.whatsapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ClientRes.DatabaseHelper;
import ClientRes.ServerDetails;

/**
 * Created by zemoso on 15/8/16.
 */
public class ContactGetterService extends Service {
    String username;
    String token;
    final public static String ContactGetterSerivceString="ContactGetterSerivceString";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sharedPreferences=getSharedPreferences("zemoso_whatsapp",MODE_PRIVATE);
        username=sharedPreferences.getString("username","");
        token=sharedPreferences.getString("token","");
        ThisClassThread thisClassThread=new ThisClassThread();
        thisClassThread.start();
        return super.onStartCommand(intent, flags, startId);
    }
    class ThisClassThread extends Thread{
        @Override
        public void run() {
            super.run();
            while (true){
                try {
                    new UsernameGetter(getApplicationContext(),username).execute(token);
                    Thread.sleep(4000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    class UsernameGetter extends AsyncTask<String,Void,Boolean> {
        Context context;
        DatabaseHelper databaseHelper=null;
        org.json.simple.JSONArray jsonArray=null;
        UsernameGetter(Context context, String username){
            databaseHelper=DatabaseHelper.getInstance(context);
            this.context=context;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            boolean completionFlag=false;
            String token=strings[0];
            HttpURLConnection httpURLConnection=null;

            try{
                String serverAddress= ServerDetails.getServerAddress();
                URL url=new URL(serverAddress+"/getUsers");
                httpURLConnection= (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Authorization",token);
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                if(httpURLConnection.getResponseCode()==200){
                    InputStreamReader inputStreamReader=new InputStreamReader(httpURLConnection.getInputStream());
                    JSONParser jsonParser=new JSONParser();
                    JSONArray jsonObject=(JSONArray) jsonParser.parse(inputStreamReader);
                    int length=jsonObject.size();
                    for(int i=0;i<length;i++){
                        JSONObject json=(JSONObject) jsonObject.get(i);
                        String name=json.get("name").toString();
                        String username=json.get("username").toString();
                        int isGroup=Integer.parseInt(json.get("isGroup").toString());
                        if(!databaseHelper.containsUser(username)) {
                            try {
                                databaseHelper.addUser(username, name, isGroup);
                            }
                            catch (Exception e){

                            }
                            completionFlag=true;
                        }
                    }
                }


            }
            catch (Exception e){
                Log.e("Exception",e.toString());
            }
            finally {

                httpURLConnection.disconnect();
            }
            return completionFlag;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                Intent intent=new Intent();
                intent.setAction(ContactGetterSerivceString);
                sendBroadcast(intent);
            }

        }
    }
}
