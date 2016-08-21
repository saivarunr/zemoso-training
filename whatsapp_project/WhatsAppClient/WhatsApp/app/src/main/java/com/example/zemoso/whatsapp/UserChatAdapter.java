package com.example.zemoso.whatsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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
    Boolean isUserGroup=false;
    public UserChatAdapter(Context context,ArrayList<Integer> messageIds,Boolean isUserGroup){
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
        UserMessages userMessages1=null;
        if(i>0){
            userMessages1=databaseHelper.getWholeMessageById(messageIds.get(i-1));
        }
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.message_container);
        LinearLayout linearLayout1= (LinearLayout) view.findViewById(R.id.message_container_wrapper);
        TextView textView= (TextView) view.findViewById(R.id.message_wrapper);
        TextView time=(TextView)view.findViewById(R.id.message_time_wrapper);
        RelativeLayout relativeLayout= (RelativeLayout) view.findViewById(R.id.group_message_sender_name_wrapper);
        TextView groupMessageSenderName= (TextView) view.findViewById(R.id.group_message_sender_name_container);
        ImageView imageView= (ImageView) view.findViewById(R.id.message_read_ticks);
        String senderUsername=userMessages.getSender();
        if(isUserGroup&!senderUsername.equals(username)){
            relativeLayout.setVisibility(View.VISIBLE);
            groupMessageSenderName.setText(senderUsername);
        }
        else{
            relativeLayout.setVisibility(View.GONE);
        }
        textView.setText(userMessages.getMessage());
        try {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm a");
            String x=simpleDateFormat.format(userMessages.getTimestamp());
            time.setText(x);
            //time.setText(DateUtils.getRelativeTimeSpanString(userMessages.getTimestamp().getTime(),System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(userMessages.getSender().equals(username)){
            if(i==0){
                linearLayout1.setBackgroundResource(R.drawable.send1);
            }
            else if(i>0&&!userMessages.getSender().equals(userMessages1.getSender())){
                linearLayout1.setBackgroundResource(R.drawable.send1);
            }
            else{
                linearLayout1.setBackgroundResource(R.drawable.rounder_textview);
            }
            imageView.setVisibility(View.VISIBLE);
            linearLayout.setGravity(Gravity.RIGHT);
            if(!isUserGroup)
            {
            if(userMessages.getIsRead()==2)
                imageView.setImageResource(R.drawable.message_seen_resource);
            else if(userMessages.getIsRead()==1)
                imageView.setImageResource(R.drawable.message_received_resource);

            }
        }
        else{
            if(i==0|!userMessages.getSender().equals(userMessages1.getSender())){
                linearLayout1.setBackgroundResource(R.drawable.rec11);
            }
            else if(i>0&&!userMessages.getSender().equals(userMessages1.getSender())){
                linearLayout1.setBackgroundResource(R.drawable.rec11);
            }
            else{
                linearLayout1.setBackgroundResource(R.drawable.rounder_textview);
            }
            imageView.setVisibility(View.INVISIBLE);
            linearLayout.setGravity(Gravity.LEFT);

        }
        return view;
    }
}
