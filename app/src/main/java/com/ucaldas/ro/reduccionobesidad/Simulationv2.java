package com.ucaldas.ro.reduccionobesidad;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class Simulationv2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private SwipeRefreshLayout swiperefresh;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Firebase database
    DatabaseReference firebaseDatabase;
    DatabaseReference dbRef;
    DatabaseReference quaDBRef;

    double piAverage = 0;
    double aaAverage = 0;
    double gsAverage = 0;
    double chAverage = 0;
    double afAverage = 0;

    double piFrecuencies = 0;
    double aaFrecuencies = 0;
    double gsFrecuencies = 0;
    double chFrecuencies = 0;
    double afFrecuencies = 0;

    int countOfPi = 0;
    int countOfAa = 0;
    int countOfGs = 0;
    int countOfCh = 0;
    int countOfAF = 0;

    public View tView;

    public Simulationv2() {}

    public static Simulationv2 newInstance(String param1, String param2) {
        Simulationv2 fragment = new Simulationv2();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_simulationv2, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private boolean assignUserItemsDBReference() {
        if (mHome.user != null && firebaseDatabase != null) {
            if (WelcomeActivity.CURRENT_APP_VERSION.equals("A"))
                dbRef = firebaseDatabase.child("user-data").child(mHome.user.getUid());
            else
                dbRef = firebaseDatabase.child("user-data-reflexive").child(mHome.user.getUid());
            return true;
        }

        return false;
    }

    private boolean assignQualificationItemDBReference() {
        if (mHome.user != null && firebaseDatabase != null) {
            if (WelcomeActivity.CURRENT_APP_VERSION.equals("A"))
                quaDBRef = firebaseDatabase.child("user-posts");
            else
                quaDBRef = firebaseDatabase.child("user-posts-reflexive");
            return true;
        }

        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swiperefresh = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                restartData();
                loadData(view);
            }
        });


        tView = view;
        loadItems(view);
    }

    private void restartData() {
        piAverage = 0;
        aaAverage = 0;
        gsAverage = 0;
        chAverage = 0;
        afAverage = 0;

        piFrecuencies = 0;
        aaFrecuencies = 0;
        gsFrecuencies = 0;
        chFrecuencies = 0;
        afFrecuencies = 0;

        countOfPi = 0;
        countOfAa = 0;
        countOfGs = 0;
        countOfCh = 0;
        countOfAF = 0;

    }

    public void loadItems(final View view) {
        restartData();

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
                        loadData(view);

                    } else {
                        // AUser is signed out
                        Log.d("AUser", "onAuthStateChanged:signed_out");
                    }
                }
            };

            mAuth.addAuthStateListener(mAuthListener);

        } else {
            loadData(view);
        }
    }

    private void loadData(final View view) {

        if (mHome.user != null) {
            final String[] foodsString = getResources().getStringArray(R.array.new_post_food_categories);
            final List<String> foodsCategories = Arrays.asList(foodsString);
            final List<String> frecuencies = Arrays.asList(getResources().getStringArray(R.array.frecuencies));
            final List<String> frecuenciesCost = Arrays.asList(getResources().getStringArray(R.array.cost_frecuencies));

            if (WelcomeActivity.CURRENT_APP_VERSION.equals("A"))
                firebaseDatabase = FirebaseDatabase.getInstance().getReference();
            else
                firebaseDatabase = FirebaseDatabase.getInstance().getReference();

            if (assignUserItemsDBReference() && assignQualificationItemDBReference()) {

                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final Map<String, HashMap> data = (HashMap) dataSnapshot.getValue();
                        if (data != null) {

                            final LinkedList<String> keys = new LinkedList();
                            keys.addAll(data.keySet());
                            final AtomicInteger countOfElements = new AtomicInteger(0);


                            for (final String key : keys) {
                                Map<String, Object> post = data.get(key);

                                quaDBRef.child(post.get("id") + "").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            Post post = dataSnapshot.getValue(Post.class);
                                            countOfElements.incrementAndGet();
                                            if (post.getResult() != 0) {

                                                calculatePostQualification(post, frecuenciesCost, frecuencies, foodsCategories);

                                                Log.v("Total", countOfElements.get()+" "+(keys.size() - 1)+"");

                                                if (countOfElements.get() == (keys.size() - 1)) {

                                                    updateViewsAndRestarData(view);
                                                    swiperefresh.setRefreshing(false);
                                                }
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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

            }

        }

    }

    private void calculatePostQualification(Post post, List<String> frecuenciesCost, List<String> frecuencies, List<String> foodsCategories) {

        long frecuency = Integer.parseInt(frecuenciesCost.get(frecuencies.indexOf(post.getFrecuency())));

        if (!foodsCategories.contains(post.getCategory())) {
            if (post.getAverage() != 0) {
                int average = (int) post.getAverage();

                countOfAF++;
                afFrecuencies += frecuency;

                switch (average) {
                    case 1:
                        average = 10;
                        break;
                    case 2:
                        average = 9;
                        break;
                    case 3:
                        average = 8;
                        break;
                    case 4:
                        average = 7;
                        break;
                    case 5:
                        average = 6;
                        break;
                    case 6:
                        average = 5;
                        break;
                    case 7:
                        average = 4;
                        break;
                    case 8:
                        average = 3;
                        break;
                    case 9:
                        average = 2;
                        break;
                    case 10:
                        average = 1;
                        break;
                }

                afAverage += average * frecuency;
            }

        } else {
            if (post.getR_pi() != 0) {
                piAverage += Double.parseDouble(post.getR_pi() + "") * frecuency;
                countOfPi++;
                piFrecuencies += frecuency;
            }

            if (post.getR_aa() != 0) {
                aaAverage += Double.parseDouble(post.getR_aa() + "") * frecuency;
                countOfAa++;
                aaFrecuencies += frecuency;
            }

            if (post.getR_gs() != 0) {
                gsAverage += Double.parseDouble(post.getR_gs() + "") * frecuency;
                countOfGs++;
                gsFrecuencies += frecuency;
            }

            if (post.getR_ch() != 0) {
                chAverage += Double.parseDouble(post.getR_ch() + "") * frecuency;
                countOfCh++;
                chFrecuencies += frecuency;
            }
        }
    }

    private void updateViewsAndRestarData(View view) {

        if (piFrecuencies != 0)
            piAverage /= piFrecuencies;

        if (aaFrecuencies != 0)
            aaAverage /= aaFrecuencies;

        if (gsFrecuencies != 0)
            gsAverage /= gsFrecuencies;

        if (chFrecuencies != 0)
            chAverage /= chFrecuencies;

        if (afFrecuencies != 0)
            afAverage /= afFrecuencies;

        updatePiViews(view, countOfPi, piAverage);
        updateAAViews(view, countOfAa, aaAverage);
        updateGSViews(view, countOfGs, gsAverage);
        updateCHViews(view, countOfCh, chAverage);
        updateAFViews(view, countOfAF, afAverage);

        restartData();
    }

    private void updatePiViews(View view, int countOfPi, double piAverage) {

        final View pi_without = view.findViewById(R.id.pi_without);
        final View pi_bottom = view.findViewById(R.id.pi_bottom);
        final View pi_bottom_medium = view.findViewById(R.id.pi_bottom_medium);
        final View pi_medium = view.findViewById(R.id.pi_medium);
        final View pi_medium_top = view.findViewById(R.id.pi_medium_top);
        final View pi_top = view.findViewById(R.id.pi_top);

        pi_without.setVisibility(View.INVISIBLE);
        pi_bottom.setVisibility(View.INVISIBLE);
        pi_bottom_medium.setVisibility(View.INVISIBLE);
        pi_medium.setVisibility(View.INVISIBLE);
        pi_medium_top.setVisibility(View.INVISIBLE);
        pi_top.setVisibility(View.INVISIBLE);

        if (piAverage == 0) {
            pi_without.setVisibility(View.VISIBLE);

        } else if (piAverage != 0 && piAverage <= 2) {
            pi_bottom.setVisibility(View.VISIBLE);

        } else if (piAverage > 2 && piAverage <= 4) {
            pi_bottom_medium.setVisibility(View.VISIBLE);

        } else if (piAverage > 4 && piAverage <= 6) {
            pi_medium.setVisibility(View.VISIBLE);

        } else if (piAverage > 6 && piAverage <= 8) {
            pi_medium_top.setVisibility(View.VISIBLE);

        } else if (piAverage > 8) {
            pi_top.setVisibility(View.VISIBLE);
        }

    }

    private void updateAAViews(View view, int countOfAA, double aaAverage) {

        final View aa_without = view.findViewById(R.id.aa_without);
        final View aa_bottom = view.findViewById(R.id.aa_bottom);
        final View aa_bottom_medium = view.findViewById(R.id.aa_bottom_medium);
        final View aa_medium = view.findViewById(R.id.aa_medium);
        final View aa_medium_top = view.findViewById(R.id.aa_medium_top);
        final View aa_top = view.findViewById(R.id.aa_top);

        aa_without.setVisibility(View.INVISIBLE);
        aa_bottom.setVisibility(View.INVISIBLE);
        aa_bottom_medium.setVisibility(View.INVISIBLE);
        aa_medium.setVisibility(View.INVISIBLE);
        aa_medium_top.setVisibility(View.INVISIBLE);
        aa_top.setVisibility(View.INVISIBLE);

        if (aaAverage == 0) {
            aa_without.setVisibility(View.VISIBLE);

        } else if (aaAverage != 0 && aaAverage <= 0.6) {
            aa_bottom.setVisibility(View.VISIBLE);

        } else if (aaAverage > 0.6 && aaAverage <= 1.2) {
            aa_bottom_medium.setVisibility(View.VISIBLE);

        } else if (aaAverage > 1.2 && aaAverage <= 1.8) {
            aa_medium.setVisibility(View.VISIBLE);

        } else if (aaAverage > 1.8 && aaAverage <= 2.4) {
            aa_medium_top.setVisibility(View.VISIBLE);

        } else if (aaAverage > 2.4) {
            aa_top.setVisibility(View.VISIBLE);
        }


    }

    private void updateGSViews(View view, int countOfGS, double gsAverage) {

        final View gs_without = view.findViewById(R.id.gs_without);
        final View gs_bottom = view.findViewById(R.id.gs_bottom);
        final View gs_bottom_medium = view.findViewById(R.id.gs_bottom_medium);
        final View gs_medium = view.findViewById(R.id.gs_medium);
        final View gs_medium_top = view.findViewById(R.id.gs_medium_top);
        final View gs_top = view.findViewById(R.id.gs_top);

        gs_without.setVisibility(View.INVISIBLE);
        gs_bottom.setVisibility(View.INVISIBLE);
        gs_bottom_medium.setVisibility(View.INVISIBLE);
        gs_medium.setVisibility(View.INVISIBLE);
        gs_medium_top.setVisibility(View.INVISIBLE);
        gs_top.setVisibility(View.INVISIBLE);


        if (gsAverage == 0) {
            gs_without.setVisibility(View.VISIBLE);

        } else if (gsAverage != 0 && gsAverage <= 0.6) {
            gs_bottom.setVisibility(View.VISIBLE);

        } else if (gsAverage > 0.6 && gsAverage <= 1.2) {
            gs_bottom_medium.setVisibility(View.VISIBLE);

        } else if (gsAverage > 1.2 && gsAverage <= 1.8) {
            gs_medium.setVisibility(View.VISIBLE);

        } else if (gsAverage > 1.8 && gsAverage <= 2.4) {
            gs_medium_top.setVisibility(View.VISIBLE);

        } else if (gsAverage > 2.4) {
            gs_top.setVisibility(View.VISIBLE);
        }

    }

    private void updateCHViews(View view, int countOfCH, double chAverage) {

        final View ch_without = view.findViewById(R.id.ch_without);
        final View ch_bottom = view.findViewById(R.id.ch_bottom);
        final View ch_bottom_medium = view.findViewById(R.id.ch_bottom_medium);
        final View ch_medium = view.findViewById(R.id.ch_medium);
        final View ch_medium_top = view.findViewById(R.id.ch_medium_top);
        final View ch_top = view.findViewById(R.id.ch_top);

        ch_without.setVisibility(View.INVISIBLE);
        ch_bottom.setVisibility(View.INVISIBLE);
        ch_bottom_medium.setVisibility(View.INVISIBLE);
        ch_medium.setVisibility(View.INVISIBLE);
        ch_medium_top.setVisibility(View.INVISIBLE);
        ch_top.setVisibility(View.INVISIBLE);


        if (chAverage == 0) {
            ch_without.setVisibility(View.VISIBLE);

        } else if (chAverage != 0 && chAverage <= 0.6) {
            ch_bottom.setVisibility(View.VISIBLE);

        } else if (chAverage > 0.6 && chAverage <= 1.2) {
            ch_bottom_medium.setVisibility(View.VISIBLE);

        } else if (chAverage > 1.2 && chAverage <= 1.8) {
            ch_medium.setVisibility(View.VISIBLE);

        } else if (chAverage > 1.8 && chAverage <= 2.4) {
            ch_medium_top.setVisibility(View.VISIBLE);

        } else if (chAverage > 2.4) {
            ch_top.setVisibility(View.VISIBLE);
        }

    }

    private void updateAFViews(View view, int countOfAF, double afAverage) {
        final View af_without = view.findViewById(R.id.af_without);
        final View af_bottom = view.findViewById(R.id.af_bottom);
        final View af_bottom_medium = view.findViewById(R.id.af_bottom_medium);
        final View af_medium = view.findViewById(R.id.af_medium);
        final View af_medium_top = view.findViewById(R.id.af_medium_top);
        final View af_top = view.findViewById(R.id.af_top);

        af_without.setVisibility(View.INVISIBLE);
        af_bottom.setVisibility(View.INVISIBLE);
        af_bottom_medium.setVisibility(View.INVISIBLE);
        af_medium.setVisibility(View.INVISIBLE);
        af_medium_top.setVisibility(View.INVISIBLE);
        af_top.setVisibility(View.INVISIBLE);

        if (afAverage == 0) {
            af_without.setVisibility(View.VISIBLE);

        } else if (afAverage > 0 && afAverage <= 2) {
            af_bottom.setVisibility(View.VISIBLE);

        } else if (afAverage > 2 && afAverage <= 4) {
            af_bottom_medium.setVisibility(View.VISIBLE);

        } else if (afAverage > 4 && afAverage <= 6) {
            af_medium.setVisibility(View.VISIBLE);

        } else if (afAverage > 6 && afAverage <= 8) {
            af_medium_top.setVisibility(View.VISIBLE);

        } else if (afAverage > 8) {
            af_top.setVisibility(View.VISIBLE);
        }

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
