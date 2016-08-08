package com.example.zemoso.whatsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zemoso.whatsapp.dummy.DummyContent;
import com.example.zemoso.whatsapp.dummy.DummyContent.DummyItem;

import java.util.List;

import ClientRes.DatabaseHelper;
import ClientRes.Users;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class UsersFragment2 extends Fragment {
SharedPreferences sharedPreferences;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UsersFragment2() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static UsersFragment2 newInstance(int columnCount) {
        UsersFragment2 fragment = new UsersFragment2();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }
    List<Users> usersList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPreferences=getActivity().getSharedPreferences("zemoso_whatsapp",Context.MODE_PRIVATE);
        String username=sharedPreferences.getString("username","");
        View view = inflater.inflate(R.layout.fragment_users_list2, container, false);
        DatabaseHelper databaseHelper = new DatabaseHelper(view.getContext(),username);
        usersList=databaseHelper.getMostRecent();
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyUsersRecyclerViewAdapter2(usersList, mListener));
        }
        RecyclerView recyclerView= (RecyclerView) view;
        recyclerView.addOnItemTouchListener(new UsersFragment.RecyclerTouchListener(getContext(), recyclerView, new UsersFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent=new Intent(getActivity(),UserChat.class);
                intent.putExtra("USERNAME",usersList.get(position).getUsername());
                view.getContext().startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        return view;
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
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Users item);
    }
}
