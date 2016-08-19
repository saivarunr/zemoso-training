package com.example.zemoso.whatsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.parser.*;
import org.json.simple.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import ClientRes.DatabaseHelper;
import ClientRes.ServerDetails;

public class LoginActivity extends AppCompatActivity {
    EditText username,password;
    Button button;
    TextView registerLabel;
    String getUsername(){
        return username.getText().toString();
    }
    String getPassword(){
        return password.getText().toString();
    }
    Map<String,String> map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy threadPolicy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);
        SharedPreferences sharedPreferences=getSharedPreferences("zemoso_whatsapp",MODE_PRIVATE);
        String userToken=sharedPreferences.getString("token",null);
        if(userToken!=null){
            this.startActivity(new Intent(LoginActivity.this,Home.class));
            finish();
        }
        setContentView(R.layout.activity_login);
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        button=(Button)findViewById(R.id.button);
        registerLabel=(TextView)findViewById(R.id.textView);
        /*
        Trigger login action from here
         */
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=getUsername();
                String password=getPassword();
                if(username.isEmpty()){
                    Toast.makeText(view.getContext(),"Username cannot be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.isEmpty()){
                    Toast.makeText(view.getContext(),"Password cannot be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                map=new HashMap<String, String>();
                map.put("username",username);
                map.put("password",password);
                new LoginValidator(view.getContext(),view).execute(map);

            }
        });

        registerLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,Register.class);
                view.getContext().startActivity(intent);
            }
        });
    }
    class LoginValidator extends AsyncTask<Map<String,String>,Void,Integer>{
        Context context;
        View view;
        public LoginValidator(Context context,View view){
            this.context=context;
            this.view=view;
        }


        @Override
        protected Integer doInBackground(Map<String, String>... maps) {
            HttpURLConnection httpURLConnection=null;
            OutputStreamWriter outputStreamWriter=null;
            Map<String,String> map[]=maps;
            Integer status=0;
            try{
                String serverAddress=ServerDetails.getServerAddress();
                URL url=new URL(serverAddress+"/login");
                httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type","application/json");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();
                JSONObject jsonObject=new JSONObject(map[0]);
                outputStreamWriter=new OutputStreamWriter(httpURLConnection.getOutputStream());
                outputStreamWriter.write(jsonObject.toJSONString());
                outputStreamWriter.flush();
                status=httpURLConnection.getResponseCode();
                if(status==200){
                    InputStream inputStream=new BufferedInputStream(httpURLConnection.getInputStream());
                    InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
                    JSONParser jsonParser=new JSONParser();
                    JSONObject jsonObject1= (JSONObject) jsonParser.parse(inputStreamReader);
                    SharedPreferences sharedPreferences=context.getSharedPreferences("zemoso_whatsapp",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("token",jsonObject1.get("token").toString());
                    editor.putString("username",map[0].get("username").toString());
                    editor.putString("password",map[0].get("password").toString());
                    editor.commit();

                    inputStreamReader.close();
                }

                outputStreamWriter.close();

            }
            catch (Exception e){

            }
            finally {
                httpURLConnection.disconnect();
            }

            return status;
        }

        @Override
        protected void onPostExecute(Integer status) {
            super.onPostExecute(status);
            if(status==200) {

                Intent intent=new Intent(LoginActivity.this,Home.class);
                context.startActivity(intent);
                finish();
            }
            else if(status==400){
                Snackbar.make(view,"Invalid credentials",Snackbar.LENGTH_SHORT).show();
            }
            else{
                Snackbar.make(view,"Oops, looks like server's down :( ",Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
