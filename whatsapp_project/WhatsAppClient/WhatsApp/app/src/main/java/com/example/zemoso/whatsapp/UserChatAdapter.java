package com.example.zemoso.whatsapp;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by zemoso on 10/8/16.
 */

public class UserChatAdapter extends BaseAdapter {
    Context context=null;
    ArrayList<String> data;
    LayoutInflater layoutInflater=null;
    ArrayList<String> TAG;
    ArrayList<Date> timeArray;
    public UserChatAdapter(Context context,ArrayList<String> data,ArrayList<String> TAG,ArrayList<Date> time){
        this.context=context;
        this.data=data;

        this.TAG=TAG;
        this.timeArray=time;
    }
    @Override
    public int getCount() {
        return data.size();
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

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.message_container);
        LinearLayout linearLayout1= (LinearLayout) view.findViewById(R.id.message_container_wrapper);
        TextView textView= (TextView) view.findViewById(R.id.message_wrapper);
        TextView time=(TextView)view.findViewById(R.id.message_time_wrapper);
        textView.setText(data.get(i).toString());
        time.setText(DateUtils.getRelativeTimeSpanString(timeArray.get(i).getTime(),System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS));
        if(TAG.get(i).equals("self")){
            linearLayout.setGravity(Gravity.RIGHT);
            linearLayout1.setBackgroundResource(R.drawable.rounder_textview);
        }
        else{
            linearLayout.setGravity(Gravity.LEFT);
            linearLayout1.setBackgroundResource(R.drawable.round_text_view_sender);

        }
        return view;
    }
}