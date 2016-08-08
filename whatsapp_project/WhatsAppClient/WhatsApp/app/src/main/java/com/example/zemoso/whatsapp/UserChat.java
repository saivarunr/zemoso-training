package com.example.zemoso.whatsapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Text;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ClientRes.DatabaseHelper;
import ClientRes.ServerDetails;
import ClientRes.UserMessages;

public class UserChat extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    EditText editText;
    String target;
    IntentFilter intentFilter;
    String username;


    class UserSpecificMessageGetter extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
                String jsons=intent.getStringExtra("jsons");
            JSONParser jsonParser=new JSONParser();
            try {
                JSONArray jsonArray= (JSONArray) jsonParser.parse(jsons);
                int arraySize=jsonArray.size();
                LinearLayout linearLayout= (LinearLayout) findViewById(R.id.textViewContainer);
                for(int i=0;i<arraySize;i++){

                    TextView textView = new TextView(UserChat.this);
                    textView.setGravity(Gravity.LEFT);
                    org.json.simple.JSONObject jsonObject= (org.json.simple.JSONObject) jsonArray.get(i);
                    String message=jsonObject.get("message").toString();
                    textView.setText(message);
                    linearLayout.addView(textView);
                    databaseHelper.addMessage(target,username,message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("zemoso_whatsapp",MODE_PRIVATE);

        databaseHelper=new DatabaseHelper(this,username);
        List<UserMessages> userMessagesList=databaseHelper.getMessage(username,target);
        int size=userMessagesList.size();
        Log.e("size",""+size);
        LinearLayout linearLayout= (LinearLayout) findViewById(R.id.textViewContainer);
        for(int i=0;i<size;i++){
            TextView textView=new TextView(UserChat.this);
            textView.setText(userMessagesList.get(i).getMessage());
            if(userMessagesList.get(i).getSender().equals(username))
                textView.setGravity(Gravity.RIGHT);
            else
                textView.setGravity(Gravity.LEFT);

            linearLayout.addView(textView);
        }
    }
    LinearLayout linearLayout;
    UserSpecificMessageGetter userSpecificMessageGetter=null;
    LinearLayout getLinearLayout(){
        return linearLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        ScrollView scrollView= (ScrollView) findViewById(R.id.scrollView);
        Button button=(Button)findViewById(R.id.sendButton);
        editText=(EditText)findViewById(R.id.userChatText);
        SharedPreferences sharedPreferences = getSharedPreferences("zemoso_whatsapp",MODE_PRIVATE);
        final String token=sharedPreferences.getString("token","");
        username=sharedPreferences.getString("username","");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent=getIntent();
        target=intent.getStringExtra("USERNAME");
        setTitle(target);

        userSpecificMessageGetter=new UserSpecificMessageGetter();
        intentFilter=new IntentFilter();
        intentFilter.addAction(MessageGetterService.BroadcastFilter);

        /*
        This is for background service which fetches messages of a particular User.
         */
        Intent intent1=new Intent(UserChat.this,MessageGetterService.class);
        intent1.putExtra("target",target);
        startService(intent1);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadData();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userData=getEditText();
                emptyEditText();
                if(!userData.isEmpty()){
                    databaseHelper.addMessage(username,target,userData);
                    new DataSender().execute(token,target,userData);
                    LinearLayout linearLayout= (LinearLayout) findViewById(R.id.textViewContainer);
                    TextView textView=new TextView(UserChat.this);
                    textView.setText(userData);
                    textView.setGravity(Gravity.RIGHT);
                    linearLayout.addView(textView);
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

    private void emptyEditText() {
        editText.setText("");
    }

    public String getEditText() {
        return editText.getText().toString().trim();
    }

    class DataSender extends AsyncTask<String,Void,Boolean>{
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

}
