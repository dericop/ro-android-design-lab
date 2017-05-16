package com.ucaldas.ro.reduccionobesidad;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Home.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends ListFragment implements AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SwipeRefreshLayout mSwipeRefreshing;
    boolean isTheFirstLoad = true;
    private HomeAdapter mPostAdapter = null;
    private Button btn_new_posts = null;
    private String lastPostLoaded = "";
    private boolean flag_loading = false;
    private int countOfItemsLoadedForTime = 10;

    private boolean isTheFirstItem = true; //usado para obtener la primer clave en la paginación

    private DatabaseReference mDatabase = null;
    private DatabaseReference postRef = null;
    private DatabaseReference userRef = null;
    private DatabaseReference commentRef = null;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    LinkedList<Post> mPostList;

    private int countOfPages = 0;


    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters. 40689793015911163  3013072302
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
    }

    View mheaderView;
    View successView;
    View errorView;
    LayoutInflater mInflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mInflater = inflater;
        if (WelcomeActivity.CURRENT_APP_VERSION.equals("A"))
            mheaderView = inflater.inflate(R.layout.header_home, null);
        else
            mheaderView = inflater.inflate(R.layout.header_home_reflexive, null);

        successView = inflater.inflate(R.layout.header_home_success, null);
        errorView = inflater.inflate(R.layout.header_home_error, null);

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPostList = new LinkedList<>();
        //Creación de adaptador para la lista.
        mPostAdapter = new HomeAdapter(
                getActivity(),
                mPostList);

        setListAdapter(mPostAdapter);
        getListView().setOnItemClickListener(this);
    }

    private void loadChallenge(View view) {
        if (mheaderView != null) {
//            this.getListView().addHeaderView(mheaderView);
        }

        setListAdapter(mPostAdapter);

        /*Button opt1 = (Button) view.findViewById(R.id.opt_1);
        opt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getListView().removeHeaderView(mheaderView);
                getListView().addHeaderView(successView);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getListView().removeHeaderView(successView);
                    }
                }, 2000);

            }
        });

        Button opt2 = (Button) view.findViewById(R.id.opt_2);
        opt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getListView().removeHeaderView(mheaderView);
                getListView().addHeaderView(errorView);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getListView().removeHeaderView(errorView);
                        getListView().addHeaderView(mheaderView);
                    }
                }, 2000);
            }
        });*/

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isTheFirstLoad = true;
        lastPostLoaded = "";
        flag_loading = false;
        isTheFirstItem = true;

        mPostList = new LinkedList<>();

        mPostAdapter = new HomeAdapter(
                getActivity(),
                mPostList);

        setListAdapter(mPostAdapter);

        if (mSwipeRefreshing == null) {
            //Inicialización
            initGraphicalElementsAndEvents(view);
            initData();
            //Consultar los primeros posts
            refreshPostList();
            loadChallenge(view);
        }
    }

    private long getResult(HashMap postMap) {
        long result = 0;
        if (postMap.get("result") != null) {
            result = (long) postMap.get("result");
        }
        return result;
    }

    private long getAverage(HashMap postMap) {
        long average = 0;
        if (postMap.get("average") != null) {
            try {
                average = (long) postMap.get("average");
            } catch (NumberFormatException ex) {

            }
        }
        return average;
    }

    private void assignPostsReference() {
        if (WelcomeActivity.CURRENT_APP_VERSION.equals("A")) {
            postRef = mDatabase.child("user-posts");
        } else {
            postRef = mDatabase.child("user-posts-reflexive");
        }
    }

    private void assignUsersReference() {
        if (WelcomeActivity.CURRENT_APP_VERSION.equals("A")) {
            userRef = mDatabase.child("users");
        } else {
            userRef = mDatabase.child("users-reflexive");
        }
    }

    private void assignCommentsReference() {
        if (WelcomeActivity.CURRENT_APP_VERSION.equals("A")) {
            commentRef = mDatabase.child("user-comments");
        } else {
            commentRef = mDatabase.child("user-comments-reflexive");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;

        if (context instanceof Activity) {

        }
    }

    private Post createBasePost(HashMap postMap) {

        final String name = (String) postMap.get("name");
        final String frecuency = (String) postMap.get("frecuency");
        final String category = (String) postMap.get("category");
        final String image = (String) postMap.get("image");
        final String user = (String) postMap.get("user");
        final String id = (String) postMap.get("id");
        long result = getResult(postMap);
        long average = getAverage(postMap);

        Post post = new Post(id, name, category, frecuency, image, user, result, average);

        if (postMap.get("last_share") != null) {
            post.setLast_share(postMap.get("last_share") + "");
        }

        if (postMap.get("duration") != null) {
            post.setDuration((String) postMap.get("duration"));
        }

        if (postMap.get("r_pi") != null && postMap.get("r_aa") != null && postMap.get("r_gs") != null && postMap.get("r_ch") != null) {
            post.setR_pi(Double.parseDouble(postMap.get("r_pi") + ""));
            post.setR_aa(Double.parseDouble(postMap.get("r_aa") + ""));
            post.setR_gs(Double.parseDouble(postMap.get("r_gs") + ""));
            post.setR_ch(Double.parseDouble(postMap.get("r_ch") + ""));
        }

        if (postMap.get("replyCount") != null) {
            post.setReplyCount((long) postMap.get("replyCount"));
        }

        return post;
    }

    //Position: 0 -> ingresar al principio, 1 - ingresar al final
    private void updatePostsFeed(HashMap map, int position) {
        final HashMap<String, Object> postMap = map;
        final Post post = createBasePost(postMap);

        if (post != null && post.getUser() != null) {
            if (position == 0)
                mPostList.addFirst(post);
            else {
                mPostList.addLast(post);
            }

            reloadData();

            assignUsersReference();

            userRef.child(post.getUser()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        HashMap<String, Object> userMap = (HashMap) dataSnapshot.getValue();
                        final String userName = (String) userMap.get("mUserName");
                        post.setmUserName(userName);
                        mPostAdapter.notifyDataSetChanged();
                    }

                    assignCommentsReference();
                    commentRef.orderByChild("id").equalTo(post.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() != null) {
                                long countOfComments = dataSnapshot.getChildrenCount();

                                post.setCountOfComments(countOfComments);
                                mPostAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    if (post.getLast_share() != null && !post.getLast_share().equals("")) {
                        userRef.child(post.getLast_share()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.getValue() != null && !post.getLast_share().equals("")) {
                                    HashMap<String, Object> tooSharedMap = (HashMap) dataSnapshot.getValue();

                                    final String tooSharedName = (String) tooSharedMap.get("mUserName");
                                    post.setLast_share(tooSharedName);

                                    mPostAdapter.notifyDataSetChanged();
                                }
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


    }


    private void searchPostAndUpdate(DataSnapshot dataSnapshot) {

        final Post postForSearch = dataSnapshot.getValue(Post.class);

        DatabaseReference updateReference;

        if (WelcomeActivity.CURRENT_APP_VERSION.equals("A")) {
            updateReference = mDatabase.child("users");
        } else {
            updateReference = mDatabase.child("users-reflexive");
        }

        for (final Post p : mPostList) {
            if (p.getId().equals(postForSearch.getId())) {

                p.setName(postForSearch.getName());
                p.setAverage(postForSearch.getAverage());
                p.setCategory(postForSearch.getCategory());
                p.setDuration(postForSearch.getDuration());
                p.setFrecuency(postForSearch.getFrecuency());
                p.setImage(postForSearch.getImage());
                p.setR_aa(postForSearch.getR_aa());
                p.setR_ch(postForSearch.getR_ch());
                p.setR_gs(postForSearch.getR_gs());
                p.setR_pi(postForSearch.getR_pi());
                p.setResult(postForSearch.getResult());
                p.setReplyCount(postForSearch.getReplyCount());

                mPostAdapter.notifyDataSetChanged();

                commentRef.orderByChild("id").equalTo(p.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() != null) {
                            long countOfComments = dataSnapshot.getChildrenCount();

                            p.setCountOfComments(countOfComments);
                            mPostAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                if (postForSearch.getLast_share() != null && !postForSearch.getLast_share().equals("")) {

                    updateReference.child(postForSearch.getLast_share()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() != null) {
                                HashMap<String, Object> tooSharedMap = (HashMap) dataSnapshot.getValue();

                                final String tooSharedName = (String) tooSharedMap.get("mUserName");
                                p.setLast_share(tooSharedName);

                                mPostAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    mPostAdapter.notifyDataSetChanged();
                }
                return;
            }
        }
    }

    private void refreshPostList() {

        //assignPostsReference();
        DatabaseReference pReference;

        if (WelcomeActivity.CURRENT_APP_VERSION.equals("A")) {
            pReference = mDatabase.child("user-posts");
        } else {
            pReference = mDatabase.child("user-posts-reflexive");
        }

        pReference.orderByKey().limitToLast(countOfItemsLoadedForTime).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final HashMap<String, Object> postMap = (HashMap) dataSnapshot.getValue();

                updatePostsFeed(postMap, 0);

                if (s != null && isTheFirstItem) {
                    lastPostLoaded = s;
                    isTheFirstItem = false;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                searchPostAndUpdate(dataSnapshot);

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

    private void loadNextPage() {
        //assignPostsReference();
        DatabaseReference pRef;

        if (WelcomeActivity.CURRENT_APP_VERSION.equals("A")) {
            pRef = mDatabase.child("user-posts");
        } else {
            pRef = mDatabase.child("user-posts-reflexive");
        }

        if (!lastPostLoaded.equals("")) {

            pRef.orderByKey().limitToLast(countOfItemsLoadedForTime).endAt(lastPostLoaded).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        final HashMap<String, Object> postMap = (HashMap) dataSnapshot.getValue();
                        isTheFirstItem = true;
                        SortedSet<String> keys = new TreeSet(postMap.keySet());
                        postMap.remove(keys.last());
                        keys = new TreeSet(postMap.keySet());

                        for (String key : keys) {

                            if (isTheFirstItem) {
                                countOfPages++;
                                lastPostLoaded = key + "";
                                isTheFirstItem = false;
                            }

                            updatePostsFeed((HashMap) postMap.get(key), 1);
                        }

                        mPostAdapter.notifyDataSetChanged();
                        flag_loading = false;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private void reloadData() {
        if (isTheFirstLoad) {
            isTheFirstLoad = false;
        }

        if (isTheFirstLoad)
            btn_new_posts.setVisibility(View.INVISIBLE);
        else
            btn_new_posts.setVisibility(View.VISIBLE);

        mPostAdapter.notifyDataSetChanged();
        mSwipeRefreshing.setRefreshing(false);
    }

    private void initData() {
        isTheFirstLoad = true;

        mSwipeRefreshing.setRefreshing(true);
        mDatabase = FirebaseDatabase.getInstance().getReference(); //Referencia de la base de datos
    }

    //Inicializa elementos gráficos y asigna eventos iniciales.
    private void initGraphicalElementsAndEvents(View view) {
        //Obtener elementos gráficos
        btn_new_posts = (Button) view.findViewById(R.id.btn_new_posts);
        mSwipeRefreshing = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);


        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {


                Log.v("Header", "Total" + totalItemCount + "");
                Log.v("Header", "Visible" + (firstVisibleItem + visibleItemCount));
                Log.v("Header", "Flag: " + flag_loading);

                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                    if (!flag_loading) {
                        flag_loading = true;
                        loadNextPage();
                    }
                }

                Log.v("Header", getListView().getHeaderViewsCount() + "");
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
                getListView().smoothScrollToPosition(0);
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
    public void onDetach() {
        super.onDetach();
        lastPostLoaded = "";
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

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
