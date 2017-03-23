package com.ucaldas.ro.reduccionobesidad;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
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
    private Button btn_ChangePhoto;


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

    private static int RESULT_LOAD_IMAGE = 1;

    private void initGraphicalComponents(){
        this.photo = (ImageView) findViewById(R.id.photo);
        this.textInputName = (TextView) findViewById(R.id.textInputName);
        this.textInputEditWeight = (TextView) findViewById(R.id.textInputEditWeight);
        this.gender_spinner = (Spinner) findViewById(R.id.gender_spinner);
        this.btn_saveChanges = (Button) findViewById(R.id.btn_saveChanges);
        this.btn_ChangePhoto = (Button) findViewById(R.id.btn_change_photo);

        genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_dropdown_item );

        this.gender_spinner.setAdapter(genderAdapter);;

        this.btn_saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDataInDB();
            }
        });

        this.btn_ChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == RESULT_LOAD_IMAGE){
            Uri selectedImage = data.getData();
            String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
            Cursor cur = managedQuery(selectedImage, orientationColumn, null, null, null);

            int orientation = -1;
            if (cur != null && cur.moveToFirst()) {
                orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
            }

            loadImageResultInImageView(photo, data, orientation);
        }
    }


    private void loadImageResultInImageView(ImageView imageView, Intent data, int orientation) {

        try {
            final Uri imageUri = data.getData();
            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

            int nh = (int) (selectedImage.getHeight() * (512.0 / selectedImage.getWidth()));
            Bitmap scaled = Bitmap.createScaledBitmap(selectedImage, 512, nh, true);

            Bitmap rotated;
            if(orientation == 90)
                rotated = rotateBitmap(scaled, 90);
            else if(orientation == 270)
                rotated = ConfigurationActivity.rotateBitmap(scaled, 270);
            else
                rotated = rotateBitmap(scaled, 0);

            imageView.setImageBitmap(rotated);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
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
