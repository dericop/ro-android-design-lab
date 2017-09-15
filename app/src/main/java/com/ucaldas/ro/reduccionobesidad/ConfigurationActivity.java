package com.ucaldas.ro.reduccionobesidad;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
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
    private TextInputLayout nameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        configureToolbarAndActions();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        configureDBReference();

        tests();
        initGraphicalComponents();
        loadData();

    }

    //28   //19
    public void tests(){
        final String mId = mHome.user.getUid();
        if(!(mId == null) && !mId.equals("")){
            mDatabase.child("users").child(mId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    AUser usr = dataSnapshot.getValue(AUser.class);
                    if(usr == null){
                        AUser nUsr = new AUser();
                        nUsr.setmUserName(mHome.user.getDisplayName());
                        nUsr.setmEmail(mHome.user.getEmail());
                        nUsr.setmPhotoUrl(mHome.user.getPhotoUrl().toString());

                        nUsr.setmUid(mId);
                        nUsr.setIsAdmin(0);

                        HashMap userData = nUsr.toMap();
                        HashMap dataToUpdate = new HashMap();

                        if (WelcomeActivity.CURRENT_APP_VERSION.equals("A")) {
                            dataToUpdate.put("/users/" + mId, userData);
                        } else {
                            dataToUpdate.put("/users-reflexive/" + mId, userData);
                        }

                        mDatabase.updateChildren(dataToUpdate).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                initGraphicalComponents();
                                loadData();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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

    private void configureToolbarAndActions() {
        /*
        *  Habilitar el toolbar para acciones y agregar evento de guardar post.
        * */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private static int RESULT_LOAD_IMAGE = 1;

    private void initGraphicalComponents() {
        this.photo = (ImageView) findViewById(R.id.photo);
        this.textInputName = (TextView) findViewById(R.id.textInputName);
        this.textInputEditWeight = (TextView) findViewById(R.id.textInputEditWeight);
        this.gender_spinner = (Spinner) findViewById(R.id.gender_spinner);
        this.btn_saveChanges = (Button) findViewById(R.id.btn_saveChanges);
        this.btn_ChangePhoto = (Button) findViewById(R.id.btn_change_photo);
        this.nameLayout = (TextInputLayout) findViewById(R.id.txt_input_layour_name);

        genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_dropdown_item);

        this.gender_spinner.setAdapter(genderAdapter);

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
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            }
        });

        EditText nameEditText = nameLayout.getEditText();
        if (nameEditText != null) {
            nameEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    nameLayout.setError("");
                    nameLayout.setErrorEnabled(false);
                }
            });
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULT_LOAD_IMAGE) {

            if (data != null) {
                Uri selectedImage = data.getData();
                String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
                Cursor cur = managedQuery(selectedImage, orientationColumn, null, null, null);

                int orientation = -1;
                if (cur != null && cur.moveToFirst()) {
                    orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                }

                if (loadImageResultInImageView(photo, data, orientation)) {

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://reduccion-de-obesidad-7414c.appspot.com");

                    Calendar cal = Calendar.getInstance();
                    String date = cal.get(Calendar.YEAR) + "" + cal.get(Calendar.MONTH) + "" + cal.get(Calendar.DAY_OF_MONTH) + "" + cal.get(Calendar.HOUR) + "" + cal.get(Calendar.MINUTE) + "" + cal.get(Calendar.SECOND) + "";
                    StorageReference imagesRef = storageRef.child("images/" + mHome.user.getUid() + "/" + date);


                    photo.setDrawingCacheEnabled(true);
                    photo.buildDrawingCache();
                    Bitmap bitmap = photo.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                    byte[] dat = baos.toByteArray();

                    if (isOnline()) {
                        configureDBReference();

                        this.progressDialog = ProgressDialog.show(this, "Actualizando foto...", "Espera un momento", true);
                        this.progressDialog.setCancelable(true);

                        UploadTask uploadTask = imagesRef.putBytes(dat);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {

                                if (getCurrentFocus() != null)
                                    Snackbar.make(getCurrentFocus(), "Revise su conexión a internet e intentelo más tarde", 2000).show();
                                progressDialog.dismiss();

                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if (taskSnapshot != null) {
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    updateUserPhoto(downloadUrl);
                                }
                            }
                        });
                    } else {
                        if (getCurrentFocus() != null)
                            Snackbar.make(getCurrentFocus(), "Revise su conexión a internet e intentelo más tarde", 2000).show();
                        progressDialog.dismiss();
                    }
                }

            }

        }
    }

    private void updateUserPhoto(final Uri downloadUrl) {
        this.confRef.child(mHome.user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    AUser user = dataSnapshot.getValue(AUser.class);
                    if (!downloadUrl.equals(""))
                        user.setmPhotoUrl(downloadUrl.toString());
                    confRef.child(mHome.user.getUid()).updateChildren(user.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    private boolean loadImageResultInImageView(ImageView imageView, Intent data, int orientation) {

        try {
            final Uri imageUri = data.getData();
            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

            int nh = (int) (selectedImage.getHeight() * (512.0 / selectedImage.getWidth()));
            Bitmap scaled = Bitmap.createScaledBitmap(selectedImage, 512, nh, true);

            Bitmap rotated;
            if (orientation == 90)
                rotated = rotateBitmap(scaled, 90);
            else if (orientation == 270)
                rotated = ConfigurationActivity.rotateBitmap(scaled, 270);
            else
                rotated = rotateBitmap(scaled, 0);

            imageView.setImageBitmap(rotated);
            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    private void updateDataInDB() {
        HashMap data;
        final CharSequence name = this.textInputName.getText();
        final CharSequence gender = this.gender_spinner.getSelectedItem().toString();
        final CharSequence weight = this.textInputEditWeight.getText().toString();

        final AppCompatActivity that = this;

        if (!(name.length() == 0)) {
            final String uid = mHome.user.getUid();
            final String email = mHome.user.getEmail();
            final String uName = name.toString().replace("\n", "");

            configureDBReference();
            if (isOnline()) {

                confRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            AUser user = dataSnapshot.getValue(AUser.class);
                            user.setmUserName(uName);

                            if (!gender.equals(""))
                                user.setmGender(gender.toString());

                            if (!weight.equals(""))
                                user.setmWeight(Long.parseLong(weight.toString()));

                            HashMap userData = user.toMap();
                            HashMap dataToUpdate = new HashMap();

                            if (WelcomeActivity.CURRENT_APP_VERSION.equals("A")) {
                                dataToUpdate.put("/users/" + uid, userData);
                            } else {
                                dataToUpdate.put("/users-reflexive/" + uid, userData);
                            }

                            progressDialog = ProgressDialog.show(that, "Actualizando datos...", "Espera un momento", true);
                            progressDialog.setCancelable(true);

                            mDatabase.updateChildren(dataToUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    finish();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            } else {
                if (getCurrentFocus() != null)
                    Snackbar.make(getCurrentFocus(), "Revise su conexión a internet e intentelo más tarde", 2000).show();
                progressDialog.dismiss();
            }

        } else {
            nameLayout.setError("El nombre es requerido");
            nameLayout.setErrorEnabled(true);
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

    private void loadData() {

        this.progressDialog = progressDialog.show(this, "Recuperando información", "Espere un momento", true);
        this.progressDialog.setCancelable(true);
        final ConfigurationActivity that = this;

        if (confRef != null && mHome.user != null) {
            confRef.child(mHome.user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        AUser user = dataSnapshot.getValue(AUser.class);
                        String remoteName = user.getmUserName();
                        String remoteGender = user.getmGender();
                        String remoteWeight = user.getmWeight() + "";
                        String photoUrl = user.getmPhotoUrl();

                        if (!remoteWeight.equals(""))
                            textInputEditWeight.setText(remoteWeight);

                        if (remoteGender != null && !remoteGender.equals("")) {
                            int pos = genderAdapter.getPosition(remoteGender);
                            gender_spinner.setSelection(pos);
                        }

                        Glide.with(getBaseContext()).load(photoUrl).into(photo);
                        textInputName.setText(remoteName);
                    }

                    if (that.progressDialog != null)
                        that.progressDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private void configureDBReference() {
        if (WelcomeActivity.CURRENT_APP_VERSION.equals("A")) {
            this.confRef = mDatabase.child("users");
        } else {
            this.confRef = mDatabase.child("users-reflexive");
        }
    }


}
