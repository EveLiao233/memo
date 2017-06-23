package com.example.lenovo.at;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by L on 2017/6/6.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int[] resID = new int[] {R.mipmap.pokemon1, R.mipmap.pokemon2, R.mipmap.pokemon3, R.mipmap.pokemon4,
                R.mipmap.pokemon5, R.mipmap.pokemon6, R.mipmap.pokemon7, R.mipmap.pokemon8,
                R.mipmap.xmas5, R.mipmap.xmas6, R.mipmap.xmas7, R.mipmap.xmas8,
                R.mipmap.xmas1, R.mipmap.xmas2, R.mipmap.xmas3, R.mipmap.xmas4,
                R.mipmap.icon1, R.mipmap.icon2, R.mipmap.icon3, R.mipmap.icon4,
                R.mipmap.animal1, R.mipmap.animal2, R.mipmap.animal3, R.mipmap.animal4,
                R.mipmap.halloween1, R.mipmap.halloween2, R.mipmap.halloween3, R.mipmap.halloween4};

        int id = intent.getIntExtra("id", 0);
        String itemName = intent.getStringExtra("thing_time");
        int icon_id = intent.getIntExtra("icon_id", 1);

        String [] arr = new String[] {" 已经过了30%的时间", " 已经过了70%的时间", " 时间已经到了！！！"};
        NotificationManager manager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(context);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resID[icon_id - 1]);
        //Bitmap bitmap = context.getResources().getDrawable(resID[icon_id - 1]);

        builder.setContentTitle("提醒")
                .setContentText(itemName + arr[2])
                .setTicker("提醒")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.dt)
                 .setLargeIcon(bitmap)
                .setDefaults(Notification.DEFAULT_ALL);

        Intent mIntent = new Intent(context, MainActivity.class);
        mIntent.putExtra("thing_time", itemName);
        PendingIntent mPendingIntent = PendingIntent.getActivity(context,
                id, mIntent, 0);
        builder.setContentIntent(mPendingIntent);

        Notification notify = builder.build();

        manager.notify(id, notify);
    }
}
