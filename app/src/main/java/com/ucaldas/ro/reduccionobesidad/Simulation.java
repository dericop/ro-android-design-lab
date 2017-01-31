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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

    //Conteo de items de buenos habitos, medios, malos
    private final AtomicInteger goodHabitsCount = new AtomicInteger(0);
    private final AtomicInteger mediumHabitsCount = new AtomicInteger(0);
    private final AtomicInteger badHabitsCount = new AtomicInteger(0);



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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getGraphicalComponents(view); // Inicialización de componentes gráficos

        if(mHome.user != null){
            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("user-data").child(mHome.user.getUid());
            firebaseDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.v("DB", dataSnapshot.getValue().toString());

                    Map<String, HashMap> data = (HashMap) dataSnapshot.getValue();
                    for(String key: data.keySet()){
                        Map<String, String> post = data.get(key);
                        String activityType = post.get("category");

                        List<String> foodsCategories = Arrays.asList(getResources().getStringArray(R.array.new_post_food_categories));

                        if(foodsCategories.contains(activityType)){ //¿Es un alimento?
                            String result = post.get("result");
                            if(result != null){

                                int resultValue = Integer.parseInt(result);
                                switch (resultValue){
                                    case 1: //Indica mal hàbito
                                        badHabitsCount.incrementAndGet();
                                        break;
                                    case 2: //Indica hábito medio
                                        mediumHabitsCount.incrementAndGet();
                                        break;
                                    case 3: //Indica buen hábito
                                        goodHabitsCount.incrementAndGet();
                                        break;
                                }

                                healthyHabits.setText(goodHabitsCount+"");
                                mediumHabits.setText(mediumHabits+"");
                                badHabits.setText(badHabits+"");


                                /*//Cálculo para la tendencia en obesidad
                                String processedLevel = data.get("processed_level");
                                String sugarLevel = data.get("sugar_level");

                                if(processedLevel != null && sugarLevel != null){
                                    int processedValue = Integer.parseInt(processedLevel);
                                    int sugarValue = Integer.parseInt(sugarLevel);

                                }*/
                            }


                        }else{

                        }



                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }








    }

    private void getGraphicalComponents(View view){

        healthyHabits = (TextView) view.findViewById(R.id.healthyHabits);
        badHabits = (TextView) view.findViewById(R.id.mediumHabits);
        mediumHabits = (TextView) view.findViewById(R.id.mediumHabits);

        sim_circle_box_1 = (LinearLayout) view.findViewById(R.id.sim_circle_box_1);
        sim_circle_box_2 = (LinearLayout) view.findViewById(R.id.sim_circle_box_2);
        sim_circle_box_3 = (LinearLayout) view.findViewById(R.id.sim_circle_box_3);

        human_image_view = (ImageView) view.findViewById(R.id.human_image_view);

        low_weight_container = (LinearLayout) view.findViewById(R.id.low_weight_container);
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
