package com.example.zemoso.whatsapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by zemoso on 11/8/16.
 */
public class MostRecentUserAdapter extends BaseAdapter {
    Context context=null;
    Activity activity=null;
    ArrayList<String> usernames=null;
    ArrayList<String> recentMessages=null;
    ArrayList<Date> times=null;
    LayoutInflater layoutInflater=null;
    public MostRecentUserAdapter(Activity activity, ArrayList<String> usernames, ArrayList<String> recentMessages, ArrayList<Date> times){
        this.activity=activity;
        this.context=activity.getApplicationContext();
        this.usernames=usernames;
        this.recentMessages=recentMessages;
        this.times=times;
    }

    @Override
    public int getCount() {
        return usernames.size();
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
        TextView textView=null;
        TextView textView1=null;
        TextView textView2=null;
        if(view==null){
            layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=layoutInflater.inflate(R.layout.most_recent_user,viewGroup,false);

        }

        textView= (TextView) view.findViewById(R.id.usernameContainer);
        textView1= (TextView) view.findViewById(R.id.usernameMessage);
        textView2= (TextView) view.findViewById(R.id.usernameMessageTime);
        textView.setText(usernames.get(i));
        final String username=usernames.get(i);
        String text_setter=(recentMessages.get(i).length()>30)?recentMessages.get(i).substring(0,30)+"...":recentMessages.get(i);
        textView1.setText(text_setter);
        textView2.setText(DateUtils.getRelativeTimeSpanString(times.get(i).getTime(),System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS));
        textView.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(activity,GenericUserChat.class);
                intent.putExtra("USERNAME",username);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        return view;
    }
}
