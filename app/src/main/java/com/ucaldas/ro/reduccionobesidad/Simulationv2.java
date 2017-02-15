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
        final View piBottom = (View) view.findViewById(R.id.pi_bottom);
        final View piBottomMedium = (View) view.findViewById(R.id.pi_bottom_medium);
        final View piMedium = (View) view.findViewById(R.id.pi_medium);
        final View piMediumTop = (View) view.findViewById(R.id.pi_medium_top);
        final View piTop = (View) view.findViewById(R.id.pi_top);

        //Texto de PI
        //final TextView piPercentage = (TextView) view.findViewById(R.id.pi_percentage);
        final TextView piBottomData = (TextView) view.findViewById(R.id.pi_bottom_data);
        final TextView piBottomMediumData = (TextView) view.findViewById(R.id.pi_bottom_medium_data);
        final TextView piMediumData = (TextView) view.findViewById(R.id.pi_medim_data);
        final TextView piMediumTopData = (TextView) view.findViewById(R.id.pi_medium_top_data);
        final TextView piTopData = (TextView) view.findViewById(R.id.pi_top_data);


        //Progress de AA
        final View aaBottom = (View) view.findViewById(R.id.aa_bottom);
        final View aaBottomMedium = (View) view.findViewById(R.id.aa_bottom_medium);
        final View aaMedium = (View) view.findViewById(R.id.aa_medium);
        final View aaMediumTop = (View) view.findViewById(R.id.aa_medium_top);
        final View aaTop = (View) view.findViewById(R.id.aa_top);

        //Textos de AA
        //final TextView aaPercentage = (TextView) view.findViewById(R.id.aa_percentage);
        final TextView aaBottomData = (TextView) view.findViewById(R.id.aa_bottom_data);
        final TextView aaBottomMediumData = (TextView) view.findViewById(R.id.aa_bottom_medium_data);
        final TextView aaMediumData = (TextView) view.findViewById(R.id.aa_medium_data);
        final TextView aaMediumTopData = (TextView) view.findViewById(R.id.aa_medium_top_data);
        final TextView aaTopData = (TextView) view.findViewById(R.id.aa_top_data);


        //Progress de GS
        final View gsBottom = (View) view.findViewById(R.id.gs_bottom);
        final View gsBottomMedium = (View) view.findViewById(R.id.gs_bottom_medium);
        final View gsMedium = (View) view.findViewById(R.id.gs_medium);
        final View gsMediumTop = (View) view.findViewById(R.id.gs_medium_top);
        final View gsTop = (View) view.findViewById(R.id.gs_top);

        //Textos de GS
        //final TextView gsPercentage = (TextView) view.findViewById(R.id.gs_percentage);
        final TextView gsBottomData = (TextView) view.findViewById(R.id.gs_bottom_data);
        final TextView gsBottomMediumData = (TextView) view.findViewById(R.id.gs_bottom_medium_data);
        final TextView gsMediumData = (TextView) view.findViewById(R.id.gs_medium_data);
        final TextView gsMediumTopData = (TextView) view.findViewById(R.id.gs_medium_top_data);
        final TextView gsTopData = (TextView) view.findViewById(R.id.gs_top_data);


        //Progress de CH
        final View chBottom = (View) view.findViewById(R.id.ch_bottom);
        final View chBottomMedium = (View) view.findViewById(R.id.ch_bottom_medium);
        final View chMedium = (View) view.findViewById(R.id.ch_medium);
        final View chMediumTop = (View) view.findViewById(R.id.ch_medium_top);
        final View chTop = (View) view.findViewById(R.id.ch_top);

        //Textos de CH
        //final TextView chPercentage = (TextView) view.findViewById(R.id.ch_percentage);
        final TextView chBottomData = (TextView) view.findViewById(R.id.ch_bottom_data);
        final TextView chBottomMediumData = (TextView) view.findViewById(R.id.ch_bottom_medium_data);
        final TextView chMediumData = (TextView) view.findViewById(R.id.ch_medium_data);
        final TextView chMediumTopData = (TextView) view.findViewById(R.id.ch_medium_top_data);
        final TextView chTopData = (TextView) view.findViewById(R.id.ch_top_data);


        //Progress de AF
        final View afSed= (View) view.findViewById(R.id.af_sed);
        final View afBottom = (View) view.findViewById(R.id.af_bottom);
        final View afBottomMedium = (View) view.findViewById(R.id.af_bottom_medium);
        final View afMedium = (View) view.findViewById(R.id.af_medium);
        final View afMediumTop = (View) view.findViewById(R.id.af_medium_top);
        final View afTop = (View) view.findViewById(R.id.af_top);

        //Textos de AF
        //final TextView afPercentage = (TextView) view.findViewById(R.id.af_percentage);
        final TextView afSedData = (TextView) view.findViewById(R.id.af_sed_data);
        final TextView afBottomData = (TextView) view.findViewById(R.id.af_bottom_data);
        final TextView afBottomMediumData = (TextView) view.findViewById(R.id.af_bottom_medium_data);
        final TextView afMediumData = (TextView) view.findViewById(R.id.af_medium_data);
        final TextView afMediumTopData = (TextView) view.findViewById(R.id.af_medium_top_data);
        final TextView afTopData = (TextView) view.findViewById(R.id.af_top_data);


        if(mHome.user != null){
            final String[] foodsString = getResources().getStringArray(R.array.new_post_food_categories);
            final List<String> foodsCategories = Arrays.asList(foodsString);
            DatabaseReference firebaseDatabase = null;

            if(WelcomeActivity.CURRENT_APP_VERSION.equals("A"))
                firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("user-data").child(mHome.user.getUid());
            else
                firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("user-data-reflexive").child(mHome.user.getUid());

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

            //Actividades
            final AtomicInteger countOfAFs = new AtomicInteger(0);
            final AtomicInteger countOfAFb = new AtomicInteger(0);
            final AtomicInteger countOfAFmb = new AtomicInteger(0);
            final AtomicInteger countOfAFm = new AtomicInteger(0);
            final AtomicInteger countOfAFma = new AtomicInteger(0);
            final AtomicInteger countOfAFa = new AtomicInteger(0);

            firebaseDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    /*progress = ProgressDialog.show(getContext(), "Creando simulación...",
                            "Espera un momento", true);*/

                    Map<String, HashMap> data = (HashMap) dataSnapshot.getValue();
                    if(data != null){
                        int size = data.keySet().size();

                        for(String key: data.keySet()){

                            Map<String, Object> post = data.get(key);

                            if(!foodsCategories.contains(post.get("category"))){

                                if(post.get("average") != null) {
                                    long avg = (long) post.get("average");
                                    if(avg==1){
                                        countOfAFs.incrementAndGet();
                                    }else if(avg>1 && avg<3){
                                        countOfAFb.incrementAndGet();
                                    }else if(avg>=3 && avg<=4){
                                        countOfAFmb.incrementAndGet();
                                    }else if(avg>4 && avg<=6){
                                        countOfAFm.incrementAndGet();
                                    }else if(avg>6 && avg<=8){
                                        countOfAFma.incrementAndGet();
                                    }else if(avg>8 && avg<=10){
                                        countOfAFa.incrementAndGet();
                                    }
                                }

                            }

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
                                }else if(r_pi>=10){
                                    countOfPIa.incrementAndGet();
                                }
                            }

                            if(post.get("r_aa") != null){
                                double r_aa = Double.parseDouble(post.get("r_aa")+"");

                                if(r_aa>0.5 && r_aa<1){
                                    countOfAAb.incrementAndGet();
                                }else if(r_aa>=1 && r_aa<1.5){
                                    countOfAAmb.incrementAndGet();
                                }else if(r_aa>=1.5 && r_aa<2.2){
                                    countOfAAm.incrementAndGet();
                                }else if(r_aa>=2.2 && r_aa<3){
                                    countOfAAma.incrementAndGet();
                                }else if(r_aa>=3){
                                    countOfAAa.incrementAndGet();
                                }
                            }

                            if(post.get("r_gs") != null){
                                double r_gs = Double.parseDouble(post.get("r_gs")+"");

                                if(r_gs>0.5 && r_gs<1){
                                    countOfGSb.incrementAndGet();
                                }else if(r_gs>=1 && r_gs<1.5){
                                    countOfGSmb.incrementAndGet();
                                }else if(r_gs>=1.5 && r_gs<2.2){
                                    countOfGSm.incrementAndGet();
                                }else if(r_gs>=2.2 && r_gs<3){
                                    countOfGSma.incrementAndGet();
                                }else if(r_gs>=3){
                                    countOfGSa.incrementAndGet();
                                }
                            }

                            if(post.get("r_ch") != null){
                                double r_ch = Double.parseDouble(post.get("r_ch")+"");

                                if(r_ch>0.5 && r_ch<1){
                                    countOfCHb.incrementAndGet();
                                }else if(r_ch>=1 && r_ch<1.5){
                                    countOfCHmb.incrementAndGet();
                                }else if(r_ch>=1.5 && r_ch<2.2){
                                    countOfCHm.incrementAndGet();
                                }else if(r_ch>=2.2 && r_ch<3){
                                    countOfCHma.incrementAndGet();
                                }else if(r_ch>=3){
                                    countOfCHa.incrementAndGet();
                                }
                            }
                        }

                        int barMaxWidth = 200;

                        //Actualización de los datos correspondientes a las barras PI
                        piBottomData.setText(countOfPIb.get()+"");
                        piBottomMediumData.setText(countOfPImb.get()+"");
                        piMediumData.setText(countOfPIm.get()+"");
                        piMediumTopData.setText(countOfPIma.get()+"");
                        piTopData.setText(countOfPIa.get()+"");

                        //Actualización de view barras PI
                        piBottom.setLayoutParams(getWidthForBar(piBottom, countOfPIb.get(), barMaxWidth, size));
                        piBottomMedium.setLayoutParams(getWidthForBar(piBottomMedium, countOfPImb.get(), barMaxWidth, size));
                        piMedium.setLayoutParams(getWidthForBar(piMedium, countOfPIm.get(), barMaxWidth, size));
                        piMediumTop.setLayoutParams(getWidthForBar(piMediumTop, countOfPIma.get(), barMaxWidth, size));
                        piTop.setLayoutParams(getWidthForBar(piTop, countOfPIa.get(), barMaxWidth, size));

                        //Actualización porcentaje PI
                        /*int piTPercentage = ((countOfPIa.get()+countOfPIb.get()+countOfPIm.get()+countOfPIma.get()+countOfPImb.get())*100)/size;
                        piPercentage.setText(piTPercentage+"%");*/



                        //Actualización de los datos correspondientes a las barras AA
                        aaBottomData.setText(countOfAAb.get()+"");
                        aaBottomMediumData.setText(countOfAAmb.get()+"");
                        aaMediumData.setText(countOfAAm.get()+"");
                        aaMediumTopData.setText(countOfAAma.get()+"");
                        aaTopData.setText(countOfAAa.get()+"");

                        //Actualización de view barras AA
                        aaBottom.setLayoutParams(getWidthForBar(aaBottom, countOfAAb.get(), barMaxWidth, size));
                        aaBottomMedium.setLayoutParams(getWidthForBar(aaBottomMedium, countOfAAmb.get(), barMaxWidth, size));
                        aaMedium.setLayoutParams(getWidthForBar(aaMedium, countOfAAm.get(), barMaxWidth, size));
                        aaMediumTop.setLayoutParams(getWidthForBar(aaMediumTop, countOfAAma.get(), barMaxWidth, size));
                        aaTop.setLayoutParams(getWidthForBar(aaTop, countOfAAa.get(), barMaxWidth, size));

                        //Actualización porcentaje AA
                        /*int aaTPercentage = ((countOfAAa.get()+countOfAAb.get()+countOfAAm.get()+countOfAAma.get()+countOfAAmb.get())*100)/size;
                        aaPercentage.setText(aaTPercentage+"%");*/


                        //Actualización de los datos correspondientes a las barras GS
                        gsBottomData.setText(countOfGSb.get()+"");
                        gsBottomMediumData.setText(countOfGSmb.get()+"");
                        gsMediumData.setText(countOfGSm.get()+"");
                        gsMediumTopData.setText(countOfGSma.get()+"");
                        gsTopData.setText(countOfGSa.get()+"");

                        //Actualización de view barras GS
                        gsBottom.setLayoutParams(getWidthForBar(gsBottom, countOfGSb.get(), barMaxWidth, size));
                        gsBottomMedium.setLayoutParams(getWidthForBar(gsBottomMedium, countOfGSmb.get(), barMaxWidth, size));
                        gsMedium.setLayoutParams(getWidthForBar(gsMedium, countOfGSm.get(), barMaxWidth, size));
                        gsMediumTop.setLayoutParams(getWidthForBar(gsMediumTop, countOfGSma.get(), barMaxWidth, size));
                        gsTop.setLayoutParams(getWidthForBar(gsTop, countOfGSa.get(), barMaxWidth, size));

                        //Actualización porcentaje GS
                        /*int gsTPercentage = ((countOfGSa.get()+countOfGSb.get()+countOfGSm.get()+countOfGSma.get()+countOfGSmb.get())*100)/size;
                        gsPercentage.setText(gsTPercentage+"%");*/


                        //Actualización de los datos correspondientes a las barras CH
                        chBottomData.setText(countOfCHb.get()+"");
                        chBottomMediumData.setText(countOfCHmb.get()+"");
                        chMediumData.setText(countOfCHm.get()+"");
                        chMediumTopData.setText(countOfCHma.get()+"");
                        chTopData.setText(countOfCHa.get()+"");

                        //Actualización de view barras CH
                        chBottom.setLayoutParams(getWidthForBar(chBottom, countOfCHb.get(), barMaxWidth, size));
                        chBottomMedium.setLayoutParams(getWidthForBar(chBottomMedium, countOfCHmb.get(), barMaxWidth, size));
                        chMedium.setLayoutParams(getWidthForBar(chMedium, countOfCHm.get(), barMaxWidth, size));
                        chMediumTop.setLayoutParams(getWidthForBar(chMediumTop, countOfCHma.get(), barMaxWidth, size));
                        chTop.setLayoutParams(getWidthForBar(chTop, countOfCHa.get(), barMaxWidth, size));

                        //Actualización porcentaje CH
                        /*int chTPercentage = ((countOfCHa.get()+countOfCHb.get()+countOfCHm.get()+countOfCHma.get()+countOfCHmb.get())*100)/size;
                        chPercentage.setText(chTPercentage+"%");*/


                        //Actualización de los datos correspondientes a las barras AF
                        afBottomData.setText(countOfAFb.get()+"");
                        afBottomMediumData.setText(countOfAFmb.get()+"");
                        afMediumData.setText(countOfAFm.get()+"");
                        afMediumTopData.setText(countOfAFma.get()+"");
                        afTopData.setText(countOfAFa.get()+"");

                        //Actualización de view barras AF
                        afSed.setLayoutParams(getWidthForBar(afSed, countOfAFs.get(), barMaxWidth, size));
                        afBottom.setLayoutParams(getWidthForBar(afBottom, countOfAFb.get(), barMaxWidth, size));
                        afBottomMedium.setLayoutParams(getWidthForBar(afBottomMedium, countOfAFmb.get(), barMaxWidth, size));
                        afMedium.setLayoutParams(getWidthForBar(afMedium, countOfAFm.get(), barMaxWidth, size));
                        afMediumTop.setLayoutParams(getWidthForBar(afMediumTop, countOfAFma.get(), barMaxWidth, size));
                        afTop.setLayoutParams(getWidthForBar(afTop, countOfAFa.get(), barMaxWidth, size));

                        //Actualización porcentaje AF
                        /*int afTPercentage = ((countOfAFs.get()+countOfAFa.get()+countOfAFb.get()+countOfAFm.get()+countOfAFma.get()+countOfAFmb.get())*100)/size;
                        afPercentage.setText(afTPercentage+"%");*/


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

    private ViewGroup.LayoutParams getWidthForBar(View view, int quantity, int maxWidth, int dataSize){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,(((quantity*100/dataSize)*maxWidth)/100), getContext().getResources().getDisplayMetrics());
        return params;
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
