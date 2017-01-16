package com.ucaldas.ro.reduccionobesidad;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AddPost extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1; //Bandera para verificar en los resultados de actividad si se ha tomado una foto.
    static final int RESULT_LOAD_IMAGE = 2; //Bandera para verificar en los resultados de actividad si se ha cargado una foto de la galería
    String SOURCE = ""; //Indica el origen de un llamado, con el objetivo de reutilizar la vista.

    final AppCompatActivity that = this; //Guardar el contexto para los menú de alerta.
    CharSequence categories[]; //Arreglo con las categorias (Dependen de SOURCE)
    CharSequence frecuencies[]; // Arreglo con las frecuencias (Dependen de SOURCE)

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
        SOURCE = getIntent().getStringExtra("SOURCE_ID"); //Obtiene el origen

        if(SOURCE.equals("activities")){ //¿El origen es de actividades?

            setTitle(getString(R.string.new_post_activity_toolbar_title)); //Actualiza el titulo general
            categories = getBaseContext().getResources().getStringArray(R.array.new_post_activity_categories); //Cargar el listado de categorías de actividades

        }else{ //¿El origen es de alimentos?

            setTitle(getString(R.string.new_post_food_toolbar_title)); //Actualiza el titulo general
            //Cambiar el color del toolbar
            Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
            tool.setBackgroundColor(getResources().getColor(R.color.toolbarColorFood));

            categories = getBaseContext().getResources().getStringArray(R.array.new_post_food_categories);//Cargar el listado de categorias de alimentos
        }
        frecuencies = getBaseContext().getResources().getStringArray(R.array.new_post_frecuencies);//Carga el listado de frecuencias
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
        /*
        * Inicializa las acciones y eventos para popUp.
        * */
        final TextView txtCategory = (TextView) findViewById(R.id.txtCategory);
        final TextView txtFrecuency = (TextView) findViewById(R.id.txtFrecuency);

        //Creación del evento de click para categorias.
        txtCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopUp(getString(R.string.subtitle_choose_categories), categories, txtCategory); //Mostrar popUp
            }
        });

        //Creación del evento de click para frecuencias
        txtFrecuency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopUp(getString(R.string.subtitle_choose_frecuencies), frecuencies, txtFrecuency); //Mostrar popUp
            }
        });

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
            public void onClick(View view) {
                //Guardar los elementos del formulario en la base de datos
                finish();
            }
        });
    }

    private void createPopUp(String title, final CharSequence data[], final TextView viewToUpdate){
        /*
        * Crear un popUp genérico con los datos enviados por parámetro
        * @param title: Título que aparecerá  en el popUp
        * @param data: Arreglo de datos para mostrar como opciones en el popUp
        * @param viewToUpdate: Vista que contendrá el texto clickeado.
        * */
        AlertDialog.Builder builder = new AlertDialog.Builder(that);
        builder.setTitle(title);
        builder.setItems(data, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewToUpdate.setText(data[which]);
            }
        });

        builder.show();
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

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) { //¿Se retornó de tomar una foto?
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ImageView prev = (ImageView) findViewById(R.id.imagePreview);
            prev.setImageBitmap(imageBitmap);
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
