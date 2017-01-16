package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by disenoestrategico on 16/01/17.
 */

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

        LayoutInflater inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View cameraView;

        if (convertView == null) {
            cameraView = inflater.inflate(R.layout.camera_item, null);

            ImageView imageView = (ImageView) cameraView
                    .findViewById(R.id.img_camera);
            imageView.setImageResource(mThumbIds[position]);

            if(position == 1) {
                TextView itemTitle = (TextView) cameraView
                        .findViewById(R.id.itemTitle);
                itemTitle.setText("Biblioteca");
            }

        } else {
            cameraView = (View) convertView;
        }

        return cameraView;
    }

    private Integer[] mThumbIds = {
            R.drawable.ic_camera_c, R.drawable.ic_library_c,
    };
}
