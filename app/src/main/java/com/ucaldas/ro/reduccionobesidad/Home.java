package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

public class Home extends ListFragment implements AdapterView.OnItemClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SwipeRefreshLayout mSwipeRefreshing;
    boolean isTheFirstLoad = true;
    private HomeAdapter mPostAdapter = null;
    private String lastPostLoaded = "";
    private boolean flag_loading = false;
    private int countOfItemsLoadedForTime = 10;

    private boolean isTheFirstItem = true; //usado para obtener la primer clave en la paginación

    private DatabaseReference mDatabase = null;
    private DatabaseReference userRef = null;
    private DatabaseReference commentRef = null;

    private String mParam1;
    private String mParam2;

    LinkedList<Post> mPostList;

    public Home() {}

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

    private void sendResponseToServer(long response, String questionId){
        DatabaseReference responseRef = FirebaseDatabase.getInstance().getReference();

        if(mHome.user != null){
            HashMap data= new HashMap();
            data.put("isCorrect",response);
            data.put("question",questionId);

            responseRef = responseRef.child("questions-user").child(mHome.user.getUid());
            responseRef.child("/"+questionId).setValue(data);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void updateViewWithChallenge(final Challenge challenge, View view){
        final LinearLayout challengeView = (LinearLayout) view.findViewById(R.id.challengeView);
        final LinearLayout challengecontainer = (LinearLayout) view.findViewById(R.id.questionsContainer);
        challengecontainer.setVisibility(View.VISIBLE);
        challengeView.setVisibility(View.VISIBLE);

        TextView txtTitle = (TextView) view.findViewById(R.id.challengeTitle);
        txtTitle.setText(challenge.getTitle());

        final LinearLayout questionView = (LinearLayout) view.findViewById(R.id.questionView);
        final LinearLayout successView = (LinearLayout) view.findViewById(R.id.successView);
        final LinearLayout errorView = (LinearLayout) view.findViewById(R.id.errorView);

        questionView.setVisibility(View.GONE);
        successView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);

        final ImageButton challengeClose = (ImageButton) view.findViewById(R.id.closeChallenge);
        challengeClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            challengecontainer.setVisibility(View.GONE);
            }
        });

        final ImageButton closeWaitChallenge = (ImageButton) view.findViewById(R.id.closeWaitChallenge);
        closeWaitChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                challengecontainer.setVisibility(View.GONE);
            }
        });
    }

    private void updateViewWithQuestion(final Question question, View view){

        final LinearLayout questionscontainer = (LinearLayout) view.findViewById(R.id.questionsContainer);
        questionscontainer.setVisibility(View.VISIBLE);
        final LinearLayout questionsView = (LinearLayout) view.findViewById(R.id.questionView);
        questionsView.setVisibility(View.VISIBLE);

        TextView txtTitle = (TextView) view.findViewById(R.id.questionTitle);
        txtTitle.setText(question.getTitle());

        final ImageButton questionClose = (ImageButton) view.findViewById(R.id.closeQuestion);
        questionClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionscontainer.setVisibility(View.GONE);
            }
        });

        final LinearLayout questionView = (LinearLayout) view.findViewById(R.id.questionView);
        final LinearLayout successView = (LinearLayout) view.findViewById(R.id.successView);
        final LinearLayout errorView = (LinearLayout) view.findViewById(R.id.errorView);

        final Button opt1 = (Button) view.findViewById(R.id.opt_1);
        opt1.setText(question.getResponse1());

        opt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionView.setVisibility(View.GONE);

                if(question.getResponse1().equals(question.getCorrect())){
                    successView.setVisibility(View.VISIBLE);
                    errorView.setVisibility(View.GONE);
                    sendResponseToServer(1, question.getId());
                }else{
                    successView.setVisibility(View.GONE);
                    errorView.setVisibility(View.VISIBLE);
                    sendResponseToServer(2, question.getId());
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        questionscontainer.setVisibility(View.GONE);
                    }
                }, 2000);
            }
        });

        Button opt2 = (Button) view.findViewById(R.id.opt_2);
        opt2.setText(question.getResponse2());

        opt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionView.setVisibility(View.GONE);

                if(question.getResponse2().equals(question.getCorrect())){
                    successView.setVisibility(View.VISIBLE);
                    errorView.setVisibility(View.GONE);
                    sendResponseToServer(1, question.getId());
                }else{
                    successView.setVisibility(View.GONE);
                    errorView.setVisibility(View.VISIBLE);
                    sendResponseToServer(2, question.getId());
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        questionscontainer.setVisibility(View.GONE);
                    }
                }, 2000);
            }
        });
    }


    public void showUploadPopUp(View v, final String challengeId) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.upload_menu, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.showMedia:
                        Intent gallery_intent = new Intent(getContext(), AddPost.class);
                        gallery_intent.putExtra("source", "gallery");
                        gallery_intent.putExtra("challenge", true);
                        gallery_intent.putExtra("challengeId", challengeId);
                        startActivity(gallery_intent);

                        break;
                    case R.id.takePhoto:
                        Intent camera_intent = new Intent(getContext(), AddPost.class);
                        camera_intent.putExtra("source", "camera");
                        camera_intent.putExtra("challenge", true);
                        camera_intent.putExtra("challengeId", challengeId);
                        startActivity(camera_intent);

                        break;
                    default:
                        break;
                }

                return false;
            }
        });
    }

    private void loadChallenge(final View view){
        setListAdapter(mPostAdapter);
        DatabaseReference challengeRef = FirebaseDatabase.getInstance().getReference();
        final ImageButton uploadBtn = (ImageButton)view.findViewById(R.id.uploadBtn);

        challengeRef.child("challenges").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    HashMap<String, HashMap> map = (HashMap) dataSnapshot.getValue();
                    Iterator keys = map.keySet().iterator();
                    while(keys.hasNext()){
                        String key = keys.next()+"";

                        Calendar sDate = Calendar.getInstance();
                        sDate.setTimeInMillis(((long)map.get(key).get("initDate")));

                        Calendar eDate = Calendar.getInstance();
                        eDate.setTimeInMillis(((long)map.get(key).get("endDate")));

                        Calendar cDate = Calendar.getInstance();

                        if(sDate.compareTo(cDate) <= 0 && eDate.compareTo(cDate) >= 0){
                            //Comparar el tema de fechas
                            final Challenge challenge = new Challenge();
                            final String challengeId = map.get(key).get("id")+"";
                            challenge.setId(challengeId);
                            challenge.setInitDate((long)map.get(key).get("initDate"));
                            challenge.setEndDate((long)map.get(key).get("endDate"));
                            challenge.setTitle(map.get(key).get("title")+"");

                            uploadBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showUploadPopUp(v, challengeId);
                                }
                            });

                            if(mHome.user != null){

                                FirebaseDatabase.getInstance().getReference().child("gamification-score")
                                        .child(mHome.user.getUid()).child(challenge.getId()).addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.getValue() == null) {
                                            updateViewWithChallenge(challenge, view);
                                        }else{ //existe el registro

                                            HashMap<String,Object> data = (HashMap) dataSnapshot.getValue();
                                            long result = 0;
                                            if(data.get("result") != null)
                                                result = (long) data.get("result");

                                            final LinearLayout challengeView = (LinearLayout) view.findViewById(R.id.challengeView);
                                            final LinearLayout challengecontainer = (LinearLayout) view.findViewById(R.id.questionsContainer);

                                            if(result == 0) { // el reto esta en espera de calificación

                                                LinearLayout waitChallengeView = (LinearLayout) view.findViewById(R.id.wait_challenge);
                                                challengecontainer.setVisibility(View.VISIBLE);

                                                if(challengeView!=null && waitChallengeView!=null){
                                                    challengeView.setVisibility(View.GONE);
                                                    waitChallengeView.setVisibility(View.VISIBLE);
                                                }

                                            }else if(result == 1){ // el reto fué aceptado
                                                challengeView.setVisibility(View.GONE);
                                                final LinearLayout successChallengeView = (LinearLayout) view.findViewById(R.id.good_challenge);
                                                successChallengeView.setVisibility(View.VISIBLE);
                                                challengecontainer.setVisibility(View.VISIBLE);

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        successChallengeView.setVisibility(View.GONE);
                                                        challengecontainer.setVisibility(View.GONE);
                                                        FirebaseDatabase.getInstance().getReference().child("gamification-score")
                                                                .child(mHome.user.getUid()).child(challenge.getId()).child("result").setValue(3);
                                                    }
                                                }, 7000);

                                            }else if(result == 2){ // el reto fué rechazado
                                                challengeView.setVisibility(View.GONE);
                                                challengecontainer.setVisibility(View.VISIBLE);
                                                final LinearLayout badChallengeView = (LinearLayout) view.findViewById(R.id.bad_challenge);
                                                badChallengeView.setVisibility(View.VISIBLE);

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        badChallengeView.setVisibility(View.GONE);
                                                        challengeView.setVisibility(View.VISIBLE);
                                                        FirebaseDatabase.getInstance().getReference().child("gamification-score")
                                                                .child(mHome.user.getUid()).child(challenge.getId()).removeValue();
                                                        updateViewWithChallenge(challenge, view);
                                                    }
                                                }, 7000);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadQuestion(final View view) {
        setListAdapter(mPostAdapter);
        DatabaseReference challengeRef = FirebaseDatabase.getInstance().getReference();
        challengeRef.child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    HashMap<String, HashMap> map = (HashMap) dataSnapshot.getValue();
                    Iterator keys = map.keySet().iterator();
                    while(keys.hasNext()){
                        String key = keys.next()+"";

                        Calendar sDate = Calendar.getInstance();
                        sDate.setTimeInMillis(((long)map.get(key).get("startDate")));

                        Calendar eDate = Calendar.getInstance();
                        eDate.setTimeInMillis(((long)map.get(key).get("endDate")));

                        Calendar cDate = Calendar.getInstance();

                        if(sDate.compareTo(cDate) <= 0 && eDate.compareTo(cDate) >= 0){
                            //Comparar el tema de fechas
                            final Question question = new Question();
                            question.setId(map.get(key).get("id")+"");
                            question.setStartDate((long)map.get(key).get("startDate"));
                            question.setEndDate((long)map.get(key).get("endDate"));
                            question.setResponse1(map.get(key).get("response1")+"");
                            question.setResponse2(map.get(key).get("response2")+"");
                            question.setTitle(map.get(key).get("title")+"");
                            question.setCorrect(map.get(key).get("correct")+"");

                            if(mHome.user != null){

                                FirebaseDatabase.getInstance().getReference().child("questions-user")
                                        .child(mHome.user.getUid()).orderByKey().equalTo(question.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.getValue() == null){
                                            updateViewWithQuestion(question, view);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

            if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
                loadChallenge(view);
            }else{
                loadQuestion(view);

            }
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
        final Post post = createBasePost(map);

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

        Log.v("test", "Prueba");

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
        mSwipeRefreshing = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                    if (!flag_loading) {
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
            mSwipeRefreshing.setRefreshing(false);
            }
        });

        //Evento para esconder boton de nuevas publicaciones.
        mSwipeRefreshing.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
