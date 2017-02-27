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
import android.widget.AbsListView;
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
public class Home extends ListFragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SwipeRefreshLayout mSwipeRefreshing;
    boolean isTheFirstLoad = true;
    private DatabaseReference mDatabase = null;
    private DatabaseReference postRef = null;
    private HomeAdapter mPostAdapter= null;
    private Button btn_new_posts = null;
    private String lastPostLoaded = "";
    private boolean flag_loading = false;
    private int countOfItemsLoadedForTime = 5;


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

    private long getResult(HashMap postMap){
        long result = 0;
        if(postMap.get("result") != null){
            result = (long)postMap.get("result");
            Log.v("DB", result+":result");
        }
        return result;
    }

    private long getAverage(HashMap postMap){
        long average = 0;
        if(postMap.get("average") != null){
            try {
                average = (long)postMap.get("average");
            }catch (NumberFormatException ex){

            }
        }
        return average;
    }

    private void assignPostsReference(){
        if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
            postRef = mDatabase.child("user-posts");
        }else{
            postRef = mDatabase.child("user-posts-reflexive");
        }
    }

    private void assignUsersReference(){
        if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
            postRef = mDatabase.child("users");
        }else{
            postRef = mDatabase.child("users-reflexive");
        }
    }

    private Post createBasePost(HashMap postMap){

        final String name = (String)postMap.get("name");
        final String frecuency =(String) postMap.get("frecuency");
        final String category = (String)postMap.get("category");
        final String image = (String)postMap.get("image");
        final String user = (String)postMap.get("user");
        final String id = (String)postMap.get("id");
        long result = getResult(postMap);
        long average = getAverage(postMap);


        Post post = new Post(id, name, category, frecuency, image, user, result, average);

        if(postMap.get("last_share") != null){
            post.setmTooShared(postMap.get("last_share")+"");
        }

        if(postMap.get("duration") != null){
            post.setmDuration((String)postMap.get("duration"));
        }

        if(postMap.get("r_pi") != null && postMap.get("r_aa")!=null && postMap.get("r_gs")!=null && postMap.get("r_ch") != null){
            post.setmPi(Double.parseDouble(postMap.get("r_pi")+""));
            post.setmAa(Double.parseDouble(postMap.get("r_aa")+""));
            post.setmGs(Double.parseDouble(postMap.get("r_gs")+""));
            post.setmCh(Double.parseDouble(postMap.get("r_ch")+""));
        }

        return post;
    }

    //Position: 0 -> ingresar al principio, 1 - ingresar al final
    private void updatePostsFeed(HashMap map, int position){
        final HashMap<String, Object> postMap = map;
        Log.v("Pagination",postMap.toString());
        final Post post = createBasePost(postMap);

        if(position == 0)
            mPostList.addFirst(post);
        else
            mPostList.addLast(post);
        reloadData();

        assignUsersReference();
        postRef.child(post.getmUser()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){

                HashMap<String, Object> userMap = (HashMap)dataSnapshot.getValue();
                final String userName = (String)userMap.get("mUserName");
                post.setmUserName(userName);
                mPostAdapter.notifyDataSetChanged();

                postRef.child(post.getmTooShared()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getValue()!=null && !post.getmTooShared().equals("")){
                            HashMap<String, Object> tooSharedMap = (HashMap) dataSnapshot.getValue();
                            Log.v("DataShare", "Too:"+tooSharedMap.get("mUserName"));

                            Log.v("DataShare", "Too:"+post.getmTooShared());

                            final String tooSharedName = (String) tooSharedMap.get("mUserName");
                            post.setmTooShared(tooSharedName);

                            mPostAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void refreshPostList(){

        assignPostsReference();

        postRef.orderByKey().limitToLast(countOfItemsLoadedForTime).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final HashMap<String, Object> postMap = (HashMap)dataSnapshot.getValue();
                updatePostsFeed(postMap, 0);
                if(s!=null)
                    lastPostLoaded = s;

                Log.v("Pagination", "last page: "+lastPostLoaded);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final HashMap<String, Object> postMap = (HashMap)dataSnapshot.getValue();
                updatePostsFeed(postMap, 0);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadNextPage(){
        assignPostsReference();

        if(!lastPostLoaded.equals("")){
            postRef.orderByKey().limitToLast(countOfItemsLoadedForTime).endAt(lastPostLoaded).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.getValue()!=null){
                        final HashMap<String, Object> postMap = (HashMap)dataSnapshot.getValue();
                        for(Object key: postMap.keySet()){
                            updatePostsFeed((HashMap)postMap.get(key), 1);
                        }

                        flag_loading = true;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

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

    private void initData(){
        isTheFirstLoad = true;

        mSwipeRefreshing.setRefreshing(true);
        mDatabase = FirebaseDatabase.getInstance().getReference(); //Referencia de la base de datos
    }

    //Inicializa elementos gráficos y asigna eventos iniciales.
    private void initGraphicalElementsAndEvents(View view){
        //Obtener elementos gráficos
        btn_new_posts = (Button) view.findViewById(R.id.btn_new_posts);
        mSwipeRefreshing = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        mListView = (ListView) view.findViewById(android.R.id.list);

        mPostList = new LinkedList<>();
        //Creación de adaptador para la lista.
        mPostAdapter = new HomeAdapter(
                getActivity(),
                mPostList);
        mListView.setAdapter(mPostAdapter);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                {
                    if(flag_loading == false)
                    {
                        flag_loading = true;
                        loadNextPage();

                    }
                }
            }
        });


        //Creación del evento de refresh
        mSwipeRefreshing.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //refreshPostList();
                mSwipeRefreshing.setRefreshing(false);
            }
        });

        //Evento para mostrar los posts nuevos.
        btn_new_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListView.smoothScrollToPosition(0);
                btn_new_posts.setVisibility(View.INVISIBLE);
            }
        });

        //Evento para esconder boton de nuevas publicaciones.
        mSwipeRefreshing.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
                btn_new_posts.setVisibility(View.INVISIBLE);
                return true;
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(mSwipeRefreshing == null){
            //Inicialización
            initGraphicalElementsAndEvents(view);
            initData();

            //Consultar los primeros posts
            refreshPostList();
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
