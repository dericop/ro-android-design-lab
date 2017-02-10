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
import java.util.Arrays;
import java.util.List;

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

            // Lead actual.
            Post post = getItem(i);
            List foodList = Arrays.asList(mContext.getResources().getStringArray(R.array.new_post_food_categories));

            if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){

                convertView = inflater.inflate(R.layout.list_my_items_item, null);
            }else{



                if(foodList.contains(post.getmCategory())){
                    convertView = inflater.inflate(R.layout.list_my_items_item_reflexive, null);
                }else{
                    convertView = inflater.inflate(R.layout.list_my_items_item_reflexive_activity, null);
                }
            }

            // Referencias UI.
            ImageView thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
            TextView name = (TextView) convertView.findViewById(R.id.title);
            LinearLayout resultItem = (LinearLayout) convertView.findViewById(R.id.result_item);
            TextView txtAverage = (TextView) convertView.findViewById(R.id.txt_average_data);
            TextView txtActivityResult = (TextView) convertView.findViewById(R.id.txt_activity_result);
            LinearLayout containerActivityResult = (LinearLayout) convertView.findViewById(R.id.container_txt_activity_result);


            // Setup
            Glide.with(mContext).load(post.getmImage()).into(thumbnail);
            name.setText(post.getmName());
            long result = post.getmResult();

            if(result == 0){
                if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
                    txtAverage.setVisibility(View.INVISIBLE);
                    resultItem.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.circle_clock));
                }else{
                    if(!foodList.contains(post.getmCategory())){
                        txtActivityResult.setText("Pendiente");
                        containerActivityResult.setBackgroundColor(mContext.getResources().getColor(R.color.activity_wait_color));
                    }
                }

            }else{
                if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
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
                }else{

                    if(!foodList.contains(post.getmCategory())){
                        containerActivityResult.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
                        int intResult = (int)result;

                        if(intResult == 1){
                            txtActivityResult.setText("Sedentario");
                        }else if(intResult>1 && intResult<3){
                            txtActivityResult.setText("Bajo");
                        }else if(intResult>=3 && intResult<=4){
                            txtActivityResult.setText("Medio Bajo");
                        }else if(intResult>4 && intResult<=6){
                            txtActivityResult.setText("Medio");
                        }else if(intResult>6 && intResult<=8){
                            txtActivityResult.setText("Medio Alto");
                        }else if(intResult>8 && intResult<=10){
                            txtActivityResult.setText("Alto");
                        }

                    }

                }
            }

        } else {
            convertView = (View) view;

        }

        return convertView;
    }
}
