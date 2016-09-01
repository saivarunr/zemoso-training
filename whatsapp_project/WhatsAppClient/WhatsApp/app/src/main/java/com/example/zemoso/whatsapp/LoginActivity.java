package com.example.zemoso.whatsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
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
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.jar.Manifest;

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

        SharedPreferences sharedPreferences=getSharedPreferences("zemoso_whatsapp",MODE_PRIVATE);
        String userToken=sharedPreferences.getString("token",null);
        Intent fromRegisterIntent=getIntent();
        String usernameD=fromRegisterIntent.getStringExtra("username");
        String passwordD=fromRegisterIntent.getStringExtra("password");
        if(userToken!=null){
            this.startActivity(new Intent(LoginActivity.this,Home.class));
            finish();
        }

        setContentView(R.layout.activity_login);
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        button=(Button)findViewById(R.id.button);
        if(usernameD!=null){
            username.setText(usernameD);
            password.setText(passwordD);
        }
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
                NetworkInfo networkInfo=ServerDetails.getConnectedState(getApplicationContext());
                if(networkInfo==null){
                    Toast.makeText(getApplicationContext(),"Device not connected to network",Toast.LENGTH_SHORT).show();
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            File file=android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            try{
                File newDir=new File(file.getAbsolutePath()+"/ZeMoSoWP");
                newDir.mkdirs();
                newDir.setExecutable(true);
            }
            catch (Exception e){
                Log.w("dirCreatExcept",e.toString());
            }
            Intent intent=new Intent(LoginActivity.this,Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }
        else{
            SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("zemoso_whatsapp",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.clear();
            editor.commit();
        }
        return;
    }

    class LoginValidator extends AsyncTask<Map<String,String>,Void,Integer>{
        Context context;
        View view;
        public LoginValidator(Context context,View view){
            this.context=context;
            this.view=view;
        }
//TODO: not registered users snackbar!

        @Override
        protected Integer doInBackground(Map<String, String>... maps) {
            HttpURLConnection httpURLConnection=null;
            OutputStreamWriter outputStreamWriter=null;
            Map<String,String> map[]=maps;
            String token=null;
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

                    token=jsonObject1.get("token").toString();
                    SharedPreferences sharedPreferences=context.getSharedPreferences("zemoso_whatsapp",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("token",token);
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
                if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Toast.makeText(getApplicationContext(), "This app need permissions to write files sent by your friends, please enable permissions from settings!", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }

                if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M) {
                    Intent intent = new Intent(LoginActivity.this, Home.class);
                    context.startActivity(intent);
                    finish();
                }
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
