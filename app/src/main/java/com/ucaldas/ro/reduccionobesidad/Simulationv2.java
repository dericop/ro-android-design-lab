package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Simulationv2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Simulationv2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Simulationv2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Simulationv2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Simulationv2.
     */
    // TODO: Rename and change types and number of parameters
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
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

    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(mHome.user != null){
            final String[] foodsString = getResources().getStringArray(R.array.new_post_food_categories);
            final List<String> foodsCategories = Arrays.asList(foodsString);
            DatabaseReference firebaseDatabase = null;

            if(WelcomeActivity.CURRENT_APP_VERSION.equals("A"))
                firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("user-data").child(mHome.user.getUid());
            else
                firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("user-data-reflexive").child(mHome.user.getUid());


            firebaseDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Map<String, HashMap> data = (HashMap) dataSnapshot.getValue();
                    if(data != null){
                        double piAverage = 0;
                        double aaAverage = 0;
                        double gsAverage = 0;
                        double chAverage = 0;
                        double afAverage = 0;

                        double piFrecuencies = 1;
                        double aaFrecuencies = 1;
                        double gsFrecuencies = 1;
                        double chFrecuencies = 1;
                        double afFrecuencies = 1;

                        int countOfPi = 0;
                        int countOfAa = 0;
                        int countOfGs = 0;
                        int countOfCh = 0;
                        int countOfAF = 0;

                        List<String> frecuencies = Arrays.asList(getResources().getStringArray(R.array.frecuencies));
                        List<String> frecuenciesCost = Arrays.asList(getResources().getStringArray(R.array.cost_frecuencies));
                        for(String key: data.keySet()){

                            Map<String, Object> post = data.get(key);
                            if(post.get("result")!=null){
                                long frecuency = Integer.parseInt(frecuenciesCost.get(frecuencies.indexOf(post.get("frecuency"))));

                                if(!foodsCategories.contains(post.get("category")+"")){
                                    if(post.get("average") != null){
                                        afAverage += Double.parseDouble(post.get("average")+"")*frecuency;
                                        countOfAF++;
                                        afFrecuencies +=frecuency;
                                    }

                                }else{
                                    if(post.get("r_pi") != null){
                                        piAverage+=Double.parseDouble(post.get("r_pi")+"")*frecuency;
                                        countOfPi++;
                                        aaFrecuencies +=frecuency;
                                    }

                                    if(post.get("r_aa") != null){
                                        aaAverage+=Double.parseDouble(post.get("r_aa")+"")*frecuency;
                                        countOfAa++;
                                        piFrecuencies +=frecuency;
                                    }

                                    if(post.get("r_gs") != null){
                                        gsAverage+=Double.parseDouble(post.get("r_gs")+"")*frecuency;
                                        countOfGs++;
                                        gsFrecuencies +=frecuency;
                                    }

                                    if(post.get("r_ch") != null){
                                        chAverage+=Double.parseDouble(post.get("r_ch")+"")*frecuency;
                                        countOfCh++;
                                        chFrecuencies += frecuency;
                                    }
                                }
                            }
                        }

                        Log.v("Simulation", aaAverage+"");
                        Log.v("Simulation", aaFrecuencies+"");

                        piAverage /= piFrecuencies;

                        aaAverage /= aaFrecuencies;
                        gsAverage /= gsFrecuencies;
                        chAverage /= chFrecuencies;
                        afAverage /= afFrecuencies;

                        updatePiViews(view, countOfPi, piAverage);
                        updateAAViews(view, countOfAa, aaAverage);
                        updateGSViews(view, countOfGs, gsAverage);
                        updateCHViews(view, countOfCh, chAverage);
                        updateAFViews(view, countOfAF, afAverage);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }

    }

    private void updatePiViews(View view, int countOfPi, double piAverage){

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


        if(piAverage == 0){
            pi_without.setVisibility(View.VISIBLE);

        }else if(piAverage!=0 && piAverage <= 2){
            pi_bottom.setVisibility(View.VISIBLE);

        }else if(piAverage > 2 && piAverage <=4){
            pi_bottom_medium.setVisibility(View.VISIBLE);

        }else if(piAverage >4 && piAverage <=6){
            pi_medium.setVisibility(View.VISIBLE);

        }else if(piAverage > 6 && piAverage <= 8){
            pi_medium_top.setVisibility(View.VISIBLE);

        }else if(piAverage > 8){
            pi_top.setVisibility(View.VISIBLE);
        }

    }

    private void updateAAViews(View view, int countOfAA, double aaAverage){

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

        if(aaAverage == 0){
            aa_without.setVisibility(View.VISIBLE);

        }else if(aaAverage!=0 && aaAverage <= 2){
            aa_bottom.setVisibility(View.VISIBLE);

        }else if(aaAverage > 2 && aaAverage <=4){
            aa_bottom_medium.setVisibility(View.VISIBLE);

        }else if(aaAverage >4 && aaAverage <=6){
            aa_medium.setVisibility(View.VISIBLE);

        }else if(aaAverage > 6 && aaAverage <= 8){
            aa_medium_top.setVisibility(View.VISIBLE);

        }else if(aaAverage > 8){
            aa_top.setVisibility(View.VISIBLE);
        }


    }

    private void updateGSViews(View view, int countOfGS, double gsAverage){

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


        if(gsAverage == 0){
            gs_without.setVisibility(View.VISIBLE);

        }else if(gsAverage!=0 && gsAverage <= 2){
            gs_bottom.setVisibility(View.VISIBLE);

        }else if(gsAverage > 2 && gsAverage <=4){
            gs_bottom_medium.setVisibility(View.VISIBLE);

        }else if(gsAverage >4 && gsAverage <=6){
            gs_medium.setVisibility(View.VISIBLE);

        }else if(gsAverage > 6 && gsAverage <= 8){
            gs_medium_top.setVisibility(View.VISIBLE);

        }else if(gsAverage > 8){
            gs_top.setVisibility(View.VISIBLE);
        }

    }

    private void updateCHViews(View view, int countOfCH, double chAverage){

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


        if(chAverage == 0){
            ch_without.setVisibility(View.VISIBLE);

        }else if(chAverage!=0 && chAverage <= 2){
            ch_bottom.setVisibility(View.VISIBLE);

        }else if(chAverage > 2 && chAverage <=4){
            ch_bottom_medium.setVisibility(View.VISIBLE);

        }else if(chAverage >4 && chAverage <=6){
            ch_medium.setVisibility(View.VISIBLE);

        }else if(chAverage > 6 && chAverage <= 8){
            ch_medium_top.setVisibility(View.VISIBLE);

        }else if(chAverage > 8){
            ch_top.setVisibility(View.VISIBLE);
        }

    }

    private void updateAFViews(View view, int countOfAF, double afAverage){
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

        if(afAverage == 0){
            af_without.setVisibility(View.VISIBLE);

        }else if(afAverage!=0 && afAverage <= 2){
            af_bottom.setVisibility(View.VISIBLE);

        }else if(afAverage > 2 && afAverage <=4){
            af_bottom_medium.setVisibility(View.VISIBLE);

        }else if(afAverage >4 && afAverage <=6){
            af_medium.setVisibility(View.VISIBLE);

        }else if(afAverage > 6 && afAverage <= 8){
            af_medium_top.setVisibility(View.VISIBLE);

        }else if(afAverage > 8){
            af_top.setVisibility(View.VISIBLE);
        }

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
