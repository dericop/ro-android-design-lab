package com.ucaldas.ro.reduccionobesidad;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    ArrayList<Post> myItems;

    private View refView;
    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout itemsRefresh;

    private DatabaseReference itemsRef = null;
    private DatabaseReference mDatabase = null;

    private GridView grid_items;
    private MyItemAdapter itemAdapter;

    private DatabaseReference firebaseDatabase;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public MyItems() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static MyItems newInstance() {
        MyItems fragment = new MyItems();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myItems = new ArrayList<>();
        itemAdapter = new MyItemAdapter(this.getContext(), myItems);

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public void loadItems() {

        if (mHome.user == null) {
            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // AUser is signed in
                        Log.d("AUser", "onAuthStateChanged:signed_in:" + user.getUid());

                        mHome.user = user; //Asignación de usuario a la clase principal
                        getDataFromDB();

                    } else {
                        // AUser is signed out
                        Log.d("AUser", "onAuthStateChanged:signed_out");

                    }
                }
            };

            mAuth.addAuthStateListener(mAuthListener);

        } else {
            getDataFromDB();
        }


    }

    private void getDataFromDB(){

        if (WelcomeActivity.CURRENT_APP_VERSION.equals("A"))
            firebaseDatabase = mDatabase.child("user-data").child(mHome.user.getUid());
        else
            firebaseDatabase = mDatabase.child("user-data-reflexive").child(mHome.user.getUid());


        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    myItems.clear();
                    HashMap<String, HashMap<String, String>> map = (HashMap) dataSnapshot.getValue();

                    for (String key : map.keySet()) {
                        HashMap<String, Object> values = (HashMap) map.get(key);

                        String name = (String) values.get("name");
                        String frecuency = (String) values.get("frecuency");
                        String category = (String) values.get("category");
                        String image = (String) values.get("image");
                        String user = (String) values.get("user");
                        String id = (String) values.get("id");

                        getPostReference(id);

                        final Post post = new Post();
                        post.setName(name);
                        post.setFrecuency(frecuency);
                        post.setCategory(category);
                        post.setImage(image);
                        post.setUser(user);
                        post.setId(id);

                        if (values.get("duration") != null) {
                            String duration = (String) values.get("duration");
                            post.setDuration(duration);
                        }

                        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Post rPost = dataSnapshot.getValue(Post.class);

                                if (rPost != null) {
                                    if (rPost.getResult() != 0)
                                        post.setResult(rPost.getResult());

                                    if (rPost.getR_aa() != 0)
                                        post.setR_aa(rPost.getR_aa());

                                    if (rPost.getR_pi() != 0)
                                        post.setR_pi(rPost.getR_pi());

                                    if (rPost.getR_ch() != 0)
                                        post.setR_pi(rPost.getR_ch());

                                    if (rPost.getR_gs() != 0)
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

    private DatabaseReference getPostReference(String id) {
        if (!id.equals("")) {
            if (WelcomeActivity.CURRENT_APP_VERSION.equals("A"))
                itemsRef = mDatabase.child("user-posts").child(id);
            else
                itemsRef = mDatabase.child("user-posts-reflexive").child(id);
        }

        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        grid_items = (GridView) refView.findViewById(R.id.grid_items);
        grid_items.setAdapter(itemAdapter);

        loadItems();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        refView = inflater.inflate(R.layout.fragment_my_items, container, false);
        return refView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;

        if (context instanceof Activity) {
            activity = (Activity) context;
            //refView = activity.getCurrentFocus();
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
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
