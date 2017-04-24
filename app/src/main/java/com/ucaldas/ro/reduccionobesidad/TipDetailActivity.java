package com.ucaldas.ro.reduccionobesidad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

public class TipDetailActivity extends AppCompatActivity {

    private Button delete;
    private TextView descriptionC;
    private ImageView imageC;

    private DatabaseReference database;
    private AppCompatActivity that = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String image = getIntent().getStringExtra("image");
        String id = getIntent().getStringExtra("id");

        descriptionC = (TextView) findViewById(R.id.description);
        imageC = (ImageView) findViewById(R.id.image);

        descriptionC.setText(description);
        Glide.with(getApplicationContext()).load(image).into(imageC);

        toolbar.setTitle(title);

        setSupportActionBar(toolbar);
        configureToolbarAndActions(id);
    }

    private void configureToolbarAndActions(final String id) {
        changeStatusBarColor();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        database = FirebaseDatabase.getInstance().getReference();

        delete = (Button) findViewById(R.id.btn_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(that)
                        .setIcon(R.drawable.ic_delete)
                        .setTitle("¿Estás seguro que deseas eliminar este anuncio?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (mHome.user != null) {
                                    database.child("tips").orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.hasChildren()) {
                                                HashMap<String, Object> map = (HashMap) dataSnapshot.getValue();
                                                SortedSet<String> keys = new TreeSet<String>(map.keySet());
                                                String keyForDelete = keys.first();

                                                Log.v("Delete", keyForDelete);

                                                database.child("tips").child(keyForDelete).removeValue(new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

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

        if (!mHome.isAdmin)
            delete.setVisibility(View.GONE);
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

}
