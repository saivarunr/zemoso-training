package com.example.zemoso.whatsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ClientRes.DatabaseHelper;
import ClientRes.MostRecentUserWrapper;
import ClientRes.Users;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MostRecentUser.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MostRecentUser#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MostRecentUser extends ListFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public AllMessagesReceiver allMessagesReceiver;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    IntentFilter intentFilter;
   static  ArrayList<MostRecentUserWrapper> usersList=null;
    static  MostRecentUserAdapter mostRecentUserAdapter=null;
    static  String username=null;
    public MostRecentUser() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MostRecentUser.
     */
    // TODO: Rename and change types and number of parameters
    public static MostRecentUser newInstance(String param1, String param2) {
        MostRecentUser fragment = new MostRecentUser();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allMessagesReceiver=new AllMessagesReceiver();
        intentFilter=new IntentFilter();
        intentFilter.addAction(GetAllMessagesService.BroadcastReceiver);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        usersList=new ArrayList<>();
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("zemoso_whatsapp",getContext().MODE_PRIVATE);
        username=sharedPreferences.getString("username","");
        DatabaseHelper databaseHelper= DatabaseHelper.getInstance(getContext());
        usersList=databaseHelper.getMostRecent();

        mostRecentUserAdapter=new MostRecentUserAdapter(getActivity(),usersList);
        setListAdapter(mostRecentUserAdapter);
        return inflater.inflate(R.layout.fragment_most_recent_user, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(allMessagesReceiver,intentFilter);
        ListView listView=getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MostRecentUserAdapter mostRecentUserAdapter = (MostRecentUserAdapter) adapterView.getAdapter();
                String username = mostRecentUserAdapter.getItem(i);
                    Intent intent = new Intent(getContext(), GenericUserChat.class);
                    intent.putExtra("USERNAME", username);
                    DatabaseHelper databaseHelper=new DatabaseHelper(getContext());
                    String name=databaseHelper.getNameByUsername(username);
                    intent.putExtra("NAME",name);
                    getContext().startActivity(intent);


            }
        });

    }



    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(allMessagesReceiver);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

   public static class AllMessagesReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data=intent.getStringExtra("data");
            if(data.equals("saved")){
                usersList.clear();
                DatabaseHelper databaseHelper=DatabaseHelper.getInstance(context);
                usersList.addAll(databaseHelper.getMostRecent());
                mostRecentUserAdapter.notifyDataSetChanged();
            }
        }
    }
}
