/*
 * Created by Tareq Islam on 4/23/19 2:12 AM
 *
 *  Last modified 4/23/19 2:12 AM
 */

package com.mti.foregroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/***
 * Created by mtita on 23,April,2019.
 */
public class ExampleBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("tareq_test" , "Service Stooooop");
        context.startService(new Intent(context, ExampleService.class));
    }
}
