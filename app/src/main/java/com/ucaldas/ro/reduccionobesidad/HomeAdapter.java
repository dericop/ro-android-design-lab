package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Dericop on 12/01/17.
 */

public class HomeAdapter extends ArrayAdapter<Post> {

    public HomeAdapter(Context context, List<Post> objects) {
        super(context, 0, objects);
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
        TextView company = (TextView) convertView.findViewById(R.id.txt_too);

        // Lead actual.
        Post lead = getItem(position);

        // Setup
        Glide.with(getContext()).load(lead.getmImage()).into(avatar);
        name.setText(lead.getmName());
        title.setText(lead.getmCategory());
        company.setText(lead.getmFrecuency());

        return convertView;
    }
}
