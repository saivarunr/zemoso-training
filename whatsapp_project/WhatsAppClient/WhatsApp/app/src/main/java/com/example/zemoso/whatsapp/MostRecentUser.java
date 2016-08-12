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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
    ArrayList<String> usernames=null;
    ArrayList<String> lastMessages=null;
    ArrayList<Date> times=null;
    MostRecentUserAdapter mostRecentUserAdapter=null;
    String username=null;
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
        // Inflate the layout for this fragment
        usernames=new ArrayList<>();
        lastMessages=new ArrayList<>();
        times=new ArrayList<>();

        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("zemoso_whatsapp",getContext().MODE_PRIVATE);
        username=sharedPreferences.getString("username","");
        DatabaseHelper databaseHelper=new DatabaseHelper(getContext(),username);
        List<MostRecentUserWrapper> usersList=databaseHelper.getMostRecent();
        usernames.clear();
        lastMessages.clear();
        times.clear();
        for(int i=0;i<usersList.size();i++){
            usernames.add(usersList.get(i).getUsername());
            lastMessages.add(usersList.get(i).getMessage());
            times.add(usersList.get(i).getDate());
        }
        mostRecentUserAdapter=new MostRecentUserAdapter(getActivity(),usernames,lastMessages,times);
        setListAdapter(mostRecentUserAdapter);
        AllMessagesReceiver allMessagesReceiver = new AllMessagesReceiver();

        return inflater.inflate(R.layout.fragment_most_recent_user, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(allMessagesReceiver,intentFilter);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class AllMessagesReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data=intent.getStringExtra("data");
            if(data.equals("saved")){
                usernames.clear();
                times.clear();
                lastMessages.clear();
                DatabaseHelper databaseHelper=new DatabaseHelper(getContext(),username);
                List<MostRecentUserWrapper> usersList=databaseHelper.getMostRecent();
                for(int i=0;i<usersList.size();i++){
                    usernames.add(usersList.get(i).getUsername());
                    lastMessages.add(usersList.get(i).getMessage());
                    times.add(usersList.get(i).getDate());
                }
                mostRecentUserAdapter.notifyDataSetChanged();
            }
        }
    }
}
