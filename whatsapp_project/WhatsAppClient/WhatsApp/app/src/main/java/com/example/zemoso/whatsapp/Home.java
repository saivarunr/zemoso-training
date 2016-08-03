package com.example.zemoso.whatsapp;

import android.app.TabActivity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;

public class Home extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TabHost tabHost=getTabHost();
        tabHost.addTab(tabHost.newTabSpec("chats").setIndicator("Chats").setContent(new Intent(this,Chats.class)));
        tabHost.addTab(tabHost.newTabSpec("contacts").setIndicator("Contacts").setContent(new Intent(this,Contacts.class)));
        tabHost.setCurrentTab(0);
    }
}
