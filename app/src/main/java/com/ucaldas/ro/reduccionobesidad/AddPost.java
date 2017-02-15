package com.ucaldas.ro.reduccionobesidad;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ucaldas.ro.reduccionobesidad.R.id.imageView;
import static java.util.Arrays.asList;

public class AddPost extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1; //Bandera para verificar en los resultados de actividad si se ha tomado una foto.
    static final int RESULT_LOAD_IMAGE = 2; //Bandera para verificar en los resultados de actividad si se ha cargado una foto de la galería
    private String SOURCE = ""; //Indica el origen de un llamado, con el objetivo de reutilizar la vista.
    private boolean isActivity;
    private boolean radioButtonIsClicked = false;

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

    private ArrayAdapter<CharSequence> categoryAdapter;

    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post); //Asociar esta vista para el control de la interface

        //Log.v("AUser", mHome.user.getUid());

        configureToolbarAndActions();
        assignActivitySourceAndInitData();

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
        isActivity = true;
    }

    private void hideSpinnerDurationAndLoadData() {

        Spinner spinnerDuration = (Spinner) findViewById(R.id.activity_duration);
        TextView spinner_duration_label = (TextView) findViewById(R.id.spinner_duration_label);

        loadAdapterWithFoodCategories();
        spinnerDuration.setVisibility(View.INVISIBLE);
        spinner_duration_label.setVisibility(View.INVISIBLE);
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

    private void assignActivitySourceAndInitData() {
        /*
        * Asigna el origen de la creación de esta vista
        * Inicializa los datos con respecto al origen
        * */
        //Creación del spinner de categorias
        categorySpinner = (Spinner) findViewById(R.id.category_spinner);
        prev = (ImageView) findViewById(R.id.imagePreview);
        SOURCE = getIntent().getStringExtra("source"); //Obtiene el origen
        //isActivity = true; //Inicialmente se configura el post como una publicación de actividad

        if (SOURCE.equals("reply")) {
            radioButtonIsClicked = true;
            imageForReply = getIntent().getStringExtra("image");
            String name = getIntent().getStringExtra("name");
            idForReply = getIntent().getStringExtra("id");
            typeForReply = getIntent().getStringExtra("type");
            resultForReply = getIntent().getIntExtra("result", 0);


            Log.v("DBP", resultForReply + "");
            averageForReply = getIntent().getIntExtra("average", 0);

            TextView txt_name = (TextView) findViewById(R.id.textInputEditText);
            txt_name.setEnabled(false);
            txt_name.setText(name);

            RadioButton radioActivity = (RadioButton) findViewById(R.id.radio_activity);
            RadioButton radioFood = (RadioButton) findViewById(R.id.radio_food);

            List foodList = Arrays.asList(getResources().getStringArray(R.array.new_post_food_categories));

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


        } else if (SOURCE.equals("camera")) { //¿El origen es de foto?
            dispatchTakePictureIntent();
        } else { // ¿El origen es de galería?
            dispatchGaleryPicture();
        }

        if (!SOURCE.equals("reply"))
            loadAdapterWithActivityCategories();

        categorySpinner.setAdapter(categoryAdapter);

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }



    private Post getPostData(String key, String downloadUrl) {
        Post post = null;
        if (isActivity) {
            View duration_view = durationSpinner.getSelectedView();
            if (duration_view != null && duration_view instanceof TextView) {
                CharSequence durationActivity = "";
                TextView activity_duration = (TextView) duration_view;
                durationActivity = activity_duration.getText();

                post = new Post(key, nameText.toString(), category.toString(), frecuency.toString(), downloadUrl + "", durationActivity.toString(), mHome.user.getUid(), resultForReply, averageForReply, "", "");
            }
        } else {
            post = new Post(key, nameText.toString(), category.toString(), frecuency.toString(), downloadUrl + "", mHome.user.getUid(), resultForReply, averageForReply, "", "");
        }
        return post;
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
                            progress = ProgressDialog.show(that, "Compartiendo Publicación...",
                                    "Espera un momento", true);

                            if (isOnline()) {
                                DatabaseReference datRef = FirebaseDatabase.getInstance().getReference();
                                String dataKey = "";
                                if(WelcomeActivity.CURRENT_APP_VERSION.equals("A"))
                                    dataKey = datRef.child("user-data").push().getKey();
                                else
                                    dataKey = datRef.child("user-data-reflexive").push().getKey();

                                Post post = getPostData(idForReply, imageForReply);
                                Map<String, Object> mapForItems = post.toMap();

                                Log.v("db", idForReply);

                                Map<String, Object> mapForUpdate = new HashMap<>();
                                mapForUpdate.put("/user-posts/" + idForReply + "/" + "last_share", mHome.user.getUid());
                                mapForUpdate.put("/user-data/" + mHome.user.getUid() + "/" + dataKey, mapForItems);


                                //mapForUpdate.put("user-data/"+mHome.user.getUid()+"/", );

                                datRef.updateChildren(mapForUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progress.dismiss();
                                        finish();
                                    }
                                });


                            } else {
                                Snackbar.make(getCurrentFocus(), "Revise su conexión a internet e intentelo más tarde", 2000).show();
                                progress.dismiss();
                            }


                        } else {

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
                                        Log.v("Add", "Error");
                                        Snackbar.make(getCurrentFocus(), "Revise su conexión a internet e intentelo más tarde", 2000).show();
                                        progress.dismiss();

                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Log.v("ST", "satisfactorio");

                                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                        Log.v("ST", downloadUrl + "");

                                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                        String key = "";
                                        String dataKey = "";

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
                                            //addSaveEventListener();

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
                                                        Log.v("DB", task.getResult() + "");
                                                        progress.dismiss();
                                                        Snackbar.make(getCurrentFocus(), "Revise su conexión a internet o intentelo más tarde", 2000).show();
                                                    }
                                                }
                                            };
                                            database.updateChildren(childUpdates).addOnCompleteListener(saveListener);
                                        }

                                    }
                                });
                            } else {
                                Snackbar.make(getCurrentFocus(), "Revise su conexión a internet e intentelo más tarde", 2000).show();
                                progress.dismiss();
                            }


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
                    Snackbar.make(getCurrentFocus(), "Es necesario que adjunte una imagen.", 4000).show();
                }

            } else {
                Snackbar.make(getCurrentFocus(), "Verifique la información ingresada.", 4000).show();
            }
        }else{
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        * Este método es llamado cuando se obtiene un resultado de otra vista, ej: tomar foto, adjuntar foto de galería
        * */

        if (resultCode == RESULT_OK) { //¿Se retornó de tomar una foto?

            if (requestCode == REQUEST_IMAGE_CAPTURE || requestCode == RESULT_LOAD_IMAGE) {

                loadImageResultInImageView(prev, data);
            }

        } else {
            finish();
        }
    }

    private void loadImageResultInImageView(ImageView imageView, Intent data) {

        try {
            final Uri imageUri = data.getData();
            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

            int nh = (int) (selectedImage.getHeight() * (512.0 / selectedImage.getWidth()));
            Bitmap scaled = Bitmap.createScaledBitmap(selectedImage, 512, nh, true);

            imageView.setImageBitmap(scaled);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void dispatchTakePictureIntent() {
        /*
        * Habilita la cámara en el celular para la toma de fotos.
        * */
        // Here, thisActivity is the current activity
        /*if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                Log.v("Permissions", "Permiso");

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }*/
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }


                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.v("Permissions", "Permiso negado");
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void dispatchGaleryPicture() {
        /*
        * Habilita la galería de fotos para adjuntar una foto.
        * */
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

}
