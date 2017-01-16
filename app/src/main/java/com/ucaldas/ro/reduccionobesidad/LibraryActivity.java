package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class LibraryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        changeStatusBarColor();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Biblioteca de actividades");

        GridView gridview = (GridView) findViewById(R.id.gridview);

        String source = getIntent().getStringExtra("SOURCE_ID");

        if(source.equals("activities")){
            gridview.setAdapter(new ImageAdapter(this));
        }else{
            gridview.setAdapter(new FoodAdapter(this));

        }


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(LibraryActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LibraryActivity.this, AddPost.class);
                intent.putExtra("SOURCE_ID", "activities");
                startActivity(intent);
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

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
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
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }


        // references to our images
        private Integer[] mThumbIds = {
                R.drawable.sample_1,R.drawable.sample_2,
                R.drawable.sample_3,R.drawable.sample_4,
                R.drawable.sample_1,R.drawable.sample_2,
                R.drawable.sample_3,R.drawable.sample_4,
                R.drawable.sample_1,R.drawable.sample_2,
                R.drawable.sample_3,R.drawable.sample_4,
                R.drawable.sample_1,R.drawable.sample_2,
                R.drawable.sample_3,R.drawable.sample_4,

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
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }


        // references to our images
        private Integer[] mThumbIds = {
                R.drawable.sample_f_1,R.drawable.sample_f_2,
                R.drawable.sample_f_3,R.drawable.sample_f_4,
                R.drawable.sample_f_5,R.drawable.sample_f_1,
                R.drawable.sample_f_1,R.drawable.sample_f_2,
                R.drawable.sample_f_3,R.drawable.sample_f_4,
                R.drawable.sample_f_5,R.drawable.sample_f_1,
                R.drawable.sample_f_1,R.drawable.sample_f_2,
                R.drawable.sample_f_3,R.drawable.sample_f_4,
                R.drawable.sample_f_5,R.drawable.sample_f_1,

        };
    }




}
