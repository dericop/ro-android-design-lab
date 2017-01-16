package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddMenu extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        changeStatusBarColor();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        configureGrids();

    }


    private void configureGrids(){
        //Gridview de actividades
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ItemAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
            /* Toast.makeText(HelloGridView.this, "" + position,
                    Toast.LENGTH_SHORT).show();*/
                switch(position){
                    case 0:
                        Intent intent = new Intent(getBaseContext(), AddPost.class);
                        intent.putExtra("SOURCE_ID", "activities");
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intentL = new Intent(getBaseContext(), LibraryActivity.class);
                        intentL.putExtra("SOURCE_ID", "activities");
                        startActivity(intentL);
                        break;
                }

            }
        });


        //GridView de alimentos
        GridView gridViewFoods = (GridView) findViewById(R.id.gridViewFoods);
        gridViewFoods.setAdapter(new FoodAdapter(this));

        gridViewFoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                /* Toast.makeText(HelloGridView.this, "" + position,
                        Toast.LENGTH_SHORT).show();*/

                switch(position){
                    case 0:
                        Intent intent = new Intent(getBaseContext(), AddPost.class);
                        intent.putExtra("SOURCE_ID", "foods");
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intentL = new Intent(getBaseContext(), LibraryActivity.class);
                        intentL.putExtra("SOURCE_ID", "foods");
                        startActivity(intentL);
                        break;
                }

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return false;
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public class ItemAdapter extends BaseAdapter {
        private Context mContext;

        public ItemAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View cameraView;

            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                cameraView = new ImageView(mContext);
                cameraView = inflater.inflate(R.layout.camera_item, null);

                Log.v("Position", position+"");

                ImageView imageView = (ImageView) cameraView
                        .findViewById(R.id.img_camera);
                imageView.setImageResource(mThumbIds[position]);

                if(position == 1){
                    TextView itemTitle = (TextView) cameraView
                            .findViewById(R.id.itemTitle);
                    itemTitle.setText("Biblioteca");

                }

                /*ImageView imageView = (ImageView) cameraView
                        .findViewById(R.id.img_camera);*/

                /*imageView.setLayoutParams(new GridView.LayoutParams(192, 192));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);*/
            } else {
                cameraView = (View) convertView;
            }

            //imageView.setImageResource(mThumbIds[position]);
            return cameraView;
        }

        // references to our images
        private Integer[] mThumbIds = {
                R.drawable.ic_camera, R.drawable.ic_library,
        };
    }

    public class FoodAdapter extends BaseAdapter {
        private Context mContext;

        public FoodAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View cameraView;

            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                cameraView = new ImageView(mContext);
                cameraView = inflater.inflate(R.layout.camera_item, null);

                Log.v("Position", position+"");

                ImageView imageView = (ImageView) cameraView
                        .findViewById(R.id.img_camera);
                imageView.setImageResource(mThumbIds[position]);

                if(position == 1) {
                    TextView itemTitle = (TextView) cameraView
                            .findViewById(R.id.itemTitle);
                    itemTitle.setText("Biblioteca");
                }



                /*ImageView imageView = (ImageView) cameraView
                        .findViewById(R.id.img_camera);*/

                /*imageView.setLayoutParams(new GridView.LayoutParams(192, 192));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);*/
            } else {
                cameraView = (View) convertView;
            }

            //imageView.setImageResource(mThumbIds[position]);
            return cameraView;
        }

        // references to our images
        private Integer[] mThumbIds = {
                R.drawable.ic_camera_c, R.drawable.ic_library_c,
        };
    }





}
