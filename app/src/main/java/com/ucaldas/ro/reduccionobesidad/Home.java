package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
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
    final AtomicInteger count = new AtomicInteger();
    DatabaseReference mDatabase = null;
    HomeAdapter mPostAdapter= null;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    private void refreshPostList(){
        /*mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int newCount = count.incrementAndGet();
                mPostAdapter.add(new Post());
                mPostAdapter.notifyDataSetChanged();

                if(newCount == dataSnapshot.getChildrenCount()){
                    mSwipeRefreshing.setRefreshing(false);
                    count.set(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mSwipeRefreshing.setRefreshing(false);
            }

        });*/

        /*final FirebaseListAdapter<HashMap> firebaseListAdapter = new FirebaseListAdapter<HashMap>(
                getActivity(),
                HashMap.class,
                R.layout.list_home_item,
                mDatabase
        ) {
            @Override
            protected void populateView(View v, HashMap model, int position) {

            }

            @Override
            protected HashMap parseSnapshot(DataSnapshot snapshot) {
                return (HashMap) snapshot.getValue();
            }
        };

        firebaseListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mSwipeRefreshing.setRefreshing(false);
            }
        });

        mListView.setAdapter(firebaseListAdapter);*/

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.v("DB", dataSnapshot.getValue().toString());
                HashMap map = (HashMap)dataSnapshot.getValue();

                //Log.v("DB", ((HashMap)map.get("-KbMbLITyK5aVJRF4_lV")).get("name")+"");

                /*for (int i=0; i<map.keySet().size(); i++) {

                }*/
                mPostList.addFirst(new Post());
                //Post post = dataSnapshot.getValue(Post.class);

                mPostAdapter.notifyDataSetChanged();
                mListView.smoothScrollToPosition(0);
                mSwipeRefreshing.setRefreshing(false);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(mSwipeRefreshing == null){
            mSwipeRefreshing = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
            mListView = (ListView) view.findViewById(android.R.id.list);
            mPostList = new LinkedList<>();

            mPostAdapter = new HomeAdapter(
                    getActivity(),
                    mPostList);


            mSwipeRefreshing.setRefreshing(true);
            mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://reduccion-de-obesidad-7414c.firebaseio.com/user-posts");
            mDatabase.limitToLast(10);

            mListView.setAdapter(mPostAdapter);

            mSwipeRefreshing.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //refreshPostList();
                    mSwipeRefreshing.setRefreshing(false);
                }
            });

            refreshPostList();

        }

        /*RecyclerView postList = (RecyclerView) view.findViewById(android.R.id.list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(false);

        postList.setHasFixedSize(false);
        postList.setLayoutManager(layoutManager);


        FirebaseRecyclerAdapter<GenericTypeIndicator, PostViewHolder> mAdapter = new FirebaseRecyclerAdapter<GenericTypeIndicator, PostViewHolder>(
                GenericTypeIndicator.class, android.R.layout.two_line_list_item,
                PostViewHolder.class, mDatabase) {

            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, final GenericTypeIndicator model, final int position) {

                String key = this.getRef(position).getKey();
                Log.v("DB", key);
                mDatabase.child("user-posts").child(key).addListenerForSingleValueEvent(new ValueEventListener() {

                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        ((TextView)viewHolder.itemView.findViewById(android.R.id.text1)).setText(name);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

            }
        };

        postList.setAdapter(mAdapter);*/









       /* mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.v("DB", dataSnapshot.getValue().toString());
                HashMap map = (HashMap)dataSnapshot.getValue();

                Log.v("DB", ((HashMap)map.get("-KbMbLITyK5aVJRF4_lV")).get("name")+"");
                //mLeadsAdapter.add(new Post());


                /*for (String k:map.keySet()) {
                    mLeadsAdapter.add(map.get(k));
                }

                //Post post = dataSnapshot.getValue(Post.class);

                mLeadsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        });*/

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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public static class PostViewHolder extends RecyclerView.ViewHolder{

        public PostViewHolder(View itemView) {
            super(itemView);
        }


    }

}
