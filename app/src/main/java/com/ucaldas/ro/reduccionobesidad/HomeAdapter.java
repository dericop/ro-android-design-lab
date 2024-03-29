package com.ucaldas.ro.reduccionobesidad;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;


public class HomeAdapter extends ArrayAdapter<Post>{

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
        List foodList = Arrays.asList(mContext.getResources().getStringArray(R.array.new_post_food_categories));

        if(WelcomeActivity.CURRENT_APP_VERSION.equals("R")){
            if(foodList.contains(post.getCategory())){
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
        final ImageView avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
        TextView name = (TextView) convertView.findViewById(R.id.post_name);
        TextView title = (TextView) convertView.findViewById(R.id.txt_by);
        TextView otherUser = (TextView) convertView.findViewById(R.id.txt_too);
        LinearLayout resultItem = (LinearLayout) convertView.findViewById(R.id.result_item);
        TextView txtAverage = (TextView) convertView.findViewById(R.id.txt_average_data);
        TextView lbl_too = (TextView) convertView.findViewById(R.id.lbl_too);
        TextView txtActivityResult = (TextView) convertView.findViewById(R.id.txt_activity_result);
        LinearLayout containerActivityResult = (LinearLayout) convertView.findViewById(R.id.container_txt_activity_result);
        TextView txtReply = (TextView) convertView.findViewById(R.id.replyCounter);
        TextView txtCommentsC = (TextView) convertView.findViewById(R.id.commentCounter);

        // Setup
        Glide.with(getContext()).load(post.getImage()).into(avatar);
        name.setText(post.getName());
        title.setText(post.getmUserName());
        otherUser.setText(post.getLast_share());


        if(post.getLast_share()!=null && post.getLast_share().equals("")){
            lbl_too.setVisibility(View.INVISIBLE);
        }else{
            lbl_too.setVisibility(View.VISIBLE);
        }

        if(post.getReplyCount()!=0)
            txtReply.setText(post.getReplyCount()+"");

        if(post.getCountOfComments()!=0)
            txtCommentsC.setText(post.getCountOfComments()+"");

        long result = post.getResult();
        if(result == 0){
            if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
                txtAverage.setVisibility(View.INVISIBLE);
                resultItem.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.circle_clock));
            }else{
                if(!foodList.contains(post.getCategory())){
                    txtActivityResult.setText("Pendiente");
                    containerActivityResult.setBackgroundColor(mContext.getResources().getColor(R.color.activity_wait_color));
                }
            }

        }else{
            if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){

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
                if(!foodList.contains(post.getCategory())){
                    containerActivityResult.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
                    int intResult = (int)post.getAverage();

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
                    View piContainer = convertView.findViewById(R.id.piContainer);
                    View aaContainer = convertView.findViewById(R.id.aaContainer);
                    View gsContainer = convertView.findViewById(R.id.gsContainer);
                    View chContainer = convertView.findViewById(R.id.chContainer);

                    int maxCalificationForPI = 10;
                    int relativeLayoutHeight = 50;

                    if(post.getR_pi() != 0){
                        int percentage = (int)(post.getR_pi()*100)/maxCalificationForPI;
                        int graphicalHeight = (percentage*relativeLayoutHeight) / 100;

                        ViewGroup.LayoutParams piParams = piContainer.getLayoutParams();
                        piParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, graphicalHeight, mContext.getResources().getDisplayMetrics());
                        piContainer.setLayoutParams(piParams);
                    }

                    int maxCalificationForOthers = 3;
                    if(post.getR_aa() != 0){
                        int percentage = (int)(post.getR_aa()*100)/maxCalificationForOthers;
                        int graphicalHeight = (percentage*relativeLayoutHeight)/ 100;

                        ViewGroup.LayoutParams aaParams = aaContainer.getLayoutParams();
                        aaParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, graphicalHeight, mContext.getResources().getDisplayMetrics());
                        aaContainer.setLayoutParams(aaParams);
                    }

                    if(post.getR_gs() != 0){
                        int percentage = (int)(post.getR_gs()*100)/maxCalificationForOthers;
                        int graphicalHeight = (percentage*relativeLayoutHeight)/ 100;

                        ViewGroup.LayoutParams gsParams = gsContainer.getLayoutParams();
                        gsParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, graphicalHeight, mContext.getResources().getDisplayMetrics());
                        gsContainer.setLayoutParams(gsParams);
                    }

                    if(post.getR_ch() != 0){

                        int percentage = (int)(post.getR_ch()*100)/maxCalificationForOthers;
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
            replyAction(post);
            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentAction(post);
            }
        });

        ImageButton action_comment = (ImageButton) convertView.findViewById(R.id.action_comment);
        action_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentAction(post);
            }
        });

        txtCommentsC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentAction(post);
            }
        });

        txtReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replyAction(post);
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                final AlertDialog dialog = builder.create();
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View dialogLayout = inflater.inflate(R.layout.image_pop_up, null);
                dialog.setView(dialogLayout);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.show();

                ImageView imgPrev = (ImageView) dialog.findViewById(R.id.foodImage);
                Glide.with(mContext).load(post.getImage()).into(imgPrev);

                float imageWidthInPX = (float)imgPrev.getWidth();

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(imageWidthInPX),
                        Math.round(imageWidthInPX * (float)imgPrev.getHeight() / (float)imgPrev.getWidth()));

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface d) {

                    }
                });
            }
        });
        return convertView;
    }

    private void replyAction(Post post){
        Intent replyIntent = new Intent(mContext, AddPost.class);
        replyIntent.putExtra("source", "reply");
        replyIntent.putExtra("id", post.getId());
        replyIntent.putExtra("image", post.getImage());
        replyIntent.putExtra("name", post.getName());
        replyIntent.putExtra("type", post.getCategory());

        replyIntent.putExtra("result", (int)post.getResult());
        replyIntent.putExtra("average", (int)post.getAverage());
        mContext.startActivity(replyIntent);
    }

    private void commentAction(Post post){
        Intent detailIntent = new Intent(mContext, PostDetail.class);
        detailIntent.putExtra("id", post.getId());
        detailIntent.putExtra("image", post.getImage());
        detailIntent.putExtra("name", post.getName());
        detailIntent.putExtra("type", post.getCategory());
        detailIntent.putExtra("userName", post.getmUserName());
        detailIntent.putExtra("r_pi", post.getR_pi());
        detailIntent.putExtra("r_aa", post.getR_aa());
        detailIntent.putExtra("r_gs", post.getR_gs());
        detailIntent.putExtra("r_ch", post.getR_ch());
        detailIntent.putExtra("result", post.getResult());

        mContext.startActivity(detailIntent);
    }


}
