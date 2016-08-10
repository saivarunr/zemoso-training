package com.example.zemoso.whatsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
        new UsernameGetter(this,username).execute(token);

    }
    class UsernameGetter extends AsyncTask<String,Void,Boolean> {
        Context context;
        DatabaseHelper databaseHelper=null;
        org.json.simple.JSONArray jsonArray=null;
        UsernameGetter(Context context, String username){
            databaseHelper=new DatabaseHelper(context,username);
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
                    jsonArray= (org.json.simple.JSONArray) jsonParser.parse(inputStreamReader);
                    inputStreamReader.close();
                    int size=jsonArray.size();
                    Log.e("jsonArray",jsonArray.toJSONString());
                    for(int i=0;i<size;i++){
                        String username=jsonArray.get(i).toString();
                        if(!databaseHelper.containsUser(username))
                            databaseHelper.addUser(username);
                    }

                    completionFlag=true;
                }


            }
            catch (Exception e){
                Log.e("Exception",e.toString());
            }
            finally {
                databaseHelper.close();
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

