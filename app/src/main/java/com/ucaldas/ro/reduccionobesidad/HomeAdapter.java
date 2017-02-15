package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Dericop on 12/01/17.
 */

public class HomeAdapter extends ArrayAdapter<Post> {

    private Context mContext;

    public HomeAdapter(Context context, List<Post> objects) {
        super(context, 0, objects);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener inflater.
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final Post post = getItem(position);

        Log.v("DBT", post.getmCategory());
        // ¿Existe el view actual?
        //if (null == convertView) {
        List foodList = Arrays.asList(mContext.getResources().getStringArray(R.array.new_post_food_categories));

        if(WelcomeActivity.CURRENT_APP_VERSION.equals("R")){
            if(foodList.contains(post.getmCategory())){
                convertView = inflater.inflate(
                        R.layout.list_home_item_reflexive,
                        parent,
                        false);
            }else{
                convertView = inflater.inflate(
                        R.layout.list_home_item_reflexive_activity,
                        parent,
                        false);
            }

        }else{
            convertView = inflater.inflate(
                    R.layout.list_home_item,
                    parent,
                    false);
        }

        // Referencias UI.
        ImageView avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
        TextView name = (TextView) convertView.findViewById(R.id.post_name);
        TextView title = (TextView) convertView.findViewById(R.id.txt_by);
        TextView otherUser = (TextView) convertView.findViewById(R.id.txt_too);
        LinearLayout resultItem = (LinearLayout) convertView.findViewById(R.id.result_item);
        TextView txtAverage = (TextView) convertView.findViewById(R.id.txt_average_data);
        TextView lbl_too = (TextView) convertView.findViewById(R.id.lbl_too);
        TextView txtActivityResult = (TextView) convertView.findViewById(R.id.txt_activity_result);
        LinearLayout containerActivityResult = (LinearLayout) convertView.findViewById(R.id.container_txt_activity_result);

        // Setup
        Glide.with(getContext()).load(post.getmImage()).into(avatar);
        name.setText(post.getmName());
        title.setText(post.getmUserName());
        otherUser.setText(post.getmTooShared());

        if(post.getmTooShared().equals("")){
            lbl_too.setVisibility(View.INVISIBLE);
        }else{
            lbl_too.setVisibility(View.VISIBLE);
        }

        long result = post.getmResult();
        Log.v("DB", result+"");
        if(result == 0){
            if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
                txtAverage.setVisibility(View.INVISIBLE);
                resultItem.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.circle_clock));
            }else{
                if(!foodList.contains(post.getmCategory())){
                    txtActivityResult.setText("Pendiente");
                    containerActivityResult.setBackgroundColor(mContext.getResources().getColor(R.color.activity_wait_color));
                }
            }

        }else{
            if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
                //txtAverage.setVisibility(View.VISIBLE);
                //txtAverage.setText(post.getmAverage()+"");

                switch ((int)result){
                    case 1:
                        resultItem.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.circle_bad));
                        break;
                    case 2:
                        resultItem.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.circle_medium));
                        break;
                    case 3:
                        resultItem.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.circle_good));
                        break;
                }
            }else{
                if(!foodList.contains(post.getmCategory())){
                    containerActivityResult.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
                    int intResult = (int)post.getmAverage();

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

                }else{
                    View piContainer = (View) convertView.findViewById(R.id.piContainer);
                    View aaContainer = (View) convertView.findViewById(R.id.aaContainer);
                    View gsContainer = (View) convertView.findViewById(R.id.gsContainer);
                    View chContainer = (View) convertView.findViewById(R.id.chContainer);


                    int maxCalificationForPI = 10;
                    int relativeLayoutHeight = 50;

                    if(post.getmPi() != 0){
                        int percentage = (int)(post.getmPi()*100)/maxCalificationForPI;
                        int graphicalHeight = (percentage*relativeLayoutHeight) / 100;

                        ViewGroup.LayoutParams piParams = piContainer.getLayoutParams();
                        piParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, graphicalHeight, mContext.getResources().getDisplayMetrics());
                        piContainer.setLayoutParams(piParams);
                    }

                    int maxCalificationForOthers = 3;
                    if(post.getmAa() != 0){
                        int percentage = (int)(post.getmAa()*100)/maxCalificationForOthers;
                        int graphicalHeight = (percentage*relativeLayoutHeight)/ 100;

                        ViewGroup.LayoutParams aaParams = aaContainer.getLayoutParams();
                        aaParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, graphicalHeight, mContext.getResources().getDisplayMetrics());
                        aaContainer.setLayoutParams(aaParams);
                    }

                    if(post.getmGs() != 0){
                        int percentage = (int)(post.getmGs()*100)/maxCalificationForOthers;
                        int graphicalHeight = (percentage*relativeLayoutHeight)/ 100;

                        ViewGroup.LayoutParams gsParams = gsContainer.getLayoutParams();
                        gsParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, graphicalHeight, mContext.getResources().getDisplayMetrics());
                        gsContainer.setLayoutParams(gsParams);
                    }

                    if(post.getmCh() != 0){

                        int percentage = (int)(post.getmCh()*100)/maxCalificationForOthers;
                        int graphicalHeight = (percentage*relativeLayoutHeight)/ 100;
                        ViewGroup.LayoutParams chParams = chContainer.getLayoutParams();
                        chParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, graphicalHeight, mContext.getResources().getDisplayMetrics());
                        chContainer.setLayoutParams(chParams);
                    }

                }

            }
        }


        ImageButton action_reply = (ImageButton) convertView.findViewById(R.id.action_reply);
        action_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent replyIntent = new Intent(mContext, AddPost.class);
                replyIntent.putExtra("source", "reply");
                replyIntent.putExtra("id", post.getmId());
                replyIntent.putExtra("image", post.getmImage());
                replyIntent.putExtra("name", post.getmName());
                replyIntent.putExtra("type", post.getmCategory());

                Log.v("DBP", post.getmResult()+"");
                replyIntent.putExtra("result", (int)post.getmResult());
                replyIntent.putExtra("average", (int)post.getmAverage());
                mContext.startActivity(replyIntent);
            }
        });

        return convertView;
    }


}
