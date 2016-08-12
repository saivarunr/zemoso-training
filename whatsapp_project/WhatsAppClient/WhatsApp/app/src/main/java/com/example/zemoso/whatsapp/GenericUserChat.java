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
import org.json.simple.parser.JSONParser;

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
    ArrayList<String> strings=null;
    ArrayList<String> TAGS=null;
    ArrayList<Date> time=null;
    UserChatAdapter userChatAdapter=null;
    ListView listView=null;
    Button button=null;
    EditText editText=null;
    String targetUsername=null;
    String token=null;
    DatabaseHelper databaseHelper=null;
    String username=null;
    UserSpecificMessageGetter userSpecificMessageGetter=null;
    IntentFilter intentFilter=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_user_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
         targetUsername=getIntent().getStringExtra("USERNAME");
        setTitle(targetUsername);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TAGS=new ArrayList<>();
        strings=new ArrayList<>();
        time=new ArrayList<>();

        listView= (ListView) findViewById(R.id.list_view);

        userChatAdapter=new UserChatAdapter(this,strings,TAGS,time);
        listView.setAdapter(userChatAdapter);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        button= (Button) findViewById(R.id.button3);
        editText=(EditText)findViewById(R.id.editText);
        SharedPreferences sharedPreferences = getSharedPreferences("zemoso_whatsapp",MODE_PRIVATE);
        token=sharedPreferences.getString("token","");
        username=sharedPreferences.getString("username","");
        databaseHelper=new DatabaseHelper(this,username);
        loadData();
        userSpecificMessageGetter=new UserSpecificMessageGetter();
        intentFilter=new IntentFilter();
        intentFilter.addAction(MessageGetterService.BroadcastFilter);
        /*
        Start service
         */
        Intent intent=new Intent(GenericUserChat.this,MessageGetterService.class);
        intent.putExtra("target",targetUsername);
        startService(intent);

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




        registerReceiver(userSpecificMessageGetter,intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(userSpecificMessageGetter);
    }

    private void publishData(String data) {


        try {
            boolean b=new DataSender().execute(token,targetUsername,data).get();
            if(b){


                updateView(data);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    private void updateView(String data) {
        TAGS.add("self");
        time.add(new Date());
        strings.add(data);
        userChatAdapter.notifyDataSetChanged();
        databaseHelper.addMessage(username, targetUsername, data);
        databaseHelper.close();
    }

    private void loadData() {
        databaseHelper=new DatabaseHelper(this,username);
        List<UserMessages> userMessagesList=databaseHelper.getMessage(username,targetUsername);
        int size=userMessagesList.size();
        strings.clear();
        time.clear();
        TAGS.clear();
        for(int i=0;i<size;i++){
            strings.add(userMessagesList.get(i).getMessage());
            String tag=(userMessagesList.get(i).getSender().equals(username)?"self":"no");
            TAGS.add(tag);
            try {
                time.add(userMessagesList.get(i).getTimestamp());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        userChatAdapter.notifyDataSetChanged();
    }


    public String getEditTextData() {
        String x=editText.getText().toString().trim();
        editText.setText("");
        return x;
    }

    class UserSpecificMessageGetter extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String jsons=intent.getStringExtra("jsons");
            JSONParser jsonParser=new JSONParser();

            try {
                JSONArray jsonArray= (JSONArray) jsonParser.parse(jsons);
                int arraySize=jsonArray.size();
                Log.e("array size",""+arraySize);

                SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
                for(int i=0;i<arraySize;i++){
                    org.json.simple.JSONObject jsonObject= (org.json.simple.JSONObject) jsonArray.get(i);
                    String message=jsonObject.get("message").toString();

                    time.add(dateFormat.parse(jsonObject.get("timestamp").toString()));
                    strings.add(message);
                    TAGS.add("no");
                    userChatAdapter.notifyDataSetChanged();
                    databaseHelper.addMessage(targetUsername,username,message,jsonObject.get("timestamp").toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}


class DataSender extends AsyncTask<String,Void,Boolean> {
    HttpURLConnection httpURLConnection=null;
    int status=0;
    @Override
    protected Boolean doInBackground(String... strings) {
        boolean completionFlag=false;
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
            status=httpURLConnection.getResponseCode();
            Log.e("json",jsonObject.toJSONString());
            if(status==200){
                completionFlag=true;
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
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

    }


}