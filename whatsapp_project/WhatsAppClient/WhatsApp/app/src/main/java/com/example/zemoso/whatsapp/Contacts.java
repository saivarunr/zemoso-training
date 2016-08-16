package com.example.zemoso.whatsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import ClientRes.DatabaseHelper;
import ClientRes.Users;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Contacts.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Contacts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Contacts extends ListFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button button= null;
    Button clear_group=null;
    IntentFilter intentFilter;
    RelativeLayout relativeLayout=null;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    NewContactsGetter newContactsGetter=null;
    private OnFragmentInteractionListener mListener;

    public Contacts() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Contacts newInstance(String param1, String param2) {
        Contacts fragment = new Contacts();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        newContactsGetter=new NewContactsGetter();
        intentFilter=new IntentFilter();
        intentFilter.addAction(ContactGetterService.ContactGetterSerivceString);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        android.support.v7.widget.Toolbar toolbar= (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        LayoutInflater layoutInflater=getActivity().getLayoutInflater();
        layoutInflater.inflate(R.layout.group_create_action_wrapper,toolbar);
        button= (Button) getActivity().findViewById(R.id.create_group);
        relativeLayout= (RelativeLayout) getActivity().findViewById(R.id.contact_action_container);
        clear_group= (Button) getActivity().findViewById(R.id.clear_group);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> stringList= (ArrayList<String>) getStringList();
                Intent intent=new Intent(getContext(),CreateGroup.class);
                intent.putStringArrayListExtra("stringList",stringList);
                getContext().startActivity(intent);
            }
        });
        clear_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stringList.clear();
                relativeLayout.setVisibility(View.GONE);
                setLongItemClickEnabled(false);
                List<Fragment> fragments=getActivity().getSupportFragmentManager().getFragments();
                getActivity().getSupportFragmentManager().beginTransaction().detach(fragments.get(1)).attach(fragments.get(1)).commit();
            }
        });
        View view=inflater.inflate(R.layout.fragment_contacts, container, false);
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("zemoso_whatsapp",Context.MODE_PRIVATE);
        username=sharedPreferences.getString("username","");
        databaseHelper=DatabaseHelper.getInstance(getContext());
        usersList=new ArrayList<>();
        contactsAdapter=new ContactsAdapter(getContext(),usersList);
        setListAdapter(contactsAdapter);
        loadData();
        return view;
    }
    void loadData(){
        usersList.clear();
        usersList.addAll(databaseHelper.getAllUsersExcept(username));
        contactsAdapter.notifyDataSetChanged();
    }
    static List<Users> usersList=null;
    static DatabaseHelper databaseHelper=null;
    static ContactsAdapter contactsAdapter=null;
    static String username=null;

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    static ListView listView;

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(newContactsGetter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(newContactsGetter,intentFilter);
         listView=getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ContactsAdapter contactsAdapter1= (ContactsAdapter) adapterView.getAdapter();
                Users users=contactsAdapter1.getItem(i);
                if(isLongItemClickEnabled()){
                    addItemToList(users.getUsername(), view);
                }
                else{
                    Intent intent=new Intent(getContext(),GenericUserChat.class);
                    intent.putExtra("USERNAME",users.getUsername());
                    intent.putExtra("NAME",users.getName());
                    intent.putExtra("is_group",users.getIs_group());
                    getContext().startActivity(intent);
                }

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                setLongItemClickEnabled(true);
                ContactsAdapter contactsAdapter1= (ContactsAdapter) adapterView.getAdapter();
                Users users=contactsAdapter1.getItem(i);
                addItemToList(users.getUsername(),view);
                return true;
            }
        });
    }
    public boolean isLongItemClickEnabled() {
        return isLongItemClickEnabled;
    }

    public void setLongItemClickEnabled(boolean longItemClickEnabled) {
        isLongItemClickEnabled = longItemClickEnabled;
    }

    private void addItemToList(String username, View view) {

        ImageView imageView= (ImageView) view.findViewById(R.id.user_checked);
        if(stringList.contains(username)){
            stringList.remove(username);

            imageView.setVisibility(View.GONE);
        }

        else{

            imageView.setVisibility(View.VISIBLE);
            stringList.add(username);

        }
        if(stringList.size()==0){
            setLongItemClickEnabled(false);
            relativeLayout.setVisibility(View.GONE);
        }
        else{
            relativeLayout.setVisibility(View.VISIBLE);
        }

        Log.e("list",stringList.toString());
    }

    public static boolean isLongItemClickEnabled=false;
    static List<String> stringList=new ArrayList<>();
    List<String> getStringList(){
        return stringList;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        stringList.clear();
        isLongItemClickEnabled=false;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public static class NewContactsGetter extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Contacts contacts=new Contacts();
            contacts.loadData();
        }
    }
}
