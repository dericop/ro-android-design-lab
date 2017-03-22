package com.ucaldas.ro.reduccionobesidad;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ConfigurationActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private DatabaseReference confRef;

    //Referencias a componentes gráficos
    private ImageView photo;
    private TextView textInputName;
    private TextView textInputEditWeight;
    private Spinner gender_spinner;
    private Button btn_saveChanges;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        configureToolbarAndActions();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        configureDBReference();

        initGraphicalComponents();
        loadData();

    }

    @Override
    public boolean onSupportNavigateUp() {
        /*
        * Habilitar la navegación en esta vista
        * */
        finish();
        return false;
    }

    private void configureToolbarAndActions() {
        /*
        *  Habilitar el toolbar para acciones y agregar evento de guardar post.
        * */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initGraphicalComponents(){
        this.photo = (ImageView) findViewById(R.id.photo);
        this.textInputName = (TextView) findViewById(R.id.textInputName);
        this.textInputEditWeight = (TextView) findViewById(R.id.textInputEditWeight);
        this.gender_spinner = (Spinner) findViewById(R.id.gender_spinner);
        this.btn_saveChanges = (Button) findViewById(R.id.btn_saveChanges);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_dropdown_item );

        this.gender_spinner.setAdapter(categoryAdapter);;

        this.btn_saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDataInDB();
            }
        });
    }

    private void updateDataInDB(){
        HashMap data = new HashMap();
        CharSequence name = this.textInputName.getText();
        CharSequence gender = this.gender_spinner.getSelectedItem().toString();
        CharSequence weight = this.textInputEditWeight.getText();

        if(!name.equals("") && !gender.equals("") && !weight.equals("")){
            String uid = mHome.user.getUid();
            String email = mHome.user.getEmail();
            String photoUrl = "";

            AUser user = new AUser(uid, name.toString(), email, photoUrl ,gender.toString());

        }

        HashMap dataToUpdate = new HashMap();
        dataToUpdate.put("/users/"+mHome.user.getUid(), data);

        this.confRef.setValue(dataToUpdate);

    }

    private void loadData(){

        String photoUrl = mHome.user.getPhotoUrl().toString();
        Glide.with(getBaseContext()).load(photoUrl).into(this.photo);

        this.textInputName.setText(mHome.user.getDisplayName());

    }

    private void configureDBReference(){
        if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
            this.confRef = mDatabase.child("users");
        }else{
            this.confRef = mDatabase.child("users-reflexive");
        }

    }


}
