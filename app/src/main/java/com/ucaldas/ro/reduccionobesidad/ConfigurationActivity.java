package com.ucaldas.ro.reduccionobesidad;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private ProgressDialog progressDialog;
    private ArrayAdapter<CharSequence> genderAdapter;


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

        genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_dropdown_item );

        this.gender_spinner.setAdapter(genderAdapter);;

        this.btn_saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDataInDB();
            }
        });
    }

    private void updateDataInDB(){
        HashMap data;
        CharSequence name = this.textInputName.getText();
        CharSequence gender = this.gender_spinner.getSelectedItem().toString();
        CharSequence weight = this.textInputEditWeight.getText().toString();

        if(!name.equals("")){
            String uid = mHome.user.getUid();
            String email = mHome.user.getEmail();
            String photoUrl = mHome.user.getPhotoUrl()+"";


            AUser user = new AUser(uid, name.toString(), email, photoUrl);

            if(!gender.equals(""))
                user.setmGender(gender.toString());

            if(!weight.equals(""))
                user.setmWeight(Long.parseLong(weight.toString()));

            data = user.toMap();

            HashMap dataToUpdate = new HashMap();

            if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
                dataToUpdate.put("/users/"+uid, data);
            }else{
                dataToUpdate.put("/users-reflexive/" + uid, data);
            }

            this.progressDialog = ProgressDialog.show(this, "Actualizando datos...", "Espera un momento", true);
            this.progressDialog.setCancelable(true);

            mDatabase.updateChildren(dataToUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    finish();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void loadData(){

        this.progressDialog = progressDialog.show(this, "Recuperando información", "Espere un momento", true);
        this.progressDialog.setCancelable(true);
        final ConfigurationActivity that = this;

        confRef.child(mHome.user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null){
                    AUser user = dataSnapshot.getValue(AUser.class);
                    String remoteName = user.getmUserName();
                    String remoteGender = user.getmGender();
                    String remoteWeight = user.getmWeight()+"";
                    String photoUrl = user.getmPhotoUrl();

                    Log.v("Configuration", remoteName);

                    if(!remoteWeight.equals(""))
                        textInputEditWeight.setText(remoteWeight);

                    if(remoteGender!=null && !remoteGender.equals("")){
                        int pos = genderAdapter.getPosition(remoteGender);
                        gender_spinner.setSelection(pos);
                    }

                    Glide.with(getBaseContext()).load(photoUrl).into(photo);
                    textInputName.setText(remoteName);
                }

                if(that != null && that.progressDialog!=null)
                    that.progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void configureDBReference(){
        if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
            this.confRef = mDatabase.child("users");
        }else{
            this.confRef = mDatabase.child("users-reflexive");
        }
    }


}
