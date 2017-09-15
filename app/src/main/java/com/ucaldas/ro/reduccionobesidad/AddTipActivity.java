package com.ucaldas.ro.reduccionobesidad;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddTipActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private Button saveButton;
    private TextInputEditText title;
    private EditText descriptionC;
    private Spinner tipType;
    private ImageView imagePreview;
    private Spinner appSpinner; 


    private ArrayAdapter<CharSequence> typesAdapter;
    private ArrayAdapter<CharSequence> appAdapter;

    private final String FILE_PROVIDER = "com.ucaldas.android.fileprovider";
    private static final int REQUEST_STORAGE_PERMISSION = 2;
    static final int RESULT_LOAD_IMAGE = 2;
    private static final String FRAGMENT_DIALOG = "dialog";

    private ProgressDialog progress;
    final AppCompatActivity that = this;

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
        appSpinner = (Spinner) findViewById(R.id.app_spinner);

        typesAdapter = ArrayAdapter.createFromResource(this,
                R.array.tips_types, android.R.layout.simple_spinner_dropdown_item);

        appAdapter = ArrayAdapter.createFromResource(this,
                R.array.app_types, android.R.layout.simple_spinner_dropdown_item);

        tipType.setAdapter(typesAdapter);
        appSpinner.setAdapter(appAdapter);
    }

    private void createSaveButtonListener() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = title.getText().toString();
                final String type = tipType.getSelectedItem().toString();
                final String description = descriptionC.getText().toString();
                final Drawable image = imagePreview.getDrawable();
                final String app = appSpinner.getSelectedItem().toString();

                if (formIsValid(name, type, description, image)) {
                    saveButton.setEnabled(false);
                    progress = ProgressDialog.show(that, "Agregando anuncio...",
                            "Espera un momento", true);

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://reduccion-de-obesidad-7414c.appspot.com");

                    imagePreview.setDrawingCacheEnabled(true);
                    imagePreview.buildDrawingCache();
                    Bitmap bitmap = imagePreview.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                    byte[] data = baos.toByteArray();


                    Calendar cal = Calendar.getInstance();
                    String date = cal.get(Calendar.YEAR) + "" + cal.get(Calendar.MONTH) + "" + cal.get(Calendar.DAY_OF_MONTH) + "" + cal.get(Calendar.HOUR) + "" + cal.get(Calendar.MINUTE) + "" + cal.get(Calendar.SECOND) + "";

                    StorageReference imagesRef = storageRef.child("tips/" +  date);

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

                                @SuppressWarnings("VisibleForTests") final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                addTip(name, type, description, downloadUrl, app);

                            }
                        });
                    } else {
                        if(getCurrentFocus()!=null)
                            Snackbar.make(getCurrentFocus(), "Revise su conexión a internet e intentelo más tarde", 2000).show();
                        progress.dismiss();
                    }


                }
            }
        });
    }

    private void addTip(String name, String type, String description, Uri downloadUrl, String app){

        String key = database.push().getKey();
        Tip tip = new Tip(key, name, type, description, downloadUrl.toString(), app);

        Map<String, Object> postValues = tip.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(key, postValues);

        OnCompleteListener saveListener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if (task.isSuccessful()) {
                    progress.dismiss();
                    saveButton.setEnabled(true);
                    finish();

                } else {
                    progress.dismiss();
                    saveButton.setEnabled(true);
                    if(getCurrentFocus()!=null)
                        Snackbar.make(getCurrentFocus(), "Revise su conexión a internet o intentelo más tarde", 2000).show();
                }
            }
        };

        database.updateChildren(childUpdates).addOnCompleteListener(saveListener);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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
