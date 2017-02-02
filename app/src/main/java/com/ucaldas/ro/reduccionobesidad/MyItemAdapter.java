package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
            LinearLayout resultItem = (LinearLayout) convertView.findViewById(R.id.result_item);
            TextView txtAverage = (TextView) convertView.findViewById(R.id.txt_average_data);

            // Lead actual.
            Post post = getItem(i);

            // Setup
            Glide.with(mContext).load(post.getmImage()).into(thumbnail);
            name.setText(post.getmName());
            long result = post.getmResult();

            if(result == 0){
                txtAverage.setVisibility(View.INVISIBLE);
                resultItem.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.circle_clock));

            }else{
                txtAverage.setVisibility(View.VISIBLE);
                txtAverage.setText(post.getmAverage()+"");

                switch ((int)result){
                    case 1:
                        resultItem.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.circle_bad));
                        break;
                    case 2:
                        resultItem.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.circle_medium));
                        break;
                    case 3:
                        resultItem.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.circle_good));
                        break;
                }
            }

        } else {
            convertView = (View) view;

        }

        return convertView;
    }
}
