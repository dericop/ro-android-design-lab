package com.ucaldas.ro.reduccionobesidad;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TermsAndConditions extends AppCompatActivity {

    private boolean termsIsOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);
        termsIsOk = true;

        Button btnAgree = (Button) findViewById(R.id.btnAgree);
        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }


}
