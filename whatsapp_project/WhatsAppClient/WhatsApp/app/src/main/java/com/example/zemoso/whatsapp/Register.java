package com.example.zemoso.whatsapp;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import ClientRes.ServerDetails;

public class Register extends AppCompatActivity {
    EditText username,password,confirmPassword,users_name;
    Button button;
    ProgressBar progressBar;
    String getUsername(){
        return username.getText().toString();
    }
    String getPassword(){
        return password.getText().toString();
    }
    String getConfirmPassword(){
        return confirmPassword.getText().toString();
    }
    String getUsersName(){return users_name.getText().toString();}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        username=(EditText)findViewById(R.id.registrationUsername);
        password=(EditText)findViewById(R.id.registrationPassword);
        confirmPassword=(EditText)findViewById(R.id.registrationConfirmPassword);
        users_name= (EditText) findViewById(R.id.users_name);
        button=(Button)findViewById(R.id.button2);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                String username=getUsername();
                String password=getPassword();
                String confirmPassword=getConfirmPassword();
                String users_name_1=getUsersName();
                if(validateUserCredentials(username,password,confirmPassword,users_name_1,view)){
                        progressBar.setVisibility(View.VISIBLE);
                        Map<String,String> map=new HashMap<String, String>();
                        map.put("username",username);
                        map.put("password",password);
                        map.put("name",users_name_1);
                        NetworkInfo networkInfo=ServerDetails.getConnectedState(getApplicationContext());
                        if(networkInfo==null){
                            Toast.makeText(getApplicationContext(),"Cannot register, device not connected to internet",Toast.LENGTH_SHORT).show();
                        }
                        new UserRegistration(view.getContext(),view).execute(map);
                }

                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private boolean validateUserCredentials(String username, String password, String confirmPassword,String name,View view) {
        if(name.isEmpty()){
            Toast.makeText(view.getContext(),"Enter your name!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(username.contains(" ")){
            Toast.makeText(view.getContext(),"Username cannot contain spaces",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(username.length()<6){
            Toast.makeText(view.getContext(),"Username should be atleast of length 6",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.length()<6){
            Toast.makeText(view.getContext(),"Password should be atleast of length 6",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.contains(" ")){
            Toast.makeText(view.getContext(),"Password cannot contain spaces",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(confirmPassword.isEmpty()){
            Toast.makeText(view.getContext(),"Confirm your password!!!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!password.equals(confirmPassword)){
            Toast.makeText(view.getContext(),"Passwords do not match, oops!",Toast.LENGTH_SHORT).show();
            return false;
        }
    return true;
    }
    class UserRegistration extends AsyncTask<Map<String,String>,Void,Integer>{
        View view;
        Context context;
        String username=null;
        String password=null;
        public UserRegistration(Context context,View view){
            this.context=context;
            this.view=view;
        }
        @Override
        protected Integer doInBackground(Map<String, String>... maps) {
            int status=0;

            HttpURLConnection httpURLConnection=null;
            String serverAddress= ServerDetails.getServerAddress();
            try{
                URL url=new URL(serverAddress+"/newUser");
                httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type","application/json");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();
                username=maps[0].get("username");
                password=maps[0].get("password");
                JSONObject jsonObject=new JSONObject(maps[0]);
                OutputStreamWriter outputStreamWriter=new OutputStreamWriter(httpURLConnection.getOutputStream());
                outputStreamWriter.write(jsonObject.toJSONString());
                outputStreamWriter.flush();
                status=httpURLConnection.getResponseCode();
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
            progressBar.setVisibility(View.INVISIBLE);
            if(status==200){
                Toast.makeText(context,"Yay, you've successfully registered",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Register.this,LoginActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("password",password);
                context.startActivity(intent);
            }
            else if(status==400){
                Toast.makeText(context,"Looks like username is already taken!",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context,"The server is going crazy...",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
