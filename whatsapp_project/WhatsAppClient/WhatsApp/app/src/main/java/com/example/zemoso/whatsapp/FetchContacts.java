package com.example.zemoso.whatsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ClientRes.DatabaseHelper;
import ClientRes.ServerDetails;

public class FetchContacts extends AppCompatActivity {
    TextView textView;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_contacts);
        SharedPreferences sharedPreferences=getSharedPreferences("zemoso_whatsapp",MODE_PRIVATE);
        String username=sharedPreferences.getString("username","");
        String token=sharedPreferences.getString("token","");
         progressBar= (ProgressBar) findViewById(R.id.progressBar2);
         textView= (TextView) findViewById(R.id.textView2);
        DatabaseHelper databaseHelper=DatabaseHelper.getInstance(getApplicationContext());
        new UsernameGetter(this,username).execute(token);

    }
    class UsernameGetter extends AsyncTask<String,Void,Boolean> {
        Context context;
        DatabaseHelper databaseHelper=null;
        org.json.simple.JSONArray jsonArray=null;
        UsernameGetter(Context context, String username){
            databaseHelper= DatabaseHelper.getInstance(context);
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
            textView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            Intent intent=new Intent(FetchContacts.this,Home.class);
            context.startActivity(intent);
            finish();
        }
    }
}

