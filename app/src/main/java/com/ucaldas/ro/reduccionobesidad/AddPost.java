package com.ucaldas.ro.reduccionobesidad;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

import static com.ucaldas.ro.reduccionobesidad.R.id.imageView;

public class AddPost extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1; //Bandera para verificar en los resultados de actividad si se ha tomado una foto.
    static final int RESULT_LOAD_IMAGE = 2; //Bandera para verificar en los resultados de actividad si se ha cargado una foto de la galería
    private String SOURCE = ""; //Indica el origen de un llamado, con el objetivo de reutilizar la vista.

    //Datos que el usuario va a ingresar para la publiación
    private Spinner frecuencySpinner;
    private Spinner categorySpinner;
    private ImageView prev;

    private CharSequence nameText;
    private CharSequence category;
    private CharSequence frecuency;
    private Drawable image;


    final AppCompatActivity that = this; //Guardar el contexto para los menú de alerta.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post); //Asociar esta vista para el control de la interface

        configureToolbarAndActions();
        assignActivitySourceAndInitData();
        initializePopUpActions();


    }

    private void assignActivitySourceAndInitData(){
        /*
        * Asigna el origen de la creación de esta vista
        * Inicializa los datos con respecto al origen
        * */
        prev = (ImageView) findViewById(R.id.imagePreview);
        SOURCE = getIntent().getStringExtra("SOURCE_ID"); //Obtiene el origen

        //Creación del spinner de categorias
        categorySpinner = (Spinner) findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> adapter;

        if(SOURCE.equals("activities")){ //¿El origen es de actividades?
            setTitle(getString(R.string.new_post_activity_toolbar_title)); //Actualiza el titulo general
            adapter = ArrayAdapter.createFromResource(this,
                    R.array.new_post_activity_categories, android.R.layout.simple_spinner_dropdown_item); //Cargar con las categorias de actividades

        }else{ //¿El origen es de alimentos?
            adapter = ArrayAdapter.createFromResource(this,
                    R.array.new_post_food_categories, android.R.layout.simple_spinner_dropdown_item); //Cargar con las categorias de alimentos

            setTitle(getString(R.string.new_post_food_toolbar_title)); //Actualiza el titulo general

            //Cambiar el color del toolbar
            Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
            tool.setBackgroundColor(getResources().getColor(R.color.toolbarColorFood));

        }

        // Creación del spinner de frecuencias para un nuevo post
        frecuencySpinner = (Spinner) findViewById(R.id.frecuency_spinner);
        ArrayAdapter<CharSequence> frecuencyAdapter;
        frecuencyAdapter = ArrayAdapter.createFromResource(this, R.array.new_post_frecuencies, android.R.layout.simple_spinner_dropdown_item);

        //Configuración de adaptadores para cargar los datos
        frecuencySpinner.setAdapter(frecuencyAdapter);
        categorySpinner.setAdapter(adapter);

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

    private void initializePopUpActions(){

        //Creación del evento de click para el menu de tomar fotos
        ImageView prev = (ImageView) findViewById(R.id.imagePreview);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPhotoOptions();
            }
        });
    }

    private void configureToolbarAndActions(){
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

    private void addSaveEventListener(){
        /*
        * Evento para guardar una nueva publicación
        * */

        Button btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
            //Guardar los elementos del formulario en la base de datos  imageViewOne.getDrawable() == null

            if(userDataIsOK()){

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://reduccion-de-obesidad-7414c.appspot.com");
                StorageReference imagesRef = storageRef.child("images");

                prev.setDrawingCacheEnabled(true);
                prev.buildDrawingCache();
                Bitmap bitmap = prev.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);

                byte[] data = baos.toByteArray();

                UploadTask uploadTask = imagesRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.v("ST", "problemas");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.v("ST", "satisfactorio");

                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.v("ST",downloadUrl + "" );
                    }
                });

                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                String key = database.child("user-posts").push().getKey();

                Post post = new Post(nameText.toString(), category.toString(), frecuency.toString(), 1);
                Map<String, Object> postValues = post.toMap();

                Map<String, Object> childUpdates = new HashMap<>();

                String userId = mHome.user.getUid();
                childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

                OnCompleteListener saveListener = new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            //finish();

                        }else{
                            Log.v("DB", task.getResult() + "");
                            Toast.makeText(getBaseContext(), "Revisa tu conexión a internet o intentalo más tarde", Toast.LENGTH_LONG);
                        }
                    }
                };
                database.updateChildren(childUpdates).addOnCompleteListener(saveListener);

            }



            }
        });
    }


    private boolean userDataIsOK(){
        if (nameIsOK() && categoryIsOk() && frecuencyIsOk()){
            if(imageIsOk()){
                return true;
            }else{
                Snackbar.make(getCurrentFocus(), "Es necesario que adjunte una imagen.", 4000).show();
            }

        }else{
            Snackbar.make(getCurrentFocus(), "Verifique la información ingresada.", 4000).show();
        }

        return false;
    }

    private boolean categoryIsOk(){
        View category_view = categorySpinner.getSelectedView();
        if (category_view != null && category_view instanceof TextView) {
            TextView selectedTextView = (TextView) category_view;
            category = selectedTextView.getText();

            if(category.toString().trim().equals(getString(R.string.add_post_category_default_value))){
                selectedTextView.setError("");
                return false;
            }
        }

        return true;
    }

    private boolean imageIsOk(){
        image = prev.getDrawable();
        return image != null;
    }

    private boolean frecuencyIsOk(){
        View frecuency_view = frecuencySpinner.getSelectedView();
        if(frecuency_view != null && frecuency_view instanceof TextView){
            TextView selectedTextView = (TextView) frecuency_view;
            frecuency = selectedTextView.getText();
            if(frecuency.toString().trim().equals(getString(R.string.add_post_frecuency_default_value))){
                selectedTextView.setError("");
                return false;
            }
        }
        return true;
    }

    private boolean nameIsOK(){
        //Agregar la funcionalidad de validaciones
        final TextInputLayout nameLayout = (TextInputLayout) findViewById(R.id.txt_input_layour_name);
        nameLayout.setErrorEnabled(true);

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

        if(nameText.toString().trim().equals("")){ //Nombre vacío
            nameLayout.setError(getString(R.string.add_post_validate_name));
        }else if((nameText.length() > 60)){//Nombre de longitud incorrecta
            nameLayout.setError(getString(R.string.add_post_validate_name_length));
        }else if(nameText.toString().contains("\n")){//Nombre con saltos de linea
            nameLayout.setError(getString(R.string.add_post_validate_line_breaks));
        }else{
            return true;
        }

        return false;

    }

    private void createPhotoOptions(){
         /*
        * Crear un popUp con opciones para tomar fotos o adjuntar archivos
        * */


        CharSequence options[] = getBaseContext().getResources().getStringArray(R.array.new_post_photo_options); //Cargar las opciones para el menú de fotos.

        AlertDialog.Builder builder = new AlertDialog.Builder(that);
        builder.setTitle(getString(R.string.new_post_choose_option));
        builder.setItems(options, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which){
                    case 0: //¿Presionaron la opción de nueva foto?
                        dispatchTakePictureIntent();
                        break;
                    case 1: //¿Presionaron la opción de adjuntar de la galería?
                        dispatchGaleryPicture();
                        break;
                }
            }
        });

        builder.show();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        * Este método es llamado cuando se obtiene un resultado de otra vista, ej: tomar foto, adjuntar foto de galería
        * */

        if (resultCode == RESULT_OK) { //¿Se retornó de tomar una foto?

            if(requestCode == REQUEST_IMAGE_CAPTURE || requestCode == RESULT_LOAD_IMAGE){

                loadImageResultInImageView(prev, data);

            }

        }
    }

    private void loadImageResultInImageView(ImageView imageView, Intent data){

        try {
            final Uri imageUri = data.getData();
            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            imageView.setImageBitmap(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    private void dispatchTakePictureIntent() {
        /*
        * Habilita la cámara en el celular para la toma de fotos.
        * */
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void dispatchGaleryPicture(){
        /*
        * Habilita la galería de fotos para adjuntar una foto.
        * */
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

}
