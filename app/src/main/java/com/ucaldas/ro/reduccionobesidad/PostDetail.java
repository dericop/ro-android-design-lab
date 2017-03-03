package com.ucaldas.ro.reduccionobesidad;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.LinkedList;
import java.util.List;

public class PostDetail extends AppCompatActivity {

    private List mComments;
    private CommentsAdapter comAdapter;
    private ListView commentListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        init();
    }

    private void init(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        commentListView = (ListView) findViewById(R.id.commentsList);

        mComments = new LinkedList();
        mComments.add(1);
        mComments.add(2);
        mComments.add(2);


        comAdapter = new CommentsAdapter(
                getBaseContext(),
                mComments);

        commentListView.setAdapter(comAdapter);


        //Recuperación de datos de la vista anterior
        String name = getIntent().getStringExtra("name");
        String imageForComment = getIntent().getStringExtra("image");
        String user = getIntent().getStringExtra("userName");
        double r_pi = getIntent().getDoubleExtra("r_pi", 0);
        double r_aa = getIntent().getDoubleExtra("r_aa", 0);
        double r_gs = getIntent().getDoubleExtra("r_gs", 0);
        double r_ch = getIntent().getDoubleExtra("r_ch", 0);

        //Actualización componentes gráficos
        ImageView imgPreview = (ImageView) findViewById(R.id.imgPreview);
        TextView title = (TextView) findViewById(R.id.title);
        TextView userName = (TextView) findViewById(R.id.userName);

        Glide.with(this).load(imageForComment).into(imgPreview);
        title.setText(name);
        userName.setText(user);

        updateQualificationInfo(r_pi, r_aa, r_gs, r_ch);

    }

    private void updateQualificationInfo(double pi, double aa, double gs, double ch){
        View piContainer = findViewById(R.id.piContainer);
        View aaContainer = findViewById(R.id.aaContainer);
        View gsContainer = findViewById(R.id.gsContainer);
        View chContainer = findViewById(R.id.chContainer);


        int maxCalificationForPI = 10;
        int relativeLayoutHeight = 50;

        if(pi != 0){
            int percentage = (int)(pi*100)/maxCalificationForPI;
            int graphicalHeight = (percentage*relativeLayoutHeight) / 100;

            ViewGroup.LayoutParams piParams = piContainer.getLayoutParams();
            piParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, graphicalHeight, getResources().getDisplayMetrics());
            piContainer.setLayoutParams(piParams);
        }

        int maxCalificationForOthers = 3;
        if(aa != 0){
            int percentage = (int)(aa*100)/maxCalificationForOthers;
            int graphicalHeight = (percentage*relativeLayoutHeight)/ 100;

            ViewGroup.LayoutParams aaParams = aaContainer.getLayoutParams();
            aaParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, graphicalHeight, getResources().getDisplayMetrics());
            aaContainer.setLayoutParams(aaParams);
        }

        if(gs != 0){
            int percentage = (int)(gs*100)/maxCalificationForOthers;
            int graphicalHeight = (percentage*relativeLayoutHeight)/ 100;

            ViewGroup.LayoutParams gsParams = gsContainer.getLayoutParams();
            gsParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, graphicalHeight, getResources().getDisplayMetrics());
            gsContainer.setLayoutParams(gsParams);
        }

        if(ch != 0){

            int percentage = (int)(ch*100)/maxCalificationForOthers;
            int graphicalHeight = (percentage*relativeLayoutHeight)/ 100;
            ViewGroup.LayoutParams chParams = chContainer.getLayoutParams();
            chParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, graphicalHeight, getResources().getDisplayMetrics());
            chContainer.setLayoutParams(chParams);
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        /*
        * Habilitar la navegación en esta vista
        * */
        finish();
        return false;
    }

}
