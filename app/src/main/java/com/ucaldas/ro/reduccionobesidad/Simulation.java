package com.ucaldas.ro.reduccionobesidad;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    //Dialogo de progreso
    private ProgressDialog progress;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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



    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getGraphicalComponents(view); // Inicialización de componentes gráficos

        if(mHome.user != null){
            final String[] foodsString = getResources().getStringArray(R.array.new_post_food_categories);
            final List<String> foodsCategories = Arrays.asList(foodsString);
            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("user-data").child(mHome.user.getUid());
            final AtomicInteger countOfHealthy = new AtomicInteger(1);
            final AtomicInteger countOfMedium = new AtomicInteger(1);
            final AtomicInteger countOfBad = new AtomicInteger(1);

            firebaseDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    /*progress = ProgressDialog.show(getContext(), "Creando simulación...",
                            "Espera un momento", true);*/

                    Log.v("Simulation", "La simulación ha cambiado");

                    double totalAverage = 0;
                    double goodHabitsAverage = 1;
                    double mediumHabitsAverage = 1;
                    double badHabitsAverages = 1;

                    int sumOfFrecuencies = 0;

                    List<String> frecuencies = Arrays.asList(getResources().getStringArray(R.array.frecuencies));
                    List<String> frecuenciesCost = Arrays.asList(getResources().getStringArray(R.array.cost_frecuencies));

                    Map<String, HashMap> data = (HashMap) dataSnapshot.getValue();
                    if(data != null){
                        for(String key: data.keySet()){

                            Map<String, Object> post = data.get(key);

                            //String activityType = post.get("category")+"";
                            //foodsCategories.contains(activityType) &&
                            if(post.get("result") != null){
                                long result = (long)post.get("result");
                                long frecuency = Integer.parseInt(frecuenciesCost.get(frecuencies.indexOf(post.get("frecuency"))));
                                Log.v("Simulation",frecuency+"");
                                sumOfFrecuencies+=frecuency;

                                double averagePon = Double.parseDouble(post.get("average")+"")*frecuency;

                                if(result == 3){
                                    goodHabitsAverage += averagePon;
                                    countOfHealthy.incrementAndGet();
                                }else if(result == 2){
                                    mediumHabitsAverage += averagePon;
                                    countOfMedium.incrementAndGet();
                                }else{
                                    badHabitsAverages += averagePon;
                                    countOfBad.incrementAndGet();
                                }

                                if(post.get("average") != null) {
                                    try {
                                        totalAverage += (long) post.get("average")*frecuency;
                                    }catch (NumberFormatException ex){
                                        totalAverage += 0.0;
                                    }
                                }
                            }
                        }

                        goodHabitsAverage =  goodHabitsAverage/countOfHealthy.get();
                        mediumHabitsAverage = mediumHabitsAverage/countOfMedium.get();
                        badHabitsAverages = badHabitsAverages/countOfBad.get();

                        Log.v("Simulation", goodHabitsAverage+" Good");
                        Log.v("Simulation", mediumHabitsAverage+" Medium");
                        Log.v("Simulation", badHabitsAverages+" Bad");

                        //Lógica para la sección superior de la interface

                        healthyHabits.setText(countOfHealthy.get()+"");
                        mediumHabits.setText(countOfMedium.get()+"");
                        badHabits.setText(countOfBad.get()+"");

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
                        if(orderedList.indexOf(newSizeBox1) == 2){
                            newSizeBox1 = 80;

                        } else if(orderedList.indexOf(newSizeBox1) == 1)
                            newSizeBox1 = 60;
                        else
                            newSizeBox1 = 40;

                        //Ubicación del tamaño para el componente 2
                        if(orderedList.indexOf(newSizeBox2) == 2){
                            newSizeBox2 = 80;

                        } else if(orderedList.indexOf(newSizeBox2) == 1)
                            newSizeBox2 = 60;
                        else
                            newSizeBox2 = 40;

                        //Ubicación del tamaño para el componente 3
                        if(orderedList.indexOf(newSizeBox3) == 2){
                            newSizeBox3 = 80;

                        }else if(orderedList.indexOf(newSizeBox3) == 1)
                            newSizeBox3 = 60;
                        else
                            newSizeBox3 = 40;

                        if(getActivity() != null){
                            healthyHabits.setTextSize((newSizeBox1 * 14)/40);
                            mediumHabits.setTextSize((newSizeBox2 * 14)/40);
                            badHabits.setTextSize((newSizeBox3 * 14)/40);

                            circleBox1Params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSizeBox1, getResources().getDisplayMetrics());
                            circleBox1Params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSizeBox1, getResources().getDisplayMetrics());;
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

                        countOfHealthy.set(0);
                        countOfMedium.set(0);
                        countOfBad.set(0);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }
    }

    private void paintLevel(double average, View view){
        final RelativeLayout humanContainer = (RelativeLayout) view.findViewById(R.id.human_container);
        final ImageView humanImageView = (ImageView) view.findViewById(R.id.human_image_view);

        restartDefaultView(severe_obesity_container);
        restartDefaultView(obesity_level_2_container);
        restartDefaultView(obesity_level_1_container);
        restartDefaultView(overweight_container);
        restartDefaultView(normal_weight_container);



        if(average!=0 && average <= 2){

            Log.v("DBO", "Peso normal");
            normal_weight_container.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.normal_weight));
            normal_weight_container.getChildAt(1).setBackgroundColor(getResources().getColor(R.color.normal_weight));
            normal_weight_container.getChildAt(2).setBackgroundColor(getResources().getColor(R.color.normal_weight));

            humanContainer.setBackgroundColor(getResources().getColor(R.color.normal_weight));
            humanImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.silueta_humano_2));

        }else if(average > 2 && average <=4){

            overweight_container.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.overweight));
            overweight_container.getChildAt(1).setBackgroundColor(getResources().getColor(R.color.overweight));
            overweight_container.getChildAt(2).setBackgroundColor(getResources().getColor(R.color.overweight));

            humanContainer.setBackgroundColor(getResources().getColor(R.color.overweight));
            humanImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.silueta_humano_3));
            Log.v("DBO", "Obesidad II");


        }else if(average >4 && average <=6){
            Log.v("DBO", "Obesidad I");

            obesity_level_1_container.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.obesity_1));
            obesity_level_1_container.getChildAt(1).setBackgroundColor(getResources().getColor(R.color.obesity_1));
            obesity_level_1_container.getChildAt(2).setBackgroundColor(getResources().getColor(R.color.obesity_1));

            humanContainer.setBackgroundColor(getResources().getColor(R.color.obesity_1));
            humanImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.silueta_humano_4));

        }else if(average > 6 && average <= 8){

            Log.v("DBO", "Soprepeso");

            obesity_level_2_container.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.obesity_2));
            obesity_level_2_container.getChildAt(1).setBackgroundColor(getResources().getColor(R.color.obesity_2));
            obesity_level_2_container.getChildAt(2).setBackgroundColor(getResources().getColor(R.color.obesity_2));

            humanContainer.setBackgroundColor(getResources().getColor(R.color.obesity_2));
            humanImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.silueta_humano_5));

        }else if(average > 8){
            Log.v("DBO", "Obesidad severa");
            severe_obesity_container.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.severe_obesity));
            severe_obesity_container.getChildAt(1).setBackgroundColor(getResources().getColor(R.color.severe_obesity));
            severe_obesity_container.getChildAt(2).setBackgroundColor(getResources().getColor(R.color.severe_obesity));

            humanContainer.setBackgroundColor(getResources().getColor(R.color.severe_obesity));
            humanImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.silueta_humano_6));
        }
    }

    private void restartDefaultView(LinearLayout view){
        view.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.simuation_barItem_normal));
        view.getChildAt(1).setBackgroundColor(getResources().getColor(R.color.simuation_barItem_normal));
        view.getChildAt(2).setBackgroundColor(getResources().getColor(R.color.simuation_barItem_normal));
    }

    private void getGraphicalComponents(View view){

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
