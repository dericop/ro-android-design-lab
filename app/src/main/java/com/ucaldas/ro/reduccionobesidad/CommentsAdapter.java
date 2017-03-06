package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by disenoestrategico on 1/03/17.
 */



public class CommentsAdapter extends ArrayAdapter<Comment> {


    private Context mContext;
    private List mComments;

    public CommentsAdapter(Context context, List<Comment> comments){
        super(context,0, comments);
        this.mContext = context;
        this.mComments = comments;
    }

    static class ViewHolder{
        private String title;
        private String detail;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = inflater.inflate(
                    R.layout.comment_item,
                    parent,
                    false);
        }

        Comment com = getItem(position);

        TextView detail = (TextView) convertView.findViewById(R.id.detail);
        TextView userName = (TextView) convertView.findViewById(R.id.titleName);

        if(mHome.user != null)
            userName.setText(mHome.user.getDisplayName());

        detail.setText(com.getDetail());



        return convertView;
    }
}
