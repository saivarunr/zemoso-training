package com.example.zemoso.whatsapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ClientRes.Users;

/**
 * Created by zemoso on 12/8/16.
 */
public class ContactsAdapter extends BaseAdapter {
    LayoutInflater layoutInflater=null;
    List<Users> usernames=null;
    Context context;
    public ContactsAdapter(Context context,List<Users> usernames){
        this.usernames=usernames;
        this.context=context;
    }
    @Override
    public int getCount() {
        return usernames.size();
    }

    @Override
    public Users getItem(int i) {
        return usernames.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=layoutInflater.inflate(R.layout.contact_resource,viewGroup,false);

        }
        TextView textView= (TextView) view.findViewById(R.id.contacts_username_wrapper);
        textView.setText(usernames.get(i).getName());
        return view;
    }
}
