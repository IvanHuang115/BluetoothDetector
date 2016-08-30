package com.example.acer.bluetoothdetector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private EditText ET_NAME;
    private EditText ET_IP;
    private EditText ET_PORT;
    private EditText ET_DBUSER;
    private EditText ET_DBPASS;
    private EditText ET_UUID;
    private EditText ET_MAJOR;
    private EditText ET_UID;

    private static final String TAG = "Beacon Test";

    private static String P_ID;
    private static String P_USER;
    private static String DB_IP;
    private static String DB_PORT;
    private static String DB_USER;
    private static String DB_PASS;
    private static String UUID;
    private static String MAJOR;
    private static String U_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Activity Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // gets the unique identifier for each android device
        TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        P_ID = tManager.getDeviceId();
        Log.d(TAG, "device id: " + P_ID);

        ET_NAME = (EditText) findViewById(R.id.ET_NAME);
        ET_IP = (EditText) findViewById(R.id.ET_IP);
        ET_PORT = (EditText) findViewById(R.id.ET_PORT);
        ET_DBUSER = (EditText) findViewById(R.id.ET_DBUSER);
        ET_DBPASS = (EditText) findViewById(R.id.ET_DBPASS);
        ET_UUID = (EditText) findViewById(R.id.ET_UUID);
        ET_MAJOR = (EditText) findViewById(R.id.ET_MAJOR);
        ET_UID = (EditText) findViewById(R.id.ET_UID);

        // gets the user data from previous user input
        SharedPreferences sp = getSharedPreferences("BluetoothDetectorData", Context.MODE_PRIVATE);
        P_USER = sp.getString("SP_USER", "");
        DB_IP = sp.getString("SP_IP", "");
        DB_PORT = sp.getString("SP_PORT", "");
        DB_USER = sp.getString("SP_DBUSER", "");
        DB_PASS = sp.getString("SP_DBPASS", "");
        UUID = sp.getString("SP_UUID", "");
        MAJOR = sp.getString("SP_MAJOR", "");
        U_ID = sp.getString("SP_UID", "");
        Log.d(TAG, "user: " + P_USER + " ip: " + DB_IP + " port: " + DB_PORT);

        // gets printed if it's the first time using the app
        if (P_USER.equals("") || DB_IP.equals("") || DB_PORT.equals("")) {
            Toast.makeText(this, "Please enter your name and the IP address of your Raspberry Pi",
                    Toast.LENGTH_LONG).show();
        }

        ET_NAME.setText(P_USER);
        ET_IP.setText(DB_IP);
        ET_PORT.setText(DB_PORT);
        ET_DBUSER.setText(DB_USER);
        ET_DBPASS.setText(DB_PASS);
        ET_UUID.setText(UUID);
        ET_MAJOR.setText(MAJOR);
        ET_UID.setText(U_ID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // gets called when Enter button is pressed
    public void enterUserInfo(View v) {
        P_USER = ET_NAME.getText().toString();
        DB_IP = ET_IP.getText().toString();
        DB_PORT = ET_PORT.getText().toString();
        DB_USER = ET_DBUSER.getText().toString();
        DB_PASS = ET_DBPASS.getText().toString();
        UUID = ET_UUID.getText().toString();
        MAJOR = ET_MAJOR.getText().toString();
        U_ID = ET_UID.getText().toString();

        submitSavedPreferences();

        Toast.makeText(this, "Input received, please press the start service button",
                Toast.LENGTH_LONG).show();
    }

    // gets called when Start Service button is pressed
    public void startServicePressed(View v) {
        // create a new thread to run the service
        Thread t = new Thread() {

            public void run() {
                Intent i = new Intent(MainActivity.this, BluetoothDetectorService.class);

                /*
                i.putExtra("USER", P_USER);
                i.putExtra("IP", DB_IP);
                i.putExtra("PORT", DB_PORT);
                */

                startService(i);
            }

        };
        t.start();
    }

    // gets called when Stop Service button is pressed
    public void stopServicePressed(View v) {
        Intent i = new Intent(this, BluetoothDetectorService.class);
        stopService(i);
    }

    public void debugPressed(View v) {
        P_USER = "debug";
        DB_IP = "192.168.0.24";
        DB_PORT = "3306";
        DB_USER = "beaconuser";
        DB_PASS = "seelab";
        UUID = "A580C8B8-89FE-4548-8A24-472B7DE1224C";
        MAJOR = "0";
        U_ID = "1234";

        submitSavedPreferences();
    }

    public void submitSavedPreferences() {
        SharedPreferences sp = getSharedPreferences("BluetoothDetectorData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        // TODO Check for blank entry, eliminate white spaces, try connecting to the db
        editor.putString("SP_USER", P_USER);
        editor.putString("SP_IP", DB_IP);
        editor.putString("SP_PORT", DB_PORT);
        editor.putString("SP_DBUSER", DB_USER);
        editor.putString("SP_DBPASS", DB_PASS);
        editor.putString("SP_UUID", UUID);
        editor.putString("SP_MAJOR", MAJOR);
        editor.putString("SP_UID", U_ID);
        editor.commit();

        ET_NAME.setText(P_USER);
        ET_IP.setText(DB_IP);
        ET_PORT.setText(DB_PORT);
        ET_DBUSER.setText(DB_USER);
        ET_DBPASS.setText(DB_PASS);
        ET_UUID.setText(UUID);
        ET_MAJOR.setText(MAJOR);
        ET_UID.setText(U_ID);
    }



}
