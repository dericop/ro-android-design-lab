package com.ucaldas.ro.reduccionobesidad;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.util.HashMap;
import java.util.Iterator;

public class GamificationActivity extends AppCompatActivity {

    private TextView score_view;
    private View percentage_view;
    private ImageView img_coin;

    private DatabaseReference database;
    private long score;

    private final float COUNT_OF_GOLD_SCORE = 241f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamification);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        init();
        configureDatabaseAndInitData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private void init(){
        score_view = (TextView)findViewById(R.id.score);
        percentage_view = findViewById(R.id.percentage_view);
        img_coin = (ImageView) findViewById(R.id.img_coin);
    }

    private void configureDatabaseAndInitData(){
        database = FirebaseDatabase.getInstance().getReference();
        if(mHome.user != null){

            database.child("gamification-score").child(mHome.user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null){
                        HashMap<String, Object> data = (HashMap) dataSnapshot.getValue();
                        Iterator keysIt = data.keySet().iterator();

                        while(keysIt.hasNext()){
                            String key = (String)keysIt.next();
                            score += (long)((HashMap)data.get(key)).get("score");
                        }

                        score_view.setText(score +"pts");

                        if(score > COUNT_OF_GOLD_SCORE){
                            score = (long)COUNT_OF_GOLD_SCORE;
                        }

                        double percentage = score/COUNT_OF_GOLD_SCORE;
                        int wTarget = (int)(270 * percentage);

                        final float scale = getBaseContext().getResources().getDisplayMetrics().density;
                        int pixels = (int) (wTarget * scale + 0.5f);
                        ViewGroup.LayoutParams lParams = percentage_view.getLayoutParams();
                        lParams.width = pixels;

                        percentage_view.setLayoutParams(lParams);

                        updateCoin();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void updateCoin(){
        if(score>=0 && score<=20){
            img_coin.setImageDrawable(getResources().getDrawable(R.drawable.ic_5nivel));
        }else if(score>20 && score<=60){
            img_coin.setImageDrawable(getResources().getDrawable(R.drawable.ic_4nivel));
        }else if(score>60 && score<=120){
            img_coin.setImageDrawable(getResources().getDrawable(R.drawable.ic_bronce));
        }else if(score>120 && score<=240){
            img_coin.setImageDrawable(getResources().getDrawable(R.drawable.ic_plata));
        }else if(score>240){
            img_coin.setImageDrawable(getResources().getDrawable(R.drawable.ic_oro));
        }
    }

}
