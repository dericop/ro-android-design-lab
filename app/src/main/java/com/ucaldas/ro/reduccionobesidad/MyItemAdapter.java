package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by disenoestrategico on 20/01/17.
 */

public class MyItemAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Post> mPostList;

    public MyItemAdapter(Context c, ArrayList<Post> postList) {
        mContext = c;
        mPostList = postList;
    }

    @Override
    public int getCount() {
        return mPostList.size();
    }

    @Override
    public Post getItem(int i) {
        return this.mPostList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View convertView;

        if (view == null) {
            convertView = inflater.inflate(R.layout.list_my_items_item, null);

            // Referencias UI.
            ImageView thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
            TextView name = (TextView) convertView.findViewById(R.id.title);

            // Lead actual.
            Post post = getItem(i);

            // Setup
            Glide.with(mContext).load(post.getmImage()).into(thumbnail);
            name.setText(post.getmName());

        } else {
            convertView = (View) view;

        }

        return convertView;
    }
}
