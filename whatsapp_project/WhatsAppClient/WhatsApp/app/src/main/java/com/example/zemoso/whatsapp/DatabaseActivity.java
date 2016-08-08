package com.example.zemoso.whatsapp;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.util.List;

import ClientRes.DatabaseHelper;
import ClientRes.Users;

public class DatabaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        SharedPreferences sharedPreferences=getSharedPreferences("zemoso_whatsapp",MODE_PRIVATE);
        String username=sharedPreferences.getString("username","");
        DatabaseHelper databaseHelper=new DatabaseHelper(this,username);
        databaseHelper.addUser("varun");
        databaseHelper.addUser("vishal");
        List<Users> usersList=databaseHelper.getAllUsers();
        for(Users users:usersList){
            Log.e("Users",users.getUsername());
        }
//        SQLiteDatabase sqLiteDatabase=SQLiteDatabase.openOrCreateDatabase("zemoso_whatsapp",null);
//        sqLiteDatabase.execSQL("create table if not exists users(username varchar(20),password varchar(20);");

    }
}
