/*
 * Created by Tareq Islam on 3/30/19 10:22 PM
 *
 *  Last modified 3/30/19 10:22 PM
 */

package com.mti.foregroundservice;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.reactivestreams.Subscription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;
import pl.charmas.android.reactivelocation2.ReactiveLocationProviderConfiguration;

import static com.mti.foregroundservice.App.CHANNEL_ID;

/***
 * Created by mtita on 30,March,2019.
 */
public class ExampleService extends Service {





    public int no_time_called=0;
   public  List<String> lists=new ArrayList<>();
    public int interval = 5 ;
    private final static int REQUEST_CHECK_SETTINGS = 0;
    public static   Timer myTimer;
    Disposable mDisposable;
    private String TAG = "tareq_test";
    GoogleApiClient mGoogleApiClient;
    private static String lastTime;

    public ExampleService() {
    }

    public ExampleService(Context applicationContext) {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
/*
// Hopefully your alarm will have a lower frequency than this!
       AlarmManager alarmMgr;
        alarmMgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HALF_HOUR,
                AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);

*/


        return START_STICKY;

    }

    private void displayLocationSettingsRequest(Context context) {
      /*  GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
*/
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener((task) -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                // All location settings are satisfied. The client can initialize location
                // requests here.
                // setupLocationListener();

            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                            // Cast to a resolvable exception.
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                        } catch (ClassCastException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });

/*
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                Log.d("tareq_test", "" + status.getStatusMessage());
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });*/
    }

 /*   private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("Tareq", strReturnedAddress.toString());
            } else {
                Log.w("Tareq", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("Tareq", "Canont get Address!");
        }
        return strAdd;
    }
 */

    ListView mListView;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();


        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = null;


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Example Service")

                    .setSmallIcon(R.drawable.ic_child_care_black_24dp)
                    .setContentIntent(pendingIntent)
                    .build();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_child_care_black_24dp)
                    .setContentTitle("TEST")
                    .setContentText("HELLO")
                    .setTicker("TICKER")
                    .setContentIntent(pendingIntent);
            notification = builder.build();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                /*  .addConnectionCallbacks(this)
                  .addOnConnectionFailedListener(this)*/
                .build();

        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60 *interval* 1000);
        mLocationRequest.setFastestInterval(30 * interval* 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d("tareq_test", "location null");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //TODO: UI updates.
                        Log.d("tareq_test", "test" + location.toString());
                    }
                }
            }


        };
        LocationServices.getFusedLocationProviderClient(getApplicationContext()).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        lists  = new ArrayList<>();
        //retrive data
        SharedPreferences prefs = getSharedPreferences("ServiceRunning", MODE_PRIVATE);

        HashSet<String> sets = (HashSet<String>) prefs.getStringSet("mkey", new HashSet<>());
        lists = new ArrayList<>(sets);

        Log.d("tareq_test" , "Size: "+lists.size());


//Declare the timer
        myTimer = new Timer();
//Set the schedule function and rate
        myTimer.schedule(new TimerTask() {

                             @SuppressLint("MissingPermission")
                             @Override
                             public void run() {
                                 //Called each time when 1000 milliseconds (1 second) (the period parameter)
                                 no_time_called++;
                                 Log.d("tareq_test", "Called ");

                                 displayLocationSettingsRequest(getApplicationContext());


                                 LocationRequest request = LocationRequest.create() //standard GMS LocationRequest
                                         .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


                                 ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(getApplicationContext(), ReactiveLocationProviderConfiguration
                                         .builder()
                                         .setRetryOnConnectionSuspended(true)
                                         .build());
                                 mDisposable = locationProvider
                                         // .getUpdatedLocation(request)
                                         .getLastKnownLocation()

                                         //   .filter(location -> location.getAccuracy()<25)
                                         .subscribe(location -> {
                                                     Log.d("tareq_test", "addr: " + GetAddressFromLatLang.getAddressFromLatLan(getApplicationContext(), location.getLatitude(), location.getLongitude()) + " acc: " + location.getAccuracy() + " T: " + CurrentTimeUtilityClass.getCurrentTimeStamp());
                                                     // To get City-Name from coordinates
                                                     //    if(location.getAccuracy()<25) {
                                                     lastTime=prefs.getString("lasttime","");
                                                     Log.d("tareq_test" , "last Time:"+lastTime);
                                                     if (!lastTime.equals("")) {
                                                         //      Log.d("tareq_test", "diff " + CurrentTimeUtilityClass.getDiffbetweenTimeStamps(lastTime));
                                                         Log.d("tareq_test", "diff " + CurrentTimeUtilityClass.getDiffBetween(lastTime));

                                                         //  if ( CurrentTimeUtilityClass.getDiffbetweenTimeStamps(lastTime) >= 5 || (CurrentTimeUtilityClass.getDiffbetweenTimeStamps(lastTime) >= -55 && CurrentTimeUtilityClass.getDiffbetweenTimeStamps(lastTime) <0) ) {
                                                         if (  CurrentTimeUtilityClass.getDiffBetween(lastTime) >= 5  ) {

                                                             Log.d("tareq_test", "Filtered_addr_if: " + GetAddressFromLatLang.getAddressFromLatLan(getApplicationContext(), location.getLatitude(), location.getLongitude()) + " acc: " + location.getAccuracy() + " T: " + CurrentTimeUtilityClass.getCurrentTimeStamp());

                                                             lists.add(GetAddressFromLatLang.getAddressFromLatLan(getApplicationContext(), location.getLatitude(), location.getLongitude()) + " acc: " + location.getAccuracy() + " T: " + CurrentTimeUtilityClass.getCurrentTimeStamp());
                                                             //add data
                                                             Set<String> set = new HashSet<String>();
                                                             set.addAll(lists);

                                                             prefs.edit().putStringSet("mkey", set).apply();


                                                             lastTime= Calendar.getInstance().getTime().toString();

                                                             prefs.edit().putString("lasttime",lastTime).apply();
                                                             if (mDisposable != null && !mDisposable.isDisposed())
                                                                 mDisposable.dispose();
                                                         }else{
                                                             Log.d("tareq_test" , "not accepted");
                                                         }
                                                     } else {
                                                         Log.d("tareq_test", "Filtered_addr: " + GetAddressFromLatLang.getAddressFromLatLan(getApplicationContext(), location.getLatitude(), location.getLongitude()) + " acc: " + location.getAccuracy() + " T: " + CurrentTimeUtilityClass.getCurrentTimeStamp());

                                                         lists.add(GetAddressFromLatLang.getAddressFromLatLan(getApplicationContext(), location.getLatitude(), location.getLongitude()) + " acc: " + location.getAccuracy() + " T: " + CurrentTimeUtilityClass.getCurrentTimeStamp());
                                                         //add data
                                                         Set<String> set = new HashSet<String>();
                                                         set.addAll(lists);

                                                         prefs.edit().putStringSet("mkey", set).apply();

                                                         lastTime = Calendar.getInstance().getTime().toString();

                                                         prefs.edit().putString("lasttime",lastTime).apply();


                                                         if (mDisposable != null && !mDisposable.isDisposed())
                                                             mDisposable.dispose();
                                                     }

                                                              /*  }else{
                                                                    Disposable disposable=    locationProvider
                                                                            .getUpdatedLocation(request)
                                                                            .subscribe(location1 -> {
                                                                        lists.add(GetAddressFromLatLang.getAddressFromLatLan(getApplicationContext(), location1.getLatitude(), location1.getLongitude()) + " acc: " + location1.getAccuracy()  + " acc_p: " + location.getAccuracy());
                                                                    });
                                                                    disposable.dispose();
                                                                }*/
                                                     MainActivity.lists=lists;
                                                     //     MainActivity.listView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, lists));


                                                 }
                                         );

                                 mGoogleApiClient.disconnect();
                             }

                         },
//Set how long before to start calling the TimerTask (in milliseconds)
                0,
//Set the amount of time between each execution (in milliseconds)
                60  * 1000);


        startForeground(1, notification);

    }

    @Override
    public void onDestroy() {
      /*  if (mDisposable != null && !mDisposable.isDisposed())
            mDisposable.dispose();*/

        super.onDestroy();
        Intent broadcastIntent = new Intent(this, ExampleBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
        if (myTimer != null) {

            myTimer.cancel();
            myTimer = null;
        }

    }

  /*  @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }*/
}
