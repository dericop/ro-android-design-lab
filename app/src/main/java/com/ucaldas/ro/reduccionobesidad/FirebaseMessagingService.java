package com.ucaldas.ro.reduccionobesidad;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import static android.content.ContentValues.TAG;

/**
 * Created by disenoestrategico on 9/03/17.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    public FirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            String type = remoteMessage.getData().get("type");
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            String data = remoteMessage.getData().get("data");

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_action_comment_white)
                            .setAutoCancel(true)
                            .setSound(alarmSound)
                            .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                            .setContentTitle(title)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                            .setContentText(body);

            Intent detailIntent = null;

            switch(type){
                case "detail":

                    detailIntent = new Intent(getApplicationContext(), PostDetail.class);
                    detailIntent.putExtra("id", data);
                    detailIntent.putExtra("notificationType", "qualification");

                    break;

                case "Tip":

                    detailIntent = new Intent(getApplicationContext(), TipDetailActivity.class);
                    detailIntent.putExtra("id", data);
                    detailIntent.putExtra("notificationType", "tip");

                    break;
            }

            if(detailIntent != null){

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(mHome.class);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(detailIntent);

                Intent backIntent = new Intent(getApplicationContext(), mHome.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                final PendingIntent resultPendingIntent = PendingIntent.getActivities(
                        getApplicationContext(), 0,
                        new Intent[]{backIntent, detailIntent}, PendingIntent.FLAG_ONE_SHOT);

                mBuilder.setContentIntent(resultPendingIntent);

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // mId allows you to update the notification later on.
                mNotificationManager.notify(1, mBuilder.build());
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }


    }

}
