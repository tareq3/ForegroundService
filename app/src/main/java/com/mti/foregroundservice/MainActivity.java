package com.mti.foregroundservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText mEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText=findViewById(R.id.editText);
        Button start=findViewById(R.id.startbutton);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String input=mEditText.getText().toString();
                Intent intent=new Intent(MainActivity.this, ExampleService.class);

                intent.putExtra("inputExtra",input);
                startService(intent);
            }
        });
        Button stop=findViewById(R.id.stopButton);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, ExampleService.class);


                stopService(intent);
            }
        });
    }
}
