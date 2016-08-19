package com.example.zemoso.whatsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ClientRes.DatabaseHelper;
import ClientRes.ServerDetails;
import ClientRes.UserMessages;

public class GenericUserChat extends AppCompatActivity {
    public static class GenericUserDataBroadcastReceiver extends BroadcastReceiver {

        GenericUserChat genericUserChat=new GenericUserChat();
        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                String data=intent.getStringExtra("data");
                if(data.equals("saved")){
                    genericUserChat.loadData();
                }
            }
            catch (Exception e){
                Log.e("ex",e.toString());
            }

        }
    }
    static ArrayList<Integer> messageIds=null;
    static UserChatAdapter userChatAdapter=null;
    ListView listView=null;
    Button button=null;
    EditText editText=null;
    static String targetUsername=null;
    static String name=null;
    static String token=null;
    static DatabaseHelper databaseHelper=null;
    static String username=null;
    IntentFilter intentFilter=null;
    Intent readTheseMessages=null;
    static boolean isUserGroup;
    public GenericUserDataBroadcastReceiver genericUserDataBroadcastReceiver;
    public static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_user_chat);

        GenericUserChat.context=getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        targetUsername=getIntent().getStringExtra("USERNAME");
        name=getIntent().getStringExtra("NAME");
        setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        messageIds=new ArrayList<>();
        listView= (ListView) findViewById(R.id.list_view);
        databaseHelper= DatabaseHelper.getInstance(this);
        isUserGroup=databaseHelper.isGroup(targetUsername);
        userChatAdapter=new UserChatAdapter(this,messageIds,isUserGroup);
        listView.setAdapter(userChatAdapter);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        button= (Button) findViewById(R.id.button3);
        editText=(EditText)findViewById(R.id.editText);
        SharedPreferences sharedPreferences = getSharedPreferences("zemoso_whatsapp",MODE_PRIVATE);
        token=sharedPreferences.getString("token","");
        username=sharedPreferences.getString("username","");

        genericUserDataBroadcastReceiver=new GenericUserDataBroadcastReceiver();
        intentFilter=new IntentFilter();
        intentFilter.addAction(GetAllMessagesService.BroadcastReceiver);

        /*
            Load previous chat history from DB into the adapter
        */

        loadData();
        listView.setSelection(listView.getCount()-1);
        /*
            Now for every input by user update listview
         */
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data=getEditTextData();
                if(!data.isEmpty()){
                    publishData(data);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(genericUserDataBroadcastReceiver,intentFilter);
        readTheseMessages=new Intent(getApplicationContext(),ReadTheseMessages.class);
        readTheseMessages.putExtra("targetUsername",targetUsername);
        intent=new Intent(GenericUserChat.this,ReadTheseMessages.class);
        intent.putExtra("targetUsername",targetUsername);
        startService(intent);
    }
     Intent intent=null;
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(genericUserDataBroadcastReceiver);
        stopService(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void publishData(String data) {


        try {
            Integer b=new DataSender().execute(token,targetUsername,data).get();
            if(b>-1){
                updateView(data,b);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    private void updateView(String data,Integer id) {
        messageIds.add(id);
        userChatAdapter.notifyDataSetChanged();
        databaseHelper.addMessage(id,username, targetUsername, data);
    }

    static  public void loadData() {
        //databaseHelper= DatabaseHelper.getInstance(context);
        List<Integer> userMessagesList=null;
        if(isUserGroup){
            userMessagesList=databaseHelper.getIdOfMessagesOfGroup(targetUsername);
        }
        else{
            userMessagesList=databaseHelper.getIdOfMessages(username,targetUsername);
        }
        int size=userMessagesList.size();
        messageIds.clear();;
        for(int i=0;i<size;i++){
            messageIds.add(userMessagesList.get(i));
        }
        userChatAdapter.notifyDataSetChanged();

    }


    public String getEditTextData() {
        String x=editText.getText().toString().trim();
        editText.setText("");
        return x;
    }
}


class DataSender extends AsyncTask<String,Void,Integer> {
    HttpURLConnection httpURLConnection=null;
    int status=0;
    @Override
    protected Integer doInBackground(String... strings) {
        Integer completionFlag=-1;
        String serverAddress= ServerDetails.getServerAddress();
        try {
            URL url=new URL(serverAddress+"/postMessage");
            httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Authorization",strings[0]);
            httpURLConnection.setRequestProperty("Content-Type","application/json");
            httpURLConnection.connect();
            org.json.simple.JSONObject jsonObject=new org.json.simple.JSONObject();
            jsonObject.put("target",strings[1]);
            jsonObject.put("message",strings[2]);
            OutputStreamWriter outputStreamWriter=new OutputStreamWriter(httpURLConnection.getOutputStream());
            outputStreamWriter.write(jsonObject.toJSONString());
            outputStreamWriter.flush();
            InputStreamReader inputStreamReader=new InputStreamReader(httpURLConnection.getInputStream());
            JSONParser jsonParser=new JSONParser();
            JSONObject jsonObject1= (JSONObject) jsonParser.parse(inputStreamReader);
            status=httpURLConnection.getResponseCode();
            Log.e("json",jsonObject.toJSONString());
            if(status==200){
                completionFlag=Integer.parseInt(jsonObject1.get("message").toString());
            }
        }
        catch (Exception e){
            Log.e("UserChat.java",e.toString());
        }
        finally {
            httpURLConnection.disconnect();
        }
        return completionFlag;
    }

    @Override
    protected void onPostExecute(Integer aBoolean) {
        super.onPostExecute(aBoolean);

    }


}