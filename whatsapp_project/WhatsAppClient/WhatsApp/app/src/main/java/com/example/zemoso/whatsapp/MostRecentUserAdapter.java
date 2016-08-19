package com.example.zemoso.whatsapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import ClientRes.DatabaseHelper;
import ClientRes.MostRecentUserWrapper;

/**
 * Created by zemoso on 11/8/16.
 */
public class MostRecentUserAdapter extends BaseAdapter {
    private final ArrayList<MostRecentUserWrapper> mostRecentUserWrappers;
    Context context=null;
    Activity activity=null;
    LayoutInflater layoutInflater=null;
    DatabaseHelper databaseHelper=null;
    public MostRecentUserAdapter(Activity activity, ArrayList<MostRecentUserWrapper> mostRecentUserWrappers){
        this.activity=activity;
        this.context=activity.getApplicationContext();
        this.mostRecentUserWrappers=mostRecentUserWrappers;
        SharedPreferences sharedPreferences=activity.getSharedPreferences("zemoso_whatsapp",Context.MODE_PRIVATE);
        this.databaseHelper=DatabaseHelper.getInstance(activity.getApplicationContext());
    }

    @Override
    public int getCount() {
        return mostRecentUserWrappers.size();
    }

    @Override
    public String getItem(int i) {
        return mostRecentUserWrappers.get(i).getUsername();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView=null;
        TextView textView1=null;
        TextView textView2=null;
        TextView textView3=null;
        if(view==null){
            layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=layoutInflater.inflate(R.layout.most_recent_user,viewGroup,false);

        }

        textView= (TextView) view.findViewById(R.id.usernameContainer);
        textView1= (TextView) view.findViewById(R.id.usernameMessage);
        textView2= (TextView) view.findViewById(R.id.usernameMessageTime);
        textView3= (TextView) view.findViewById(R.id.unread_message_count);
        String musername=mostRecentUserWrappers.get(i).getUsername();
        String username=databaseHelper.getNameByUsername(musername);
        textView.setText(username);
        String mMessage=mostRecentUserWrappers.get(i).getMessage();
        String text_setter=(mMessage.length()>30)?mMessage.substring(0,30)+"...":mMessage;
        textView1.setText(text_setter);
        textView2.setText(DateUtils.getRelativeTimeSpanString(mostRecentUserWrappers.get(i).getDate().getTime(),System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS));
        int mCount=mostRecentUserWrappers.get(i).getUnreadCount();
        if(mCount>0){
            textView3.setVisibility(View.VISIBLE);
            textView3.setText(String.valueOf(mCount));
            textView2.setTextColor(Color.parseColor("#42E800"));
        }
        else{
            textView3.setVisibility(View.GONE);
            textView2.setTextColor(Color.BLACK);
        }
        return view;
    }
}
