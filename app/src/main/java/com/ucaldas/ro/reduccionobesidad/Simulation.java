package com.ucaldas.ro.reduccionobesidad;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Simulation.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Simulation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Simulation extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Elementos gráficos a actualizar

    //Textos
    private TextView healthyHabits;
    private TextView mediumHabits;
    private TextView badHabits;

    //Contenedores ciculares
    private LinearLayout sim_circle_box_1;
    private LinearLayout sim_circle_box_2;
    private LinearLayout sim_circle_box_3;

    //Image del humano a simular
    private ImageView human_image_view;

    //Medidores de tendencia en obesidad
    private LinearLayout low_weight_container;
    private LinearLayout normal_weight_container;
    private LinearLayout overweight_container;
    private LinearLayout obesity_level_1_container;
    private LinearLayout obesity_level_2_container;
    private LinearLayout severe_obesity_container;

    private SwipeRefreshLayout swiperefresh;

    //Dialogo de progreso
    private ProgressDialog progress;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Firebase database
    DatabaseReference firebaseDatabase;
    DatabaseReference dbRef;
    DatabaseReference quaDBRef;

    Double totalAverage = 0.0;
    Double goodHabitsAverage = 1.0;
    Double mediumHabitsAverage = 1.0;
    Double badHabitsAverages = 1.0;

    public Simulation() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Simulation.
     */
    // TODO: Rename and change types and number of parameters
    public static Simulation newInstance(String param1, String param2) {
        Simulation fragment = new Simulation();
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
        return inflater.inflate(R.layout.fragment_simulation, container, false);
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
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getGraphicalComponents(view); // Inicialización de componentes gráficos
        loadItems(view);
    }

    private void loadItems(final View view){
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

    private void loadData(final View view){
        if (mHome.user != null) {
            final String[] foodsString = getResources().getStringArray(R.array.new_post_food_categories);
            final List<String> foodsCategories = Arrays.asList(foodsString);

            firebaseDatabase = FirebaseDatabase.getInstance().getReference();
            if (assignUserItemsDBReference() && assignQualificationItemDBReference()) {
                final AtomicInteger countOfHealthy = new AtomicInteger(1);
                final AtomicInteger countOfMedium = new AtomicInteger(1);
                final AtomicInteger countOfBad = new AtomicInteger(1);

                final List<String> frecuencies = Arrays.asList(getResources().getStringArray(R.array.frecuencies));
                final List<String> frecuenciesCost = Arrays.asList(getResources().getStringArray(R.array.cost_frecuencies));

                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final AtomicInteger sumOfFrecuencies = new AtomicInteger(0);
                        totalAverage = 0.0;
                        goodHabitsAverage = 1.0;
                        mediumHabitsAverage = 1.0;
                        badHabitsAverages = 1.0;

                        Map<String, HashMap> data = (HashMap) dataSnapshot.getValue();
                        if (data != null) {

                            final LinkedList<String> keys = new LinkedList();
                            keys.addAll(data.keySet());

                            for (final String key : keys) {

                                Map<String, Object> post = data.get(key);

                                quaDBRef.child(post.get("id") + "").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            Post post = dataSnapshot.getValue(Post.class);
                                            if (post.getResult() != 0) {
                                                calculatePostQualification(post, frecuenciesCost, frecuencies, sumOfFrecuencies, countOfHealthy, countOfMedium, countOfBad);
                                                if (keys.indexOf(key) == (keys.size() - 1)){
                                                    updateViewsAndRestarData(countOfHealthy, countOfMedium, countOfBad, view, sumOfFrecuencies);
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

    private void updateViewsAndRestarData(AtomicInteger countOfHealthy, AtomicInteger countOfMedium, AtomicInteger countOfBad, View view, AtomicInteger sumOfFrecuencies) {
        goodHabitsAverage = goodHabitsAverage / countOfHealthy.get();
        mediumHabitsAverage = mediumHabitsAverage / countOfMedium.get();
        badHabitsAverages = badHabitsAverages / countOfBad.get();

        updateCircleBoxSize(goodHabitsAverage, mediumHabitsAverage, badHabitsAverages, view, totalAverage, sumOfFrecuencies.get());

        countOfHealthy.set(0);
        countOfMedium.set(0);
        countOfBad.set(0);
    }

    private void calculatePostQualification(Post post, List<String> frecuenciesCost, List<String> frecuencies, AtomicInteger sumOfFrecuencies,
                                            AtomicInteger countOfHealthy, AtomicInteger countOfMedium, AtomicInteger countOfBad) {
        long result = post.getResult();
        long frecuency = Integer.parseInt(frecuenciesCost.get(frecuencies.indexOf(post.getFrecuency())));

        sumOfFrecuencies.addAndGet((int) frecuency);

        double averagePon = post.getAverage() * frecuency;

        if (result == 3) {
            goodHabitsAverage += averagePon;
            countOfHealthy.incrementAndGet();
        } else if (result == 2) {
            mediumHabitsAverage += averagePon;
            countOfMedium.incrementAndGet();
        } else {
            badHabitsAverages += averagePon;
            countOfBad.incrementAndGet();
        }

        try {
            totalAverage += post.getAverage() * frecuency;
        } catch (NumberFormatException ex) {
            totalAverage += 0.0;
        }
    }

    private void updateCircleBoxSize(double goodHabitsAverage, double mediumHabitsAverage, double badHabitsAverages,
                                     View view, double totalAverage, int sumOfFrecuencies) {

        ViewGroup.LayoutParams circleBox1Params = sim_circle_box_1.getLayoutParams();
        ViewGroup.LayoutParams circleBox2Params = sim_circle_box_2.getLayoutParams();
        ViewGroup.LayoutParams circleBox3Params = sim_circle_box_3.getLayoutParams();

        int newSizeBox1 = ((int) goodHabitsAverage);
        int newSizeBox2 = ((int) mediumHabitsAverage);
        int newSizeBox3 = ((int) badHabitsAverages);

        LinkedList orderedList = new LinkedList();
        orderedList.push(newSizeBox1);
        orderedList.push(newSizeBox2);
        orderedList.push(newSizeBox3);

        Collections.sort(orderedList);

        //Ubicación del tamaño para el componente 1
        if (orderedList.indexOf(newSizeBox1) == 2) {
            newSizeBox1 = 80;

        } else if (orderedList.indexOf(newSizeBox1) == 1)
            newSizeBox1 = 60;
        else
            newSizeBox1 = 40;

        //Ubicación del tamaño para el componente 2
        if (orderedList.indexOf(newSizeBox2) == 2) {
            newSizeBox2 = 80;

        } else if (orderedList.indexOf(newSizeBox2) == 1)
            newSizeBox2 = 60;
        else
            newSizeBox2 = 40;

        //Ubicación del tamaño para el componente 3
        if (orderedList.indexOf(newSizeBox3) == 2) {
            newSizeBox3 = 80;

        } else if (orderedList.indexOf(newSizeBox3) == 1)
            newSizeBox3 = 60;
        else
            newSizeBox3 = 40;

        if (getActivity() != null) {
            healthyHabits.setTextSize((newSizeBox1 * 14) / 40);
            mediumHabits.setTextSize((newSizeBox2 * 14) / 40);
            badHabits.setTextSize((newSizeBox3 * 14) / 40);

            circleBox1Params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSizeBox1, getResources().getDisplayMetrics());
            circleBox1Params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSizeBox1, getResources().getDisplayMetrics());
            ;
            sim_circle_box_1.setLayoutParams(circleBox1Params);

            circleBox2Params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSizeBox2, getResources().getDisplayMetrics());
            circleBox2Params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSizeBox2, getResources().getDisplayMetrics());
            sim_circle_box_2.setLayoutParams(circleBox2Params);

            circleBox3Params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSizeBox3, getResources().getDisplayMetrics());
            circleBox3Params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSizeBox3, getResources().getDisplayMetrics());
            sim_circle_box_3.setLayoutParams(circleBox3Params);

            totalAverage /= sumOfFrecuencies;

            paintLevel(totalAverage, view);
        }
    }

    private void paintLevel(double average, View view) {
        final RelativeLayout humanContainer = (RelativeLayout) view.findViewById(R.id.human_container);
        final ImageView humanImageView = (ImageView) view.findViewById(R.id.human_image_view);

        restartDefaultView(severe_obesity_container);
        restartDefaultView(obesity_level_2_container);
        restartDefaultView(obesity_level_1_container);
        restartDefaultView(overweight_container);
        restartDefaultView(normal_weight_container);

        if (average != 0 && average <= 2) {

            normal_weight_container.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.normal_weight));
            normal_weight_container.getChildAt(1).setBackgroundColor(getResources().getColor(R.color.normal_weight));
            normal_weight_container.getChildAt(2).setBackgroundColor(getResources().getColor(R.color.normal_weight));

            humanContainer.setBackgroundColor(getResources().getColor(R.color.normal_weight));
            humanImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.silueta_humano_2));

        } else if (average > 2 && average <= 4) {

            overweight_container.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.overweight));
            overweight_container.getChildAt(1).setBackgroundColor(getResources().getColor(R.color.overweight));
            overweight_container.getChildAt(2).setBackgroundColor(getResources().getColor(R.color.overweight));

            humanContainer.setBackgroundColor(getResources().getColor(R.color.overweight));
            humanImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.silueta_humano_3));


        } else if (average > 4 && average <= 6) {

            obesity_level_1_container.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.obesity_1));
            obesity_level_1_container.getChildAt(1).setBackgroundColor(getResources().getColor(R.color.obesity_1));
            obesity_level_1_container.getChildAt(2).setBackgroundColor(getResources().getColor(R.color.obesity_1));

            humanContainer.setBackgroundColor(getResources().getColor(R.color.obesity_1));
            humanImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.silueta_humano_4));

        } else if (average > 6 && average <= 8) {

            obesity_level_2_container.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.obesity_2));
            obesity_level_2_container.getChildAt(1).setBackgroundColor(getResources().getColor(R.color.obesity_2));
            obesity_level_2_container.getChildAt(2).setBackgroundColor(getResources().getColor(R.color.obesity_2));

            humanContainer.setBackgroundColor(getResources().getColor(R.color.obesity_2));
            humanImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.silueta_humano_5));

        } else if (average > 8) {
            severe_obesity_container.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.severe_obesity));
            severe_obesity_container.getChildAt(1).setBackgroundColor(getResources().getColor(R.color.severe_obesity));
            severe_obesity_container.getChildAt(2).setBackgroundColor(getResources().getColor(R.color.severe_obesity));

            humanContainer.setBackgroundColor(getResources().getColor(R.color.severe_obesity));
            humanImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.silueta_humano_6));
        }
    }

    private void restartDefaultView(LinearLayout view) {
        view.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.simuation_barItem_normal));
        view.getChildAt(1).setBackgroundColor(getResources().getColor(R.color.simuation_barItem_normal));
        view.getChildAt(2).setBackgroundColor(getResources().getColor(R.color.simuation_barItem_normal));
    }

    private void getGraphicalComponents(final View view) {

        healthyHabits = (TextView) view.findViewById(R.id.healthyHabits);
        badHabits = (TextView) view.findViewById(R.id.bad_habits);
        mediumHabits = (TextView) view.findViewById(R.id.mediumHabits);

        sim_circle_box_1 = (LinearLayout) view.findViewById(R.id.sim_circle_box_1);
        sim_circle_box_2 = (LinearLayout) view.findViewById(R.id.sim_circle_box_2);
        sim_circle_box_3 = (LinearLayout) view.findViewById(R.id.sim_circle_box_3);

        human_image_view = (ImageView) view.findViewById(R.id.human_image_view);

        //low_weight_container = (LinearLayout) view.findViewById(R.id.low_weight_container);
        normal_weight_container = (LinearLayout) view.findViewById(R.id.normal_weight_container);
        overweight_container = (LinearLayout) view.findViewById(R.id.overweight_container);
        obesity_level_1_container = (LinearLayout) view.findViewById(R.id.obesity_level_1_container);
        obesity_level_2_container = (LinearLayout) view.findViewById(R.id.obesity_level_2_container);
        severe_obesity_container = (LinearLayout) view.findViewById(R.id.severe_obesity_container);

        swiperefresh = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                 totalAverage = 0.0;
                 goodHabitsAverage = 1.0;
                 mediumHabitsAverage = 1.0;
                 badHabitsAverages = 1.0;

                loadData(view);
            }
        });
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
