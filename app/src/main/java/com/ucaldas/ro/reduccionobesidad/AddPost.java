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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AddPost extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int RESULT_LOAD_IMAGE = 2;

    String SOURCE = "";

    final AppCompatActivity that = this;
    CharSequence categories[];
    CharSequence frecuencies[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        changeStatusBarColor();

        configureToolbarAndActions();
        assignActivitySourceAndInitData();
        initializePopUpActions();
    }

    private void assignActivitySourceAndInitData(){
        SOURCE = getIntent().getStringExtra("SOURCE_ID");

        if(SOURCE.equals("activities")){
            setTitle("Nueva Actividad");
            categories = new CharSequence[]{"Deporte", "Entretenimiento", "Trabajo", "Estudio"};
        }else{
            setTitle("Nueva Comida");

            Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
            tool.setBackgroundColor(getResources().getColor(R.color.toolbarColorFood));



            categories = new CharSequence[]{"Desayuno", "Almuerzo", "Comida", "Algo"};
        }

        frecuencies = new CharSequence[]{"Todos los días", "Casi todos los días", "2-3 veces a la semana", "1 vez a la semana", "1 vez cada dos semanas", "1 vez al mes", "Ocasionalmente"};
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void initializePopUpActions(){
        final TextView txtCategory = (TextView) findViewById(R.id.txtCategory);
        final TextView txtFrecuency = (TextView) findViewById(R.id.txtFrecuency);


        txtCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopUp("Elige una categoría", categories, txtCategory);
            }
        });


        txtFrecuency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopUp("Elige una frecuencia", frecuencies, txtFrecuency);
            }
        });

        ImageView prev = (ImageView) findViewById(R.id.imagePreview);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPhotoOptions();
            }
        });
    }

    private void configureToolbarAndActions(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button btn_save = (Button) toolbar.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Guardar los elementos del formulario en la base de datos
                finish();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    private void createPopUp(String title, final CharSequence data[], final TextView viewToUpdate){

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
        CharSequence options[] = new CharSequence[]{"Nueva Foto", "Elegir de la galeria"};

        AlertDialog.Builder builder = new AlertDialog.Builder(that);
        builder.setTitle("Elija una opción:");
        builder.setItems(options, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which){
                    case 0:
                        dispatchTakePictureIntent();
                        break;
                    case 1:
                        dispatchGaleryPicture();
                        break;
                }
            }
        });

        builder.show();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.v("Request", )

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ImageView prev = (ImageView) findViewById(R.id.imagePreview);
            prev.setImageBitmap(imageBitmap);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void dispatchGaleryPicture(){
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);

    }

}
