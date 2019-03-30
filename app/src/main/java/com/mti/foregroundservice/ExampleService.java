/*
 * Created by Tareq Islam on 3/30/19 10:22 PM
 *
 *  Last modified 3/30/19 10:22 PM
 */

package com.mti.foregroundservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import static com.mti.foregroundservice.App.CHANNEL_ID;

/***
 * Created by mtita on 30,March,2019.
 */
public class ExampleService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            String input=intent.getStringExtra("inputExtra");
            Intent notificationIntent=new Intent(this, MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,
                                0,notificationIntent,0);
        Notification notification= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("Example Service")
                        .setContentText(input)
                        .setSmallIcon(R.drawable.ic_child_care_black_24dp)
                        .setContentIntent(pendingIntent)
                        .build();
        }
        startForeground(1,notification);
    return START_REDELIVER_INTENT;

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
