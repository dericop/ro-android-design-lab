package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class AddMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.addMenuTittle);
        setSupportActionBar(toolbar);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ItemAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
            /* Toast.makeText(HelloGridView.this, "" + position,
                    Toast.LENGTH_SHORT).show();*/
            }
        });


        //GridView de alimentos
        GridView gridViewFoods = (GridView) findViewById(R.id.gridViewFoods);
        gridViewFoods.setAdapter(new ItemAdapter(this));

        gridViewFoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                /* Toast.makeText(HelloGridView.this, "" + position,
                        Toast.LENGTH_SHORT).show();*/
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return false;
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

                ImageView imageView = (ImageView) cameraView
                        .findViewById(R.id.img_camera);

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
                R.drawable.ic_food, R.drawable.ic_food,
        };
    }





}
