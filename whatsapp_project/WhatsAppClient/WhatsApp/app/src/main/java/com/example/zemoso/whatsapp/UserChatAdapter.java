package com.example.zemoso.whatsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import ClientRes.DatabaseHelper;
import ClientRes.UserMessages;

/**
 * Created by zemoso on 10/8/16.
 */

public class UserChatAdapter extends BaseAdapter {
    Context context=null;

    LayoutInflater layoutInflater=null;
    ArrayList<Integer> messageIds=null;
    DatabaseHelper databaseHelper=null;
    SharedPreferences sharedPreferences=null;
    String username=null;
    Integer isUserGroup=0;
    public UserChatAdapter(Context context,ArrayList<Integer> messageIds,Integer isUserGroup){
        this.context=context;
        this.messageIds=messageIds;
        this.isUserGroup=isUserGroup;
        sharedPreferences=context.getSharedPreferences("zemoso_whatsapp",Context.MODE_PRIVATE);
        username=sharedPreferences.getString("username","");
        databaseHelper= DatabaseHelper.getInstance(context);

    }
    @Override
    public int getCount() {
        return messageIds.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=layoutInflater.inflate(R.layout.list_view_resource,viewGroup,false);
        }
        UserMessages userMessages=databaseHelper.getWholeMessageById(messageIds.get(i));
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.message_container);
        LinearLayout linearLayout1= (LinearLayout) view.findViewById(R.id.message_container_wrapper);
        TextView textView= (TextView) view.findViewById(R.id.message_wrapper);
        TextView time=(TextView)view.findViewById(R.id.message_time_wrapper);
        ImageView imageView= (ImageView) view.findViewById(R.id.message_read_ticks);

        textView.setText(userMessages.getMessage());
        try {
            time.setText(DateUtils.getRelativeTimeSpanString(userMessages.getTimestamp().getTime(),System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(userMessages.getSender().equals(username)){
            imageView.setVisibility(View.VISIBLE);
            linearLayout.setGravity(Gravity.RIGHT);
            linearLayout1.setBackgroundResource(R.drawable.rounder_textview);
            if(userMessages.getIsRead()==2)
            imageView.setImageResource(R.drawable.message_seen_resource);
            else if(userMessages.getIsRead()==1)
                imageView.setImageResource(R.drawable.message_received_resource);
            else
                imageView.setImageResource(R.drawable.ic_done_black_24dp);
        }
        else{
            imageView.setVisibility(View.INVISIBLE);
            linearLayout.setGravity(Gravity.LEFT);
            linearLayout1.setBackgroundResource(R.drawable.round_text_view_sender);
        }
        return view;
    }
}
