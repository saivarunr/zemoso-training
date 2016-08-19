package com.example.zemoso.whatsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ClientRes.ServerDetails;

public class CreateGroup extends AppCompatActivity {
    EditText editText;
    String getEditText(){
        return editText.getText().toString();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("New Group");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        editText= (EditText) findViewById(R.id.new_group_name);

        ListView listView= (ListView) findViewById(R.id.new_group_list);
        Intent intent=getIntent();
        ArrayList<String> strings=intent.getStringArrayListExtra("stringList");
        ArrayAdapter<String> stringArrayAdapter=new ArrayAdapter<String>(getApplicationContext(),R.layout.new_group_list_resource,strings);
        listView.setAdapter(stringArrayAdapter);
        Map<Integer,String> map=new HashMap<>();
        int i=0;
        for(String s:strings){
            map.put(i++,s);
        }
        final JSONObject jsonObject=new JSONObject(map);
        SharedPreferences sharedPreferences=getSharedPreferences("zemoso_whatsapp", Context.MODE_PRIVATE);
        final String token=sharedPreferences.getString("token","");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editText=getEditText();
                if(!editText.isEmpty()){
                    JSONObject object=new JSONObject();
                    object.put("groupName",editText);
                    object.put("users",jsonObject);
                    new NewGroupCreator().execute(token,object.toJSONString());
                }
                else{
                    Toast.makeText(getApplicationContext(),"Provide a group subject",Toast.LENGTH_LONG).show();
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6BD44E")));

    }
    class NewGroupCreator extends AsyncTask<String,Void,Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            boolean completionFlag=false;
            HttpURLConnection httpURLConnection = null;


            try{
                String serverAddress= ServerDetails.getServerAddress();
                URL url=new URL(serverAddress+"/addMembersToGroup");
                httpURLConnection= (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Authorization",strings[0]);
                httpURLConnection.setRequestProperty("Content-Type","application/json");

                httpURLConnection.connect();
                OutputStreamWriter outputStreamWriter=new OutputStreamWriter(httpURLConnection.getOutputStream());
                outputStreamWriter.write(strings[1]);
                outputStreamWriter.flush();
                outputStreamWriter.close();
                if(httpURLConnection.getResponseCode()==200){
                    completionFlag=true;
                }

            }
            catch (Exception e){
                Log.e("CG",e.toString());
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
                Toast.makeText(getApplicationContext(),"Group Created",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(CreateGroup.this,Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        }
    }

}
