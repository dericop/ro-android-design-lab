package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyItems.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyItems#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyItems extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayList<Post> myItems;

    private View refView;
    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout itemsRefresh;

    private DatabaseReference itemsRef = null;
    private DatabaseReference mDatabase = null;


    public MyItems() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyItems.
     */
    // TODO: Rename and change types and number of parameters
    public static MyItems newInstance(String param1, String param2) {
        MyItems fragment = new MyItems();
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

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        if(menuVisible && refView!=null){
            //loadItems();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void loadItems(){
        final GridView grid_items = (GridView) refView.findViewById(R.id.grid_items);
        myItems = new ArrayList<>();
        final MyItemAdapter itemAdapter = new MyItemAdapter(this.getContext(), myItems);
        grid_items.setAdapter(itemAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference firebaseDatabase = null;
        if(mHome.user != null){
            if(WelcomeActivity.CURRENT_APP_VERSION.equals("A"))
                firebaseDatabase = mDatabase.child("user-data").child(mHome.user.getUid());
            else
                firebaseDatabase = mDatabase.child("user-data-reflexive").child(mHome.user.getUid());

            firebaseDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        myItems.clear();
                        HashMap<String, HashMap<String, String>> map = (HashMap)dataSnapshot.getValue();

                        for (String key: map.keySet()){
                            HashMap<String, Object> values = (HashMap) map.get(key);

                            String name = (String)values.get("name");
                            String frecuency = (String)values.get("frecuency");
                            String category = (String)values.get("category");
                            String image = (String)values.get("image");
                            String user = (String)values.get("user");
                            String id = (String)values.get("id");

                            /*long result = 0;
                            if(values.get("result") != null){
                                result = (long)values.get("result");
                            }

                            long average = 0;
                            if(values.get("average") != null){
                                average = (long)values.get("average");
                            }*/

                            getPostReference(id);

                            final Post post = new Post();
                            post.setName(name);
                            post.setFrecuency(frecuency);
                            post.setCategory(category);
                            post.setImage(image);
                            post.setUser(user);
                            post.setId(id);

                            if(values.get("duration") != null){
                                String duration = (String)values.get("duration");
                                post.setDuration(duration);
                            }

                            itemsRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Post rPost = dataSnapshot.getValue(Post.class);

                                    if(rPost!=null){
                                        if(rPost.getResult()!=0)
                                            post.setResult(rPost.getResult());

                                        if(rPost.getR_aa() != 0)
                                            post.setR_aa(rPost.getR_aa());

                                        if(rPost.getR_pi() != 0)
                                            post.setR_pi(rPost.getR_pi());

                                        if(rPost.getR_ch() != 0)
                                            post.setR_pi(rPost.getR_ch());

                                        if(rPost.getR_gs() != 0)
                                            post.setR_gs(rPost.getR_gs());

                                        myItems.add(post);
                                        itemAdapter.notifyDataSetChanged();
                                        grid_items.setAdapter(itemAdapter);
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    private DatabaseReference getPostReference(String id){
        if(!id.equals("")){
            if(WelcomeActivity.CURRENT_APP_VERSION.equals("A"))
                itemsRef = mDatabase.child("user-posts").child(id);
            else
                itemsRef = mDatabase.child("user-posts-reflexive").child(id);
        }

        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refView = view;
        loadItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_items, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.v("Pager", refView+" Attached!!");
        if(refView != null)
            loadItems();

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
}
