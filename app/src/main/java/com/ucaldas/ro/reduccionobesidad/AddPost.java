package com.ucaldas.ro.reduccionobesidad;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v13.app.ActivityCompat;
import android.support.v13.app.FragmentCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.acl.Permission;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static android.os.Environment.getExternalStoragePublicDirectory;


public class AddPost extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    static final int REQUEST_IMAGE_CAPTURE = 1; //Bandera para verificar en los resultados de actividad si se ha tomado una foto.
    static final int RESULT_LOAD_IMAGE = 2; //Bandera para verificar en los resultados de actividad si se ha cargado una foto de la galería
    private String SOURCE = ""; //Indica el origen de un llamado, con el objetivo de reutilizar la vista.
    private boolean isActivity;
    private boolean radioButtonIsClicked = false;
    private static final String FRAGMENT_DIALOG = "dialog";
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 2;

    //Datos que el usuario va a ingresar para la publiación
    private Spinner frecuencySpinner;
    private Spinner categorySpinner;
    private Spinner durationSpinner;
    private ImageView prev;

    private CharSequence nameText;
    private CharSequence category;
    private CharSequence frecuency;
    private Drawable image;
    private ProgressDialog progress;
    final AppCompatActivity that = this; //Guardar el contexto para los menú de alerta.
    private String idForReply;
    private String imageForReply;
    private String typeForReply;
    private long resultForReply;
    private long averageForReply;

    private String idForUpdate;

    private DatabaseReference database;

    private ArrayAdapter<CharSequence> categoryAdapter;
    private AppCompatActivity thisRef;

    private final String FILE_PROVIDER = "com.ucaldas.android.cocono.fileprovider";

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post); //Asociar esta vista para el control de la interface

        configureToolbarAndActions();
        assignActivitySourceAndInitData(savedInstanceState);

        //Esconder el teclado
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    private void showSpinnerDurationAndLoadData() {
        Spinner spinnerDuration = (Spinner) findViewById(R.id.activity_duration);
        TextView spinner_duration_label = (TextView) findViewById(R.id.spinner_duration_label);

        loadAdapterWithActivityCategories();
        spinnerDuration.setVisibility(View.VISIBLE);
        spinner_duration_label.setVisibility(View.VISIBLE);
        categorySpinner.setAdapter(categoryAdapter);
        isActivity = true;
    }

    private void hideSpinnerDurationAndLoadData() {

        Spinner spinnerDuration = (Spinner) findViewById(R.id.activity_duration);
        TextView spinner_duration_label = (TextView) findViewById(R.id.spinner_duration_label);

        loadAdapterWithFoodCategories();
        spinnerDuration.setVisibility(View.INVISIBLE);
        spinner_duration_label.setVisibility(View.INVISIBLE);

        categorySpinner.setAdapter(categoryAdapter);
        isActivity = false;
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        radioButtonIsClicked = true;

        switch (view.getId()) {
            case R.id.radio_activity:
                if (checked) {
                    showSpinnerDurationAndLoadData();
                }
                break;
            case R.id.radio_food:
                if (checked) {
                    hideSpinnerDurationAndLoadData();
                }
                break;
        }

        categorySpinner.setAdapter(categoryAdapter);

    }

    private void loadAdapterWithActivityCategories() {
        categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.new_post_activity_categories, android.R.layout.simple_spinner_dropdown_item); //Cargar con las categorias de actividades
    }

    private void loadAdapterWithFoodCategories() {
        categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.new_post_food_categories, android.R.layout.simple_spinner_dropdown_item); //Cargar con las categorias de alimentos
    }

    private void assignActivitySourceAndInitData(Bundle saveInstanceState) {
        /*
        * Asigna el origen de la creación de esta vista
        * Inicializa los datos con respecto al origen
        * */
        //Creación del spinner de categorias
        categorySpinner = (Spinner) findViewById(R.id.category_spinner);

        prev = (ImageView) findViewById(R.id.imagePreview);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //requestCameraPermission();
            }
        });
        SOURCE = getIntent().getStringExtra("source"); //Obtiene el origen

        // Creación del spinner de frecuencias para un nuevo post
        frecuencySpinner = (Spinner) findViewById(R.id.frecuency_spinner);
        ArrayAdapter<CharSequence> frecuencyAdapter;
        frecuencyAdapter = ArrayAdapter.createFromResource(this, R.array.new_post_frecuencies, android.R.layout.simple_spinner_dropdown_item);


        //Configuración de adaptadores para cargar los datos
        frecuencySpinner.setAdapter(frecuencyAdapter);

        //Creación del spinner de duraciones para un nuevo post
        durationSpinner = (Spinner) findViewById(R.id.activity_duration);
        ArrayAdapter<CharSequence> durationAdapter;
        durationAdapter = ArrayAdapter.createFromResource(this, R.array.new_post_activity_duration, android.R.layout.simple_spinner_dropdown_item);
        //Configuración del adaptador de duraciones de una actividad
        durationSpinner.setAdapter(durationAdapter);


        TextView txt_name = (TextView) findViewById(R.id.textInputEditText);
        List foodList = Arrays.asList(getResources().getStringArray(R.array.new_post_food_categories));
        List activityList = Arrays.asList(getResources().getStringArray(R.array.new_post_activity_categories));
        List durationList = Arrays.asList(getResources().getStringArray(R.array.new_post_activity_duration));
        List frecuencyList = Arrays.asList(getResources().getStringArray(R.array.new_post_frecuencies));

        RadioButton radioActivity = (RadioButton) findViewById(R.id.radio_activity);
        RadioButton radioFood = (RadioButton) findViewById(R.id.radio_food);

        switch(SOURCE){
            case "reply":
                radioButtonIsClicked = true;
                imageForReply = getIntent().getStringExtra("image");
                String name = getIntent().getStringExtra("name");
                idForReply = getIntent().getStringExtra("id");
                typeForReply = getIntent().getStringExtra("type");
                resultForReply = getIntent().getIntExtra("result", 0);
                averageForReply = getIntent().getIntExtra("average", 0);

                txt_name.setEnabled(false);
                txt_name.setText(name);


                if (foodList.contains(typeForReply)) {
                    radioFood.setChecked(true);
                    hideSpinnerDurationAndLoadData();
                } else {
                    radioActivity.setSelected(true);
                    showSpinnerDurationAndLoadData();
                }

                radioActivity.setEnabled(false);
                radioFood.setEnabled(false);

                Glide.with(this).load(imageForReply).into(prev);
                break;
            case "camera":
                dispatchTakePictureIntent();
                break;
            case "update":


                Button btnDelete = (Button) findViewById(R.id.btn_delete);
                btnDelete.setVisibility(View.VISIBLE);

                final String mName = getIntent().getStringExtra("name");
                final String mCategory = getIntent().getStringExtra("category");
                final String mFrecuency = getIntent().getStringExtra("frecuency");
                final String mDuration = getIntent().getStringExtra("duration");
                final String mImage= getIntent().getStringExtra("image");
                idForUpdate = getIntent().getStringExtra("id");

                if(mName!=null){
                    txt_name.setText(mName);
                    txt_name.setEnabled(false);
                }

                if(mCategory!=null){

                    if (foodList.contains(mCategory)) {
                        radioFood.setChecked(true);
                        hideSpinnerDurationAndLoadData();
                        categorySpinner.setSelection(foodList.indexOf(mCategory));
                        frecuencySpinner.setSelection(frecuencyList.indexOf(mFrecuency));
                        isActivity = false;
                    } else {
                        radioActivity.setSelected(true);
                        showSpinnerDurationAndLoadData();
                        categorySpinner.setSelection(activityList.indexOf(mCategory));
                        durationSpinner.setSelection(durationList.indexOf(mDuration));
                        frecuencySpinner.setSelection(frecuencyList.indexOf(mFrecuency));
                        isActivity = true;
                    }
                    radioButtonIsClicked = true;
                    radioActivity.setEnabled(false);
                    radioFood.setEnabled(false);

                }

                thisRef = this;
                database = FirebaseDatabase.getInstance().getReference();

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new AlertDialog.Builder(thisRef)
                                .setIcon(R.drawable.ic_delete)
                                .setTitle("¿Estás seguro que deseas eliminar esta publicación?")
                                .setPositiveButton("Si", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        assignUserDataReference();
                                        if(mHome.user != null){
                                            database.child(mHome.user.getUid()).orderByChild("id").equalTo(idForUpdate).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    if(dataSnapshot.hasChildren()){
                                                        HashMap<String, Object> map = (HashMap)dataSnapshot.getValue();
                                                        SortedSet<String> keys = new TreeSet<String>(map.keySet());
                                                        String keyForDelete = keys.first();

                                                        database.child(mHome.user.getUid()).child(keyForDelete).removeValue(new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                //progress.dismiss();
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                        finish();

                                    }

                                })
                                .setNegativeButton("No", null)
                                .show();

                    }
                });

                if(mImage!=null)
                    Glide.with(this).load(mImage).into(prev);

                break;
            default:
                dispatchGaleryPicture();
                break;
        }

        if (!SOURCE.equals("reply") && !SOURCE.equals("update"))
            loadAdapterWithActivityCategories();


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

    private void configureToolbarAndActions() {
        /*
        *  Habilitar el toolbar para acciones y agregar evento de guardar post.
        * */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addSaveEventListener();

        changeStatusBarColor();
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private Post getPostData(String key, String downloadUrl) {
        Post post = null;
        if (isActivity) {
            View duration_view = durationSpinner.getSelectedView();
            if (duration_view != null && duration_view instanceof TextView) {
                CharSequence durationActivity;
                TextView activity_duration = (TextView) duration_view;
                durationActivity = activity_duration.getText();

                post = new Post(key, nameText.toString(), category.toString(), frecuency.toString(), downloadUrl + "", durationActivity.toString(), mHome.user.getUid(), resultForReply, averageForReply, "", "");
            }
        } else {
            post = new Post(key, nameText.toString(), category.toString(), frecuency.toString(), downloadUrl + "", mHome.user.getUid(), resultForReply, averageForReply);
        }
        return post;
    }

    private void assignUserPostsReference(){
        if(WelcomeActivity.CURRENT_APP_VERSION.equals("A"))
            database = database.child("user-posts");
        else
            database = database.child("user-posts-reflexive");
    }

    private void assignUserDataReference(){
        if(WelcomeActivity.CURRENT_APP_VERSION.equals("A"))
            database = database.child("user-data");
        else
            database = database.child("user-data-reflexive");
    }

    private void assignUserPostsReferenceForTest(){
        database = database.child("user-posts-reflexive-tests");
    }

    private void sumTocounterReply(String idForReply){
        assignUserPostsReference();
        database = database.child(idForReply);
        database.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Post p = mutableData.getValue(Post.class);

                if (p == null) {
                    return Transaction.success(mutableData);
                }
                p.replyCount = p.replyCount + 1;

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if(databaseError!=null){
                    if(getCurrentFocus()!=null){
                        Snackbar.make(getCurrentFocus(), "Revise su conexión a internet o intentelo más tarde", 2000).show();
                    }
                }else{
                    progress.dismiss();
                    finish();
                }
            }

        });

    }

    private void replyPost(){

        progress = ProgressDialog.show(that, "Compartiendo Publicación...",
                "Espera un momento", true);

        if (isOnline()) {
            database = FirebaseDatabase.getInstance().getReference();

            String dataKey;
            if(WelcomeActivity.CURRENT_APP_VERSION.equals("A"))
                dataKey = database.child("user-data").push().getKey();
            else
                dataKey = database.child("user-data-reflexive").push().getKey();

            Post post = getPostData(idForReply, imageForReply);
            Map<String, Object> mapForItems = post.toMap();


            Map<String, Object> mapForUpdate = new HashMap<>();

            if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
                mapForUpdate.put("/user-posts/" + idForReply + "/" + "last_share", mHome.user.getUid());
                mapForUpdate.put("/user-data/" + mHome.user.getUid() + "/" + dataKey, mapForItems);
            }else{
                mapForUpdate.put("/user-posts-reflexive/" + idForReply + "/" + "last_share", mHome.user.getUid());
                mapForUpdate.put("/user-data-reflexive/" + mHome.user.getUid() + "/" + dataKey, mapForItems);
            }

            database.updateChildren(mapForUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    sumTocounterReply(idForReply);
                }
            });


        } else {
            if(getCurrentFocus()!=null){
                Snackbar.make(getCurrentFocus(), "Revise su conexión a internet e intentelo más tarde", 2000).show();
            }
            progress.dismiss();
        }
    }

    private void addPostAndUploadImage(){
        progress = ProgressDialog.show(that, "Agregando Publicación...",
                "Espera un momento", true);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://reduccion-de-obesidad-7414c.appspot.com");


        prev.setDrawingCacheEnabled(true);
        prev.buildDrawingCache();
        Bitmap bitmap = prev.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] data = baos.toByteArray();


        Calendar cal = Calendar.getInstance();
        String date = cal.get(Calendar.YEAR) + "" + cal.get(Calendar.MONTH) + "" + cal.get(Calendar.DAY_OF_MONTH) + "" + cal.get(Calendar.HOUR) + "" + cal.get(Calendar.MINUTE) + "" + cal.get(Calendar.SECOND) + "";

        StorageReference imagesRef = storageRef.child("images/" + mHome.user.getUid() + "/" + date);

        if (isOnline()) {
            UploadTask uploadTask = imagesRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    if(getCurrentFocus() != null)
                        Snackbar.make(getCurrentFocus(), "Revise su conexión a internet e intentelo más tarde", 2000).show();
                    progress.dismiss();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    addPost(downloadUrl);

                }
            });
        } else {
            if(getCurrentFocus()!=null)
                Snackbar.make(getCurrentFocus(), "Revise su conexión a internet e intentelo más tarde", 2000).show();
            progress.dismiss();
        }
    }

    private void addPost(Uri downloadUrl){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String key;
        String dataKey;

        if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
            key = database.child("user-posts").push().getKey();
            dataKey = database.child("user-data").push().getKey();
        }else{
            key = database.child("user-posts-reflexive").push().getKey();
            dataKey = database.child("user-data-reflexive").push().getKey();
        }

        Post post = getPostData(key, downloadUrl.toString());

        if (post != null) {
            Map<String, Object> postValues = post.toMap();
            Map<String, Object> childUpdates = new HashMap<>();

            if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
                childUpdates.put("/user-posts/" + key, postValues);
                childUpdates.put("/user-data/" + mHome.user.getUid() + "/" + dataKey, postValues);
            }else{
                childUpdates.put("/user-posts-reflexive/" + key, postValues);
                childUpdates.put("/user-data-reflexive/" + mHome.user.getUid() + "/" + dataKey, postValues);
            }

            OnCompleteListener saveListener = new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if (task.isSuccessful()) {
                        progress.dismiss();
                        finish();

                    } else {
                        progress.dismiss();
                        if(getCurrentFocus()!=null)
                            Snackbar.make(getCurrentFocus(), "Revise su conexión a internet o intentelo más tarde", 2000).show();
                    }
                }
            };
            database.updateChildren(childUpdates).addOnCompleteListener(saveListener);
        }
    }

    private void updatePost(){

        if(!idForUpdate.equals("")){
            final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            DatabaseReference updateref = db;

            if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
                updateref = db.child("user-data").child(mHome.user.getUid());
            }else{
                updateref = db.child("user-data-reflexive").child(mHome.user.getUid());
            }

            updateref.orderByChild("id").equalTo(idForUpdate).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    progress = ProgressDialog.show(that, "Actualizando Publicación...",
                            "Espera un momento", true);

                    if(dataSnapshot.hasChildren()){
                        HashMap<String, Object> userMap = (HashMap)dataSnapshot.getValue();
                        SortedSet<String> keys = new TreeSet<String>(userMap.keySet());

                        HashMap postMap = (HashMap)userMap.get(keys.first());

                        if (postMap != null) {
                            if (isActivity) {
                                View duration_view = durationSpinner.getSelectedView();
                                if (duration_view != null && duration_view instanceof TextView) {
                                    CharSequence durationActivity;
                                    TextView activity_duration = (TextView) duration_view;
                                    durationActivity = activity_duration.getText();

                                    postMap.put("duration", durationActivity.toString());
                                }
                            }

                            postMap.put("frecuency",frecuency.toString());
                            postMap.put("category",category.toString());

                            Map<String, Object> childUpdates = new HashMap<>();

                            if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
                                childUpdates.put("/user-data/" + mHome.user.getUid() + "/" + keys.first(),postMap);
                            }else{
                                childUpdates.put("/user-data-reflexive/" + mHome.user.getUid() + "/" + keys.first(), postMap);
                            }

                            OnCompleteListener saveListener = new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                    if (task.isSuccessful()) {
                                        progress.dismiss();
                                        finish();

                                    } else {
                                        progress.dismiss();
                                        if(getCurrentFocus()!=null)
                                            Snackbar.make(getCurrentFocus(), "Revise su conexión a internet o intentelo más tarde", 2000).show();
                                    }
                                }
                            };
                            db.updateChildren(childUpdates).addOnCompleteListener(saveListener);
                        }
                    }else{

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else{
            if(getCurrentFocus()!=null)
                Snackbar.make(getCurrentFocus(), "Revise su conexión a internet o intentelo más tarde", 2000).show();
        }
    }

    private void addSaveEventListener() {
        /*
        * Evento para guardar una nueva publicación
        * */
        Button btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //Guardar los elementos del formulario en la base de datos  imageViewOne.getDrawable() == null

                if (mHome.user != null) {
                    if (userDataIsOK()) {

                        if (SOURCE.equals("reply")) {

                            replyPost();

                        } else if(SOURCE.equals("update")){

                            updatePost();


                        }else {

                            addPostAndUploadImage();

                        }

                    }
                }

            }
        });
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private boolean userDataIsOK() {
        if (radioButtonIsClicked) {
            if (nameIsOK() && categoryIsOk() && frecuencyIsOk()) {

                if (imageIsOk()) {
                    return true;
                } else {
                    if(getCurrentFocus()!=null)
                        Snackbar.make(getCurrentFocus(), "Es necesario que adjunte una imagen.", 4000).show();
                }

            } else {
                if(getCurrentFocus()!=null)
                    Snackbar.make(getCurrentFocus(), "Verifique la información ingresada.", 4000).show();
            }
        }else{
            if(getCurrentFocus()!=null)
                Snackbar.make(getCurrentFocus(), "Seleccione el tipo de publicación (actividad o alimento)", 4000).show();
        }

        return false;
    }

    private boolean categoryIsOk() {
        View category_view = categorySpinner.getSelectedView();
        if (category_view != null && category_view instanceof TextView) {
            TextView selectedTextView = (TextView) category_view;
            category = selectedTextView.getText();

            if (category.toString().trim().equals("")) {
                selectedTextView.setError("");
                return false;
            }
        }

        return true;
    }

    private boolean imageIsOk() {
        image = prev.getDrawable();
        return image != null;
    }

    private boolean frecuencyIsOk() {
        View frecuency_view = frecuencySpinner.getSelectedView();
        if (frecuency_view != null && frecuency_view instanceof TextView) {
            TextView selectedTextView = (TextView) frecuency_view;
            frecuency = selectedTextView.getText();

            if (frecuency.toString().trim().equals("")) {
                selectedTextView.setError("");
                return false;
            }
        }
        return true;
    }

    private boolean nameIsOK() {
        //Agregar la funcionalidad de validaciones
        final TextInputLayout nameLayout = (TextInputLayout) findViewById(R.id.txt_input_layour_name);
//        nameLayout.setErrorEnabled(true);

        //Obtener el nombre ingresado
        EditText nameEditText = nameLayout.getEditText();
        if(nameEditText!=null){
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

            nameText = nameEditText.getText();

            if (nameText.toString().trim().equals("")) { //Nombre vacío
                nameLayout.setError(getString(R.string.add_post_validate_name));
            } else if ((nameText.length() > 60)) {//Nombre de longitud incorrecta
                nameLayout.setError(getString(R.string.add_post_validate_name_length));
            } else if (nameText.toString().contains("\n")) {//Nombre con saltos de linea
                nameLayout.setError(getString(R.string.add_post_validate_line_breaks));
            } else {
                return true;
            }

        }

        return false;

    }


    @Override
    public boolean onSupportNavigateUp() {
        /*
        * Habilitar la navegación en esta vista
        * */
        finish();
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        * Este método es llamado cuando se obtiene un resultado de otra vista, ej: tomar foto, adjuntar foto de galería
        * */
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_IMAGE_CAPTURE) {

                setPic(mCurrentPhotoPath);
                //galleryAddPic();

            }else if(requestCode == RESULT_LOAD_IMAGE){

                Uri targetUri = data.getData();
                setPic(getRealPathFromURI(targetUri));

            }

        } else {
            finish();
        }
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void setPic(String path) {
        // Get the dimensions of the View
        int targetW = 340;
        int targetH = 200;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);

        try {
            ExifInterface exifReader = new ExifInterface(path);
            int orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

            Bitmap rotated;

            if(bitmap !=null){
                if(orientation == ExifInterface.ORIENTATION_ROTATE_90)
                    rotated = ConfigurationActivity.rotateBitmap(bitmap, 90);
                else if(orientation == ExifInterface.ORIENTATION_ROTATE_270)
                    rotated = ConfigurationActivity.rotateBitmap(bitmap, 270);
                else if(orientation == ExifInterface.ORIENTATION_ROTATE_180)
                    rotated = ConfigurationActivity.rotateBitmap(bitmap, 180);
                else
                    rotated = ConfigurationActivity.rotateBitmap(bitmap, 0);

                prev.setImageBitmap(rotated);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadImageResultInImageView(Intent data, int orientation) {

        try {
            final Uri imageUri = data.getData();
            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

            int nh = (int) (selectedImage.getHeight() * (512.0 / selectedImage.getWidth()));


            Bitmap scaled = Bitmap.createScaledBitmap(selectedImage, 512, nh, true);
            Bitmap rotated;

            if(orientation == 90)
                rotated = ConfigurationActivity.rotateBitmap(scaled, 90);
            else if(orientation == 270)
                rotated = ConfigurationActivity.rotateBitmap(scaled, 270);
            else
                rotated = ConfigurationActivity.rotateBitmap(scaled, 0);

            prev.setImageBitmap(rotated);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void dispatchTakePictureIntent() {

        if (Build.VERSION.SDK_INT >= 21) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                requestCameraPermission();
                return;
            }
        }

        startCamera();
    }


    private void dispatchGaleryPicture() {
        /*
        * Habilita la galería de fotos para adjuntar una foto.
        * */

        if (Build.VERSION.SDK_INT >= 21) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                requestMediaPermission();
                return;
            }
        }
        startMedia();
    }

    private void requestMediaPermission(){
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_STORAGE_PERMISSION);
    }


    private void requestCameraPermission() {
       /* if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
            new ConfirmationDialog().show(getFragmentManager(),FRAGMENT_DIALOG);
        } else {*/
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION);
        //}
    }

    private void startMedia(){

        /*Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(i.resolveActivity(getPackageManager()) != null){
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }*/

        Intent takePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        takePictureIntent.setType("image/*");

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        FILE_PROVIDER,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, RESULT_LOAD_IMAGE);
            }
        }
    }

    private void startCamera(){
        /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }*/

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        FILE_PROVIDER,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startCamera();

                } else if(ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])){

                    ErrorDialog.newInstance(getString(R.string.request_permission))
                            .show(getFragmentManager(), FRAGMENT_DIALOG);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }else{
                    ErrorDialog.newInstance(getString(R.string.permission_indication))
                            .show(getFragmentManager(), FRAGMENT_DIALOG);

                }
                break;
            }
            case REQUEST_STORAGE_PERMISSION:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startMedia();

                } else if(ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])){

                    ErrorDialog.newInstance(getString(R.string.request_permission))
                            .show(getFragmentManager(), FRAGMENT_DIALOG);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }else{
                    ErrorDialog.newInstance(getString(R.string.permission_indication))
                            .show(getFragmentManager(), FRAGMENT_DIALOG);
                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    public static class ConfirmationDialog extends DialogFragment {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity parent = getActivity();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.request_permission)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(parent,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA_PERMISSION);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity activity = parent;
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                }
                            })
                    .create();
        }
    }

    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }

    }

}
