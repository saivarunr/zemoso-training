package com.example.zemoso.whatsapp;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;

import ClientRes.DatabaseHelper;
import ClientRes.ServerDetails;
import ClientRes.Users;

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
        databaseHelper= DatabaseHelper.getInstance(getApplicationContext());
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
        JSONObject jsonObject=null;


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
                    jsonObject= (JSONObject) jsonParser.parse(inputStreamReader);
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
                    JSONArray jsonArrayC=(JSONArray) jsonObject.get("contacts");
                    int contactsSize=jsonArrayC.size();
                    List<Users> dbUsers=databaseHelper.getAllUsers();
                    intent.putExtra("contacts","no");
                    int dbUserSize=dbUsers.size();

                    if(dbUserSize<contactsSize){
                        for(int i=0;i<contactsSize;i++){
                            JSONObject jsonObject= (JSONObject) jsonArrayC.get(i);
                            String username=jsonObject.get("username").toString();
                            String name=jsonObject.get("name").toString();
                            Integer isGroup=Integer.parseInt(jsonObject.get("isGroup").toString());
                            Log.e("contains",""+databaseHelper.containsUser(username));
                            if(!databaseHelper.containsUser(username)){
                                databaseHelper.addUser(username,name,isGroup);
                                intent.putExtra("contacts","new");
                            }
                        }
                    }
                    JSONArray jsonArray= (JSONArray) jsonObject.get("messages");
                    int arraySize=jsonArray.size();
                    intent.putExtra("data","");
                    for(int i=0;i<arraySize;i++){
                        JSONObject jsonObject= (JSONObject) jsonArray.get(i);
                        String message=jsonObject.get("message").toString();
                        String senderName=jsonObject.get("senderName").toString();
                        Integer server_id=Integer.parseInt(jsonObject.get("id").toString());
                        databaseHelper.addMessage(server_id,senderName,username,message,jsonObject.get("timestamp").toString());
                        intent.putExtra("data","saved");
                    }
                    JSONArray jsonArray1= (JSONArray) jsonObject.get("requested");
                    int arraySize1=jsonArray1.size();
                    for(int i=0;i<arraySize1;i++){
                        JSONObject jsonObject= (JSONObject) jsonArray1.get(i);
                        int id=Integer.parseInt(jsonObject.get("id").toString());
                        int requested=Integer.parseInt(jsonObject.get("requested").toString());
                        databaseHelper.updateMessageasRead(id,requested);
                        intent.putExtra("data","saved");
                    }
                    JSONArray jsonArray2= (JSONArray) jsonObject.get("groupMessage");
                    int arraySize2=jsonArray2.size();
                    for(int i=0;i<arraySize2;i++){
                        JSONObject jsonObject= (JSONObject) jsonArray2.get(i);
                        int id=Integer.parseInt(jsonObject.get("id").toString());
                        String message=jsonObject.get("message").toString();
                        String timestamp=jsonObject.get("timestamp").toString();
                        String senderName=jsonObject.get("senderName").toString();
                        String recieverName=jsonObject.get("recieverName").toString();
                        if(databaseHelper.isGroup(recieverName)){
                            databaseHelper.addMessage(id,senderName,recieverName,message,timestamp);
                            intent.putExtra("data","saved");
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("GAMS",e.toString());
                }

            }
            else{

                Log.e("json Array","No data");
                intent.putExtra("contacts","no");
                intent.putExtra("data","nosaved");
            }
            sendBroadcast(intent);
        }
    }

}
