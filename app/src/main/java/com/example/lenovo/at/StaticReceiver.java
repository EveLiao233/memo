package com.example.lenovo.at;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

/**
 * Created by lenovo on 2016/12/14.
 */
public class StaticReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("STATICACTION")) {
            Bundle bundle = intent.getExtras();
            Notification.Builder builder = new Notification.Builder(context);

            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.xmas5);
            builder.setContentTitle("我是来提醒你这些事快到ddl了~")
                    .setContentText(bundle.getString("things"))
                    .setLargeIcon(bmp)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.cat)
                    .setAutoCancel(true);

            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notifyintent = new Intent(context, MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(context, 0, notifyintent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(pi);
            Notification notify = builder.build();
            manager.notify(0, notify);
        }
    }
}
