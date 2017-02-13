package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

        //Progress de PI
        View piBottom = (View) view.findViewById(R.id.pi_bottom);
        View piBottomMedium = (View) view.findViewById(R.id.pi_bottom_medium);
        View piMedium = (View) view.findViewById(R.id.pi_medium);
        View piMediumTop = (View) view.findViewById(R.id.pi_medium_top);
        View piTop = (View) view.findViewById(R.id.pi_top);

        //Texto de PI
        TextView piPercentage = (TextView) view.findViewById(R.id.pi_percentage);
        TextView piBottomData = (TextView) view.findViewById(R.id.pi_bottom_data);
        TextView piBottomMediumData = (TextView) view.findViewById(R.id.pi_bottom_medium_data);
        TextView piMediumData = (TextView) view.findViewById(R.id.pi_medim_data);
        TextView piMediumTopData = (TextView) view.findViewById(R.id.pi_medium_top_data);
        TextView piTopData = (TextView) view.findViewById(R.id.pi_top_data);

        //Progress de AA
        View aaBottom = (View) view.findViewById(R.id.aa_bottom);
        View aaBottomMedium = (View) view.findViewById(R.id.aa_bottom_medium);
        View aaMedium = (View) view.findViewById(R.id.aa_medium);
        View aaMediumTop = (View) view.findViewById(R.id.aa_medium_top);
        View aaTop = (View) view.findViewById(R.id.aa_top);

        //Textos de AA
        TextView aaPercentage = (TextView) view.findViewById(R.id.aa_percentage);
        TextView aaBottomData = (TextView) view.findViewById(R.id.aa_bottom_data);
        TextView aaBottomMediumData = (TextView) view.findViewById(R.id.aa_bottom_medium_data);
        /*TextView aaMediumData = (TextView) view.findViewById(R.id.aa_medim_data);
        TextView aaMediumTopData = (TextView) view.findViewById(R.id.aa_medium_top_data);
        TextView aaTopData = (TextView) view.findViewById(R.id.aa_top_data);*/









        //Progress de GS
        View gsBottom = (View) view.findViewById(R.id.gs_bottom);
        View gsBottomMedium = (View) view.findViewById(R.id.gs_bottom_medium);
        View gsMedium = (View) view.findViewById(R.id.gs_medium);
        View gsMediumTop = (View) view.findViewById(R.id.gs_medium_top);
        View gsTop = (View) view.findViewById(R.id.gs_top);
        TextView gsPercentage = (TextView) view.findViewById(R.id.gs_percentage);

        //Progress de CH
        View chBottom = (View) view.findViewById(R.id.ch_bottom);
        View chBottomMedium = (View) view.findViewById(R.id.ch_bottom_medium);
        View chMedium = (View) view.findViewById(R.id.ch_medium);
        View chMediumTop = (View) view.findViewById(R.id.ch_medium_top);
        View chTop = (View) view.findViewById(R.id.ch_top);
        TextView chPercentage = (TextView) view.findViewById(R.id.ch_percentage);

        //Progress de AF
        View afSed= (View) view.findViewById(R.id.af_sed);
        View afBottom = (View) view.findViewById(R.id.af_bottom);
        View afBottomMedium = (View) view.findViewById(R.id.af_bottom_medium);
        View afMedium = (View) view.findViewById(R.id.af_medium);
        View afMediumTop = (View) view.findViewById(R.id.af_medium_top);
        View afTop = (View) view.findViewById(R.id.af_top);
        TextView afPercentage = (TextView) view.findViewById(R.id.af_percentage);


        if(mHome.user != null){
            final String[] foodsString = getResources().getStringArray(R.array.new_post_food_categories);
            final List<String> foodsCategories = Arrays.asList(foodsString);
            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("user-data").child(mHome.user.getUid());

            //Procesados industrialmente
            final AtomicInteger countOfPIb = new AtomicInteger(0);
            final AtomicInteger countOfPImb = new AtomicInteger(0);
            final AtomicInteger countOfPIm = new AtomicInteger(0);
            final AtomicInteger countOfPIma = new AtomicInteger(0);
            final AtomicInteger countOfPIa = new AtomicInteger(0);

            //Azúcares añadidos
            final AtomicInteger countOfAAb = new AtomicInteger(0);
            final AtomicInteger countOfAAmb = new AtomicInteger(0);
            final AtomicInteger countOfAAm = new AtomicInteger(0);
            final AtomicInteger countOfAAma = new AtomicInteger(0);
            final AtomicInteger countOfAAa = new AtomicInteger(0);

            //Grasas Saturadas
            final AtomicInteger countOfGSb = new AtomicInteger(0);
            final AtomicInteger countOfGSmb = new AtomicInteger(0);
            final AtomicInteger countOfGSm = new AtomicInteger(0);
            final AtomicInteger countOfGSma = new AtomicInteger(0);
            final AtomicInteger countOfGSa = new AtomicInteger(0);

            //Grasas Saturadas
            final AtomicInteger countOfCHb = new AtomicInteger(0);
            final AtomicInteger countOfCHmb = new AtomicInteger(0);
            final AtomicInteger countOfCHm = new AtomicInteger(0);
            final AtomicInteger countOfCHma = new AtomicInteger(0);
            final AtomicInteger countOfCHa = new AtomicInteger(0);

            firebaseDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    /*progress = ProgressDialog.show(getContext(), "Creando simulación...",
                            "Espera un momento", true);*/

                    Map<String, HashMap> data = (HashMap) dataSnapshot.getValue();
                    if(data != null){
                        for(String key: data.keySet()){

                            Map<String, Object> post = data.get(key);

                            if(post.get("r_pi") != null){
                                double r_pi = Double.parseDouble(post.get("r_pi")+"");

                                if(r_pi>0 && r_pi<3){
                                    countOfPIb.incrementAndGet();
                                }else if(r_pi>=3 && r_pi<5){
                                    countOfPImb.incrementAndGet();
                                }else if(r_pi>=5 && r_pi<7){
                                    countOfPIm.incrementAndGet();
                                }else if(r_pi>=7 && r_pi<10){
                                    countOfPIma.incrementAndGet();
                                }else{
                                    countOfPIa.incrementAndGet();
                                }
                            }

                            if(post.get("r_aa") != null){
                                double r_aa = Double.parseDouble(post.get("r_aa")+"");

                                if(r_aa>0 && r_aa<3){
                                    countOfAAb.incrementAndGet();
                                }else if(r_aa>=3 && r_aa<5){
                                    countOfAAmb.incrementAndGet();
                                }else if(r_aa>=5 && r_aa<7){
                                    countOfAAm.incrementAndGet();
                                }else if(r_aa>=7 && r_aa<10){
                                    countOfAAma.incrementAndGet();
                                }else{
                                    countOfAAa.incrementAndGet();
                                }
                            }

                            if(post.get("r_gs") != null){
                                double r_gs = Double.parseDouble(post.get("r_gs")+"");

                                if(r_gs>0 && r_gs<3){
                                    countOfGSb.incrementAndGet();
                                }else if(r_gs>=3 && r_gs<5){
                                    countOfGSmb.incrementAndGet();
                                }else if(r_gs>=5 && r_gs<7){
                                    countOfGSm.incrementAndGet();
                                }else if(r_gs>=7 && r_gs<10){
                                    countOfGSma.incrementAndGet();
                                }else{
                                    countOfGSa.incrementAndGet();
                                }
                            }

                            if(post.get("r_ch") != null){
                                double r_ch = Double.parseDouble(post.get("r_ch")+"");

                                if(r_ch>0 && r_ch<3){
                                    countOfCHb.incrementAndGet();
                                }else if(r_ch>=3 && r_ch<5){
                                    countOfCHmb.incrementAndGet();
                                }else if(r_ch>=5 && r_ch<7){
                                    countOfCHm.incrementAndGet();
                                }else if(r_ch>=7 && r_ch<10){
                                    countOfCHma.incrementAndGet();
                                }else{
                                    countOfCHa.incrementAndGet();
                                }
                            }
                        }


                        //ViewGroup.LayoutParams circleBox1Params = sim_circle_box_1.getLayoutParams();


                        //Inicialización de variables
                        countOfPIb.set(0);
                        countOfPImb.set(0);
                        countOfPIm.set(0);
                        countOfPIma.set(0);
                        countOfPIa.set(0);

                        countOfAAb.set(0);
                        countOfAAmb.set(0);
                        countOfAAm.set(0);
                        countOfAAma.set(0);
                        countOfAAa.set(0);

                        countOfGSb.set(0);
                        countOfGSmb.set(0);
                        countOfGSm.set(0);
                        countOfGSma.set(0);
                        countOfGSa.set(0);

                        countOfCHb.set(0);
                        countOfCHmb.set(0);
                        countOfCHm.set(0);
                        countOfCHma.set(0);
                        countOfCHa.set(0);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
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
