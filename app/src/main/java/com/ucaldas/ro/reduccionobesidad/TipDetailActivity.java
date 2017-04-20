package com.ucaldas.ro.reduccionobesidad;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class TipDetailActivity extends AppCompatActivity {

    private Button delete;
    private TextView descriptionC;
    private ImageView imageC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String image = getIntent().getStringExtra("image");

        descriptionC = (TextView) findViewById(R.id.description);
        imageC = (ImageView) findViewById(R.id.image);

        descriptionC.setText(description);
        Glide.with(getApplicationContext()).load(image).into(imageC);

        toolbar.setTitle(title);


        setSupportActionBar(toolbar);
        configureToolbarAndActions();
    }

    private void configureToolbarAndActions() {
        changeStatusBarColor();
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        delete = (Button) findViewById(R.id.btn_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });

        if(!mHome.isAdmin)
            delete.setVisibility(View.GONE);
    }

    private void changeStatusBarColor() {
        /*
        * Cambiar el color del toolbar
        * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

}
