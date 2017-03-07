package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Calendar;
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
        final ImageView avatar = (ImageView) convertView.findViewById(R.id.avatar);
        TextView time = (TextView) convertView.findViewById(R.id.time);

        if(mHome.user != null){
            userName.setText(mHome.user.getDisplayName());
            Glide.with(mContext)
                .load(mHome.user.getPhotoUrl())
                .asBitmap()
                .listener(new RequestListener<Uri, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Uri model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {

                        return false;
                    }
                })
                .into(avatar);
        }

        if(com!=null){
            detail.setText(com.getDetail());

            Calendar currentCalendar = Calendar.getInstance();
            int dayC = currentCalendar.get(Calendar.DAY_OF_MONTH);
            int monthC = currentCalendar.get(Calendar.MONTH);
            int yearC = currentCalendar.get(Calendar.YEAR);

            Calendar commentCalendar = Calendar.getInstance();
            commentCalendar.setTimeInMillis(com.getDate());
            int dayCom = commentCalendar.get(Calendar.DAY_OF_MONTH);
            int monthCom = commentCalendar.get(Calendar.MONTH);
            int yearCom = commentCalendar.get(Calendar.YEAR);

            if(dayC == dayCom && monthC == monthCom && yearC==yearCom){
                int hourC = currentCalendar.get(Calendar.HOUR_OF_DAY);
                int hourCom = commentCalendar.get(Calendar.HOUR_OF_DAY);
                int timeC = hourC - hourCom;
                if(timeC == 0){//Fué hace menos de una hora
                    int minutesC = currentCalendar.get(Calendar.MINUTE);
                    int minutesCom = commentCalendar.get(Calendar.MINUTE);
                    int minuteC = minutesC - minutesCom;

                    time.setText(minuteC +" minutos");
                }else{
                    time.setText(timeC+" horas");
                }
            }else if(yearC == yearCom){//Igual año
                    time.setText(getMonthString(monthCom));
            }else{
                time.setText(getMonthString(monthCom)+" "+yearCom);
            }
        }

        return convertView;
    }

    private String getMonthString(int n){
        String month = "";

        switch (n){
            case 0:
                month = "Enero";
                break;
            case 1:
                month = "Febrero";
                break;
            case 2:
                month = "Marzo";
                break;
            case 3:
                month = "Abril";
                break;
            case 4:
                month = "Mayo";
                break;
            case 5:
                month = "Junio";
                break;
            case 6:
                month = "Julio";
                break;
            case 7:
                month = "Agosto";
                break;
            case 8:
                month = "Sept";
                break;
            case 9:
                month = "Octubre";
                break;
            case 10:
                month = "Nov";
                break;
            case 11:
                month = "Dic";
                break;
        }

        return month;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
