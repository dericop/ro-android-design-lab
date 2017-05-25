package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by disenoestrategico on 24/05/17.
 */

public class QuestionsAdapter extends ArrayAdapter<Question>{

    private Context mContext;
    private List mQuestions;

    static class ViewHolder{
        private String title;
        private String response1;
        private String response2;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = inflater.inflate(
                    R.layout.question_item,
                    parent,
                    false);
        }

        Question cQuestion = getItem(position);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        Button response1 = (Button) convertView.findViewById(R.id.response1);
        Button response2 = (Button) convertView.findViewById(R.id.response2);
        ImageView userResponse = (ImageView) convertView.findViewById(R.id.userResponse);

        title.setText(cQuestion.getTitle());
        response1.setText(cQuestion.getResponse1());
        response2.setText(cQuestion.getResponse2());

        if(cQuestion.isUserResponse()){
            userResponse.setImageDrawable(mContext.getResources().getDrawable(R.drawable.green_circle));
        }else{
            userResponse.setImageDrawable(mContext.getResources().getDrawable(R.drawable.red_circle));
        }

        if(response1.getText().equals(cQuestion.getCorrect())){
            response1.setBackgroundResource(R.drawable.success_response);
            response1.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        }else{
            response2.setBackgroundResource(R.drawable.success_response);
            response2.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        }

        return convertView;
    }

    public QuestionsAdapter(Context context, List questions){
        super(context,0, questions);
        mContext = context;
        mQuestions = questions;
    }



}