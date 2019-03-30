/*
 * Created by Tareq Islam on 3/30/19 10:05 PM
 *
 *  Last modified 3/30/19 10:05 PM
 */

package com.mti.foregroundservice;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

/***
 * Created by mtita on 30,March,2019.
 */
public class App extends Application {
public static final String CHANNEL_ID="testServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel serviceChannel=new NotificationChannel(
                    CHANNEL_ID, "Test Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager =getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
