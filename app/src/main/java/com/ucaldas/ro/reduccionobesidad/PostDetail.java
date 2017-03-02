package com.ucaldas.ro.reduccionobesidad;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

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


        //Actualización componentes gráficos
        ImageView imgPreview = (ImageView) findViewById(R.id.imgPreview);
        String imageForComment = getIntent().getStringExtra("image");
        Glide.with(this).load(imageForComment).into(imgPreview);


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
