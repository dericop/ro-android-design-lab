package com.ucaldas.ro.reduccionobesidad;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddTipActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private Button saveButton;
    private TextInputEditText title;
    private EditText descriptionC;
    private Spinner tipType;
    private ImageView imagePreview;

    private ArrayAdapter<CharSequence> typesAdapter;
    private final String FILE_PROVIDER = "com.ucaldas.android.cocono.fileprovider";
    private static final int REQUEST_STORAGE_PERMISSION = 2;
    static final int RESULT_LOAD_IMAGE = 2;
    private static final String FRAGMENT_DIALOG = "dialog";

    //Atributos de base de datos
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tip);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //Esconder el teclado
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initGraphicalComponents();
        createSaveButtonListener();

        dispatchGaleryPicture();

        startDatabase();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private void startDatabase(){
        database = FirebaseDatabase.getInstance().getReference();
        database = database.child("tips");
    }

    private void initGraphicalComponents() {
        saveButton = (Button) findViewById(R.id.btn_save);
        title = (TextInputEditText) findViewById(R.id.title);
        tipType = (Spinner) findViewById(R.id.spinner_tip_type);
        descriptionC = (EditText) findViewById(R.id.desc_edit);
        imagePreview = (ImageView) findViewById(R.id.imagePreview);

        typesAdapter = ArrayAdapter.createFromResource(this,
                R.array.tips_types, android.R.layout.simple_spinner_dropdown_item);

        tipType.setAdapter(typesAdapter);
    }

    private void createSaveButtonListener() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = title.getText().toString();
                String type = tipType.getSelectedItem().toString();
                String description = descriptionC.getText().toString();
                Drawable image = imagePreview.getDrawable();
                String app;

                if(WelcomeActivity.CURRENT_APP_VERSION.equals("A"))
                    app = "A";
                else
                    app = "R";

                if (formIsValid(name, type, description, image)) {
                    Tip tip = new Tip("", name, type, description, "", app);
                    
                }
            }
        });
    }

    private boolean formIsValid(String name, String type, String description, Drawable image) {
        if (image != null) {
            if (!name.equals("") && !type.equals("")) {
                if (!description.equals("")) {
                    return true;
                } else {
                    descriptionC.setError("La descripción es un campo requerido");
                }
            } else {
                title.setError("El nombre es un campo requerido");
            }
        } else {
            showSnackBar("Es necesario que adjunte una imagen");
        }

        return false;
    }


    private void showSnackBar(String message) {
        if (getCurrentFocus() != null) {
            Snackbar.make(getCurrentFocus(), message, 2000).show();
        }
    }

    private void dispatchGaleryPicture() {
        /*
        * Habilita la galería de fotos para adjuntar una foto.
        * */

        if (Build.VERSION.SDK_INT >= 21) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                requestMediaPermission();
                return;
            }
        }
        startMedia();
    }

    private void requestMediaPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_STORAGE_PERMISSION);
    }

    private void startMedia() {

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



        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        * Este método es llamado cuando se obtiene un resultado de otra vista, ej: tomar foto, adjuntar foto de galería
        * */
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            Uri targetUri = data.getData();
            setPic(getRealPathFromURI(targetUri));

        } else{
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
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);

        try {
            ExifInterface exifReader = new ExifInterface(path);
            int orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

            Bitmap rotated;

            if (bitmap != null) {
                if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
                    rotated = ConfigurationActivity.rotateBitmap(bitmap, 90);
                else if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                    rotated = ConfigurationActivity.rotateBitmap(bitmap, 270);
                else if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                    rotated = ConfigurationActivity.rotateBitmap(bitmap, 180);
                else
                    rotated = ConfigurationActivity.rotateBitmap(bitmap, 0);

                imagePreview.setImageBitmap(rotated);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startMedia();

                } else if(ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])){

                    AddPost.ErrorDialog.newInstance(getString(R.string.request_permission))
                            .show(getFragmentManager(), FRAGMENT_DIALOG);

                }else{
                    AddPost.ErrorDialog.newInstance(getString(R.string.permission_indication))
                            .show(getFragmentManager(), FRAGMENT_DIALOG);
                }
                break;
            }
        }
    }



}
