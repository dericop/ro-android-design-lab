package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    private OnFragmentInteractionListener mListener;

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final GridView grid_items = (GridView) view.findViewById(R.id.grid_items);
        final ArrayList<Post> myItems = new ArrayList<>();
        final MyItemAdapter itemAdapter = new MyItemAdapter(this.getContext(), myItems);
        grid_items.setAdapter(itemAdapter);

        Log.v("view", "test view");

        if(mHome.user != null){
            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("user-data").child(mHome.user.getUid());
            firebaseDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        myItems.clear();

                        HashMap<String, HashMap<String, String>> map = (HashMap)dataSnapshot.getValue();

                        Log.v("DB", map.toString());
                        for (String key: map.keySet()){
                            HashMap<String, String> values = map.get(key);

                            String name = values.get("name");
                            String frecuency = values.get("frecuency");
                            String category = values.get("category");
                            String image = values.get("image");
                            String user = values.get("user");
                            String id = values.get("id");

                            if(map.get("duration") != null){
                                String duration = values.get("duration");
                                myItems.add(new Post(id, name, category, frecuency, image, duration, user));

                            }else{
                                myItems.add(new Post(id, name, category, frecuency, image, user));
                            }

                            itemAdapter.notifyDataSetChanged();
                            grid_items.setAdapter(itemAdapter);


                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

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

    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

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
