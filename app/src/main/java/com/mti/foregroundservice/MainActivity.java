package com.mti.foregroundservice;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    Context mContext;
    EditText mEditText;
    private Disposable mDisposable;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        final RxPermissions rxPermissions = new RxPermissions(this);

        mDisposable = rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET)
                .subscribe(granted -> {
                    if (granted) {
                        Toast.makeText(mContext, "permission granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "One of them denied", Toast.LENGTH_SHORT).show();
                    }
                });

        mEditText = findViewById(R.id.editText);
        Button start = findViewById(R.id.startbutton);
     exampleService= new ExampleService(mContext);
        intent = new Intent(MainActivity.this, exampleService.getClass());


        start.setOnClickListener(v -> {
            String input = mEditText.getText().toString();

          intent.putExtra("inputExtra", input);
            if (!isMyServiceRunning(exampleService.getClass())) {
                mContext.startService(intent);
            }
        });


        Button stop = findViewById(R.id.stopButton);
        stop.setOnClickListener(v -> {



            stopService(intent);
           ExampleService.myTimer.cancel();
        });



     listView=findViewById(R.id.listview);

      listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,lists));

       // ((TextView) findViewById(R.id.textView2)).setText("n: " + exampleService.no_time_called);
    }

    ExampleService exampleService;
    Intent intent ;
    public  ListView listView;
   public static List<String> lists=new ArrayList<>();


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    protected void onDestroy() {
       if(intent!=null)
        stopService(intent); /*The latter may seem rather peculiar:
         why do we want to stop exactly the service that we want to keep alive?
          Because if we do not stop it, the service will die with our app.
          Instead, by stopping the service, we will force the service to call its own onDestroy which will force it to recreate itself after the app is dead.*/
        super.onDestroy();
    }
}
