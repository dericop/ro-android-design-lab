package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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

        // ¿Existe el view actual?
        if (null == convertView) {
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

        // Lead actual.
        final Post post = getItem(position);

        // Setup
        Glide.with(getContext()).load(post.getmImage()).into(avatar);
        name.setText(post.getmName());
        title.setText(post.getmUser());
        otherUser.setText("");

        ImageButton action_reply = (ImageButton) convertView.findViewById(R.id.action_reply);
        action_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent replyIntent = new Intent(mContext, AddPost.class);
                replyIntent.putExtra("source", "reply");
                replyIntent.putExtra("id", post.getmId());
                replyIntent.putExtra("image", post.getmImage());
                replyIntent.putExtra("name", post.getmName());
                mContext.startActivity(replyIntent);

            }
        });

        return convertView;
    }


}
