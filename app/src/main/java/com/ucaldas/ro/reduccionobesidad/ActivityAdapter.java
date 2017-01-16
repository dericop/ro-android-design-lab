package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by disenoestrategico on 16/01/17.
 */

public class ActivityAdapter extends BaseAdapter {

    private Context mContext;

    public ActivityAdapter(Context c) {
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

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cameraView;

        if (convertView == null) {
            cameraView = inflater.inflate(R.layout.camera_item, null);

            ImageView imageView = (ImageView) cameraView
                    .findViewById(R.id.img_camera);
            imageView.setImageResource(mThumbIds[position]);

            if(position == 1){
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
            R.drawable.ic_camera, R.drawable.ic_library,
    };


}
