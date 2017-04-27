package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TipsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private DatabaseReference database;
    private LinkedList<Tip> mTips;

    private TipsRecyclerViewAdapter recyclerViewAdapter;

    private String lastTipLoaded = "";
    private boolean isTheFirstItem = true; //usado para obtener la primer clave en la paginación
    private boolean flag_loading = false;

    private int countOfElementsByPage = 10;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TipsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TipsFragment newInstance(int columnCount) {
        TipsFragment fragment = new TipsFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tips_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            lastTipLoaded = "";
            isTheFirstItem = true;
            flag_loading = false;

            loadTips(recyclerView);
        }
        return view;
    }

    private void loadNextPage(){
        database = FirebaseDatabase.getInstance().getReference();
        database = database.child("tips");

        database.orderByKey().limitToLast(countOfElementsByPage).endAt(lastTipLoaded).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    final HashMap<String, Object> tipMap = (HashMap)dataSnapshot.getValue();
                    isTheFirstItem = true;
                    SortedSet<String> keys = new TreeSet(tipMap.keySet());
                    tipMap.remove(keys.last());
                    keys = new TreeSet(tipMap.keySet());

                    for (String key : keys) {

                        if(isTheFirstItem){
                            lastTipLoaded = key+"";
                            isTheFirstItem = false;
                        }

                        updateTipsFeed((HashMap)tipMap.get(key), 1);
                    }

                    recyclerViewAdapter.notifyDataSetChanged();
                    flag_loading = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private Tip createBaseTip(HashMap tipMap){

        final String app = (String)tipMap.get("app");
        final String description =(String) tipMap.get("description");
        final String id = (String)tipMap.get("id");
        final String image = (String)tipMap.get("image");
        final String name = (String)tipMap.get("name");
        final String type = (String)tipMap.get("type");

        Tip tip = new Tip(id, name, type, description, image, app);
        return tip;
    }

    private void reloadData(){

        recyclerViewAdapter.notifyDataSetChanged();
        //mSwipeRefreshing.setRefreshing(false);
    }

    //Position: 0 -> ingresar al principio, 1 - ingresar al final
    private void updateTipsFeed(HashMap map, int position){
        final HashMap<String, Object> postMap = map;
        final Tip tip = createBaseTip(postMap);

        if(tip.getApp().equals(WelcomeActivity.CURRENT_APP_VERSION)){
            if(position == 0)
                mTips.addFirst(tip);
            else{
                mTips.addLast(tip);
            }
        }

        reloadData();
    }

    private void loadTips(final RecyclerView recyclerView){
        database = FirebaseDatabase.getInstance().getReference();
        database = database.child("tips");
        mTips = new LinkedList<>();
        recyclerViewAdapter = new TipsRecyclerViewAdapter(mTips, mListener);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

        database.orderByKey().limitToLast(countOfElementsByPage).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue() != null){
                    Tip tip = dataSnapshot.getValue(Tip.class);
                    if(tip.getApp().equals(WelcomeActivity.CURRENT_APP_VERSION)){
                        mTips.addFirst(tip);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                }

                if(s!=null && isTheFirstItem){
                    lastTipLoaded = s;
                    isTheFirstItem = false;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){

                    Tip tipForDelete = dataSnapshot.getValue(Tip.class);
                    for (int i =0; i< mTips.size(); i++){
                        if(mTips.get(i).getId().equals(tipForDelete.getId())){
                            mTips.remove(i);
                            recyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(layoutManager.findLastCompletelyVisibleItemPosition() == (mTips.size() -1)){
                    Log.v("Scrolled", flag_loading+"");

                    if(!flag_loading)
                    {
                        flag_loading = true;
                        loadNextPage();

                        Log.v("Scrolled", "Next page");
                    }
                }
            }
        });

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mTips = new LinkedList<>();
        recyclerViewAdapter.notifyDataSetChanged();
        lastTipLoaded = "";
    }

    @Override
    public void onResume() {
        super.onResume();

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
        void onListFragmentInteraction(Tip item);
    }
}
