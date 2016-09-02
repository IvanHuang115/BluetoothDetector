package com.example.acer.bluetoothdetector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DevicesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

    }

    public void addBeaconActivityPressed(View v) {
        Intent i = new Intent(this, AddBeaconActivity.class);
        startActivity(i);
    }

}
