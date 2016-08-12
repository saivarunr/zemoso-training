package com.example.zemoso.whatsapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class UserChatHistory extends AppCompatActivity {
    EditText editText;
    ArrayList<String> strings=null;
    ArrayList<String> TAGS=null;
    ArrayList<Date> time=null;
    String getEdittext(){
        return editText.getText().toString();
    }
    UserChatAdapter userChatAdapter=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat_history);
        Button button= (Button) findViewById(R.id.button3);

        editText = (EditText) findViewById(R.id.editText);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=getEdittext();
                addItemsToArrayAdapter(text);
            }
        });
        ListView listView= (ListView) findViewById(R.id.list_view);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        strings=new ArrayList<String>();
        strings.add("one");
        strings.add("two");
        strings.add("three");
        TAGS=new ArrayList<String>();
        TAGS.add("no");
        TAGS.add("self");
        TAGS.add("no");
        time=new ArrayList<>();
        time.add(new Date());
        time.add(new Date());
        time.add(new Date());
        userChatAdapter=new UserChatAdapter(UserChatHistory.this,strings,TAGS,time);
        listView.setAdapter(userChatAdapter);
    }

    private void addItemsToArrayAdapter(String text) {
        time.add(new Date());
        TAGS.add("self");
        strings.add(text);
        userChatAdapter.notifyDataSetChanged();
    }
}
