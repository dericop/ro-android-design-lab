package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.nfc.FormatException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Home.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends ListFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SwipeRefreshLayout mSwipeRefreshing;
    boolean isTheFirstLoad = true;
    DatabaseReference mDatabase = null;
    HomeAdapter mPostAdapter= null;
    Button btn_new_posts = null;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ListView mListView;
    LinkedList<Post> mPostList;


    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
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

        /*FloatingActionButton fab = (FloatingActionButton) getContext().findViewById(R.id.btn_addPost);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private void refreshPostList(){

        DatabaseReference mDatabaseTemp;
        if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
            mDatabaseTemp = mDatabase.child("user-posts");
        }else{
            mDatabaseTemp = mDatabase.child("user-posts-reflexive");
        }

        mDatabaseTemp.orderByKey().limitToLast(30).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final HashMap<String, Object> postMap = (HashMap)dataSnapshot.getValue();

                final String name = (String)postMap.get("name");
                final String frecuency =(String) postMap.get("frecuency");
                final String category = (String)postMap.get("category");
                final String image = (String)postMap.get("image");
                final String user = (String)postMap.get("user");
                final String id = (String)postMap.get("id");

                DatabaseReference mDatabaseTemp2;
                if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
                    mDatabaseTemp2 = mDatabase.child("users");
                }else{
                    mDatabaseTemp2 = mDatabase.child("users-reflexive");
                }

                Log.v("DATAPP", postMap.toString());

                long result = 0;
                if(postMap.get("result") != null){
                    result = (long)postMap.get("result");
                    Log.v("DB", result+":result");
                }

                long average = 0;
                if(postMap.get("average") != null){
                    try {
                        average = (long)postMap.get("average");
                    }catch (NumberFormatException ex){

                    }
                }

                Post post;
                if(postMap.get("duration") != null){
                    String duration = (String)postMap.get("duration");

                    //if(!tooSharedCopy[0].equals(""))
                        post = new Post(id, name, category, frecuency, image, duration, user, result, average, "", "");
                    /*else
                        post = new Post(id, name, category, frecuency, image, duration, user, result, average, userName, tooSharedCopy[0]);*/

                }else{

                    //if(!tooSharedCopy[0].equals(""))
                        post = new Post(id, name, category, frecuency, image, user, result, average, "", "");
                    /*else
                        post = new Post(id, name, category, frecuency, image, user, result, average, userName, tooSharedCopy[0]);*/
                }

                if(postMap.get("r_pi") != null && postMap.get("r_aa")!=null && postMap.get("r_gs")!=null && postMap.get("r_ch") != null){

                    post.setmPi(Double.parseDouble(postMap.get("r_pi")+""));
                    post.setmAa(Double.parseDouble(postMap.get("r_aa")+""));
                    post.setmGs(Double.parseDouble(postMap.get("r_gs")+""));
                    post.setmCh(Double.parseDouble(postMap.get("r_ch")+""));

                }
                Log.v("Data", post.toString());
                mPostList.addFirst(post);
                reloadData();

                mDatabaseTemp2.child(user).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, Object> map = (HashMap)dataSnapshot.getValue();
                        final String userName = (String)map.get("mUserName");
                        final String[] tooSharedCopy = {""};

                        Log.v("dataPP", name);

                        if(postMap.get("last_share") != null){
                            final String lastShare = (String)postMap.get("last_share");

                            DatabaseReference mDatabaseTemp2 = null;
                            if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
                                mDatabaseTemp2 = mDatabase.child("users");
                            }else{
                                mDatabaseTemp2 = mDatabase.child("users-reflexive");
                            }

                            mDatabaseTemp2.child(lastShare).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    HashMap<String, Object> tooSharedMap = (HashMap) dataSnapshot.getValue();
                                    final String tooSharedName = (String) tooSharedMap.get("mUserName");
                                    tooSharedCopy[0] = tooSharedName;
                                    for (int i=0; i<mPostList.size();i++) {
                                        if(mPostList.get(i).getmId().equals(id)){
                                            mPostList.get(i).setmTooShared(tooSharedName);
                                            Log.v("dataPP", "Si paso");
                                        }
                                    }
                                    reloadData();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> map = (HashMap)dataSnapshot.getValue();

                String name = (String)map.get("name");
                String frecuency =(String) map.get("frecuency");
                String category = (String)map.get("category");
                String image = (String)map.get("image");
                String user = (String)map.get("user");
                final String id = (String)map.get("id");

                long result = 0;
                if(map.get("result") != null){
                    result = (long)map.get("result");
                    Log.v("DB", result+":result");
                }
                final long resultData = result;

                long average = 0;
                if(map.get("average") != null){
                    try {
                        average = (long)map.get("average");
                    }catch (NumberFormatException ex){

                    }

                }

                final long averageData = average;

                String tooShare = "";
                if(map.get("last_share") != null){

                    DatabaseReference mDatabaseTemp2 = null;
                    if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
                        mDatabaseTemp2 = mDatabase.child("users");
                    }else{
                        mDatabaseTemp2 = mDatabase.child("users-reflexive");
                    }

                    tooShare = (String) map.get("last_share");
                    mDatabaseTemp2.child(tooShare).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String, Object> tooSharedMap = (HashMap) dataSnapshot.getValue();
                            String tooSharedName = (String) tooSharedMap.get("mUserName");

                            for (int i=0; i<mPostList.size(); i++){
                                if(mPostList.get(i).getmId().equals(id)){
                                    mPostList.get(i).setmResult(resultData);
                                    mPostList.get(i).setmAverage(averageData);
                                    mPostList.get(i).setmTooShared(tooSharedName);

                                    /*mPostList.get(i).setmId(id);
                                    mPostList.get(i).setmName(name);
                                    mPostList.get(i).setmCategory(category);
                                    mPostList.get(i).setmCategory(category);*/
                                }
                            }

                            //}

                            mPostAdapter.notifyDataSetChanged();
                            mSwipeRefreshing.setRefreshing(false);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }else{
                    for (int i=0; i<mPostList.size(); i++){
                        if(mPostList.get(i).getmId().equals(id)){
                            mPostList.get(i).setmResult(result);
                            mPostList.get(i).setmAverage(average);

                        /*mPostList.get(i).setmId(id);
                        mPostList.get(i).setmName(name);
                        mPostList.get(i).setmCategory(category);
                        mPostList.get(i).setmCategory(category);*/
                        }
                    }

                    //}

                    mPostAdapter.notifyDataSetChanged();
                    mSwipeRefreshing.setRefreshing(false);

                }


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.v("DB", "removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void reloadData(){
        if(isTheFirstLoad){
            isTheFirstLoad = false;
        }


        if(isTheFirstLoad)
            btn_new_posts.setVisibility(View.INVISIBLE);
        else
            btn_new_posts.setVisibility(View.VISIBLE);

        mPostAdapter.notifyDataSetChanged();
        mSwipeRefreshing.setRefreshing(false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if(mSwipeRefreshing == null){
            isTheFirstLoad = true;
            btn_new_posts = (Button) view.findViewById(R.id.btn_new_posts);

            mSwipeRefreshing = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
            mListView = (ListView) view.findViewById(android.R.id.list);
            mPostList = new LinkedList<>();

            mPostAdapter = new HomeAdapter(
                    getActivity(),
                    mPostList);


            mSwipeRefreshing.setRefreshing(true);
            mDatabase = FirebaseDatabase.getInstance().getReference();

            mListView.setAdapter(mPostAdapter);

            mSwipeRefreshing.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //refreshPostList();
                    mSwipeRefreshing.setRefreshing(false);
                }
            });

            refreshPostList();

            btn_new_posts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListView.smoothScrollToPosition(0);
                    btn_new_posts.setVisibility(View.INVISIBLE);
                }
            });

           mSwipeRefreshing.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
               @Override
               public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
                   btn_new_posts.setVisibility(View.INVISIBLE);
                   return true;
               }
           });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
