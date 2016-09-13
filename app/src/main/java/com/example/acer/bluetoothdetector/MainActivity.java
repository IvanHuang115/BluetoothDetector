package com.example.acer.bluetoothdetector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    /*
    private static final String TAG = "Beacon Test";

    private static final String DB_NAME = "BeaconDatabase";
    private static final String DB_TABLE = "ProximityData";
    private static final String DB_UTABLE = "UserData";
    private static final String DB_BTABLE = "BeaconData";
    private static String P_ID;
    private static String P_USER;
    private static String DB_IP;
    private static String DB_PORT;
    private static String DB_USER;
    private static String DB_PASS;
    private static String UUID;
    private static String MAJOR;
    private static String U_ID;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(Globals.TAG, "Activity Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Globals.status = "stopped";

        // gets the unique identifier for each android device
        /*
        TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        Globals.P_ID = tManager.getDeviceId();
        Log.d(Globals.TAG, "device id: " + Globals.P_ID);
        */

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
        Globals.P_USER = sp.getString("SP_USER", "");
        Globals.DB_IP = sp.getString("SP_IP", "");
        Globals.DB_PORT = sp.getString("SP_PORT", "");
        Globals.DB_USER = sp.getString("SP_DBUSER", "");
        Globals.DB_PASS = sp.getString("SP_DBPASS", "");
        Globals.UUID = sp.getString("SP_UUID", "");
        Globals.MAJOR = sp.getString("SP_MAJOR", "");
        Globals.U_ID = sp.getString("SP_UID", "");
        Log.d(Globals.TAG, "user: " + Globals.P_USER + " ip: " + Globals.DB_IP + " port: " + Globals.DB_PORT);

        // gets printed if it's the first time using the app
        if (Globals.P_USER.equals("") || Globals.DB_IP.equals("") || Globals.DB_PORT.equals("")) {
            Toast.makeText(this, "Please enter your name and the IP address of your Raspberry Pi",
                    Toast.LENGTH_LONG).show();
        }

        // displays previous user input
        ET_NAME.setText(Globals.P_USER);
        ET_IP.setText(Globals.DB_IP);
        ET_PORT.setText(Globals.DB_PORT);
        ET_DBUSER.setText(Globals.DB_USER);
        ET_DBPASS.setText(Globals.DB_PASS);
        ET_UUID.setText(Globals.UUID);
        ET_MAJOR.setText(Globals.MAJOR);
        ET_UID.setText(Globals.U_ID);
    }

    @Override
    protected void onDestroy() {
        Intent i = new Intent(MainActivity.this, BluetoothDetectorService.class);
        stopService(i);
        super.onDestroy();
    }

    public void devicesPressed(View v) {
        Intent i = new Intent(this, DevicesActivity.class);
        startActivity(i);
    }


    // gets called when Enter button is pressed
    public void enterUserInfo(View v) {
        Globals.P_USER = ET_NAME.getText().toString();
        Globals.DB_IP = ET_IP.getText().toString();
        Globals.DB_PORT = ET_PORT.getText().toString();
        Globals.DB_USER = ET_DBUSER.getText().toString();
        Globals.DB_PASS = ET_DBPASS.getText().toString();
        Globals.UUID = ET_UUID.getText().toString();
        Globals.MAJOR = ET_MAJOR.getText().toString();
        Globals.U_ID = ET_UID.getText().toString();

        submitSavedPreferences();
        Toast.makeText(this, "Input Received", Toast.LENGTH_LONG).show();
    }

    // gets called when Start Service button is pressed
    public void startServicePressed(View v) {

        // check to see if the inputs are acceptable
        SharedPreferences sp = getSharedPreferences("BluetoothDetectorData", Context.MODE_PRIVATE);
        if (sp.getBoolean("SP_CONNECTIONERROR", false)) {
            Toast.makeText(MainActivity.this, "Error: cannot connect to the database " +
                    "based on the inputs, please reenter", Toast.LENGTH_LONG).show();
            return;
        } else {
            //Toast.makeText(this, "Service started",
            //        Toast.LENGTH_LONG).show();
        }

        // create a new thread to run the service
        Thread t = new Thread() {

            public void run() {
                Intent i = new Intent(MainActivity.this, BluetoothDetectorService.class);
                startService(i);
            }

        };
        t.start();
    }

    // gets called when Stop Service button is pressed
    public void stopServicePressed(View v) {
        Globals.status = "stopped";
        Intent i = new Intent(MainActivity.this, BluetoothDetectorService.class);
        stopService(i);
    }

    // gets called when the default values button is pressed
    public void debugPressed(View v) {
        Globals.P_USER = "debug";
        Globals.DB_IP = "192.168.0.24";
        Globals.DB_PORT = "3306";
        Globals.DB_USER = "beaconuser";
        Globals.DB_PASS = "seelab";
        Globals.UUID = "A580C8B8-89FE-4548-8A24-472B7DE1224C";
        Globals.MAJOR = "0";
        Globals.U_ID = "1234";

        submitSavedPreferences();
    }

    public void checkStatusPressed(View v) {
        if (Globals.status.equals("sqlerror")) {
            Toast.makeText(this, "SQL Error: Nothing is being sent to the database",
                    Toast.LENGTH_LONG).show();
        } else if (Globals.status.equals("stopped")) {
            Toast.makeText(this, "Service is stopped or hasn't started", Toast.LENGTH_LONG).show();
        } else if (Globals.status.equals("empty")) {
            Toast.makeText(this, "Service is running, but no beacons in range. Nothing sent to database",
                    Toast.LENGTH_LONG).show();
        } else if (Globals.status.equals("fine")){
                Toast.makeText(this, "Everything is fine, data is being sent to the database",
                        Toast.LENGTH_LONG).show();
        }

    }

    // gets called in enterUserInfo() and debugPressed(), saves the user data into a saved
    // preferences file
    public void submitSavedPreferences() {
        SharedPreferences sp = getSharedPreferences("BluetoothDetectorData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        // TODO Check for blank entry, eliminate white spaces
        editor.putString("SP_USER", Globals.P_USER);
        editor.putString("SP_IP", Globals.DB_IP);
        editor.putString("SP_PORT", Globals.DB_PORT);
        editor.putString("SP_DBUSER", Globals.DB_USER);
        editor.putString("SP_DBPASS", Globals.DB_PASS);
        editor.putString("SP_UUID", Globals.UUID);
        editor.putString("SP_MAJOR", Globals.MAJOR);
        editor.putString("SP_UID", Globals.U_ID);
        editor.commit();

        ET_NAME.setText(Globals.P_USER);
        ET_IP.setText(Globals.DB_IP);
        ET_PORT.setText(Globals.DB_PORT);
        ET_DBUSER.setText(Globals.DB_USER);
        ET_DBPASS.setText(Globals.DB_PASS);
        ET_UUID.setText(Globals.UUID);
        ET_MAJOR.setText(Globals.MAJOR);
        ET_UID.setText(Globals.U_ID);

        // check to see if the inputted values provide a valid connection
        new CheckTask().execute();
    }

    // this class runs as a separate thread that checks to see if there is a valid connection to
    // the database. it also checks to see if the user's data is already in the database; if not,
    // it adds it
    private class CheckTask extends AsyncTask<URL, Integer, Long> {

        @Override
        protected Long doInBackground(URL... params) {
            SharedPreferences sp = getSharedPreferences("BluetoothDetectorData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();

            // checks to see if the jdbc driver class exists
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                editor.putBoolean("SP_CONNECTIONERROR", false);
                editor.commit();
            } catch (Exception e) {
                Log.d(Globals.TAG, "(in async task) Error finding new instance of the driver class");
                e.printStackTrace();
                editor.putBoolean("SP_CONNECTIONERROR", true);
                editor.commit();
                return null;
            }

            // checks to see if we can connect to the database based on the inputs given
            // store result into a shared preferences key/value, for startServicePressed() to check
            try {
                String db = "jdbc:mysql://" + Globals.DB_IP + ":" + Globals.DB_PORT + "/" + Globals.DB_NAME;
                Log.d(Globals.TAG, "(in async task) Connection string: " + db);
                Connection connection = DriverManager.getConnection(db, Globals.DB_USER, Globals.DB_PASS);
                Log.d(Globals.TAG, "(in async task) got connection");
                connection.close();
                editor.putBoolean("SP_CONNECTIONERROR", false);
                editor.commit();
            } catch (SQLException e) {
                Log.d(Globals.TAG, "(in async task) sql error");
                editor.putBoolean("SP_CONNECTIONERROR", true);
                editor.commit();
                return null;
            }

            // checks to see if this user already exists in the database, if not, add it
            //TODO replace all this by calling query()
            try {
                String db = "jdbc:mysql://" + Globals.DB_IP + ":" + Globals.DB_PORT + "/" + Globals.DB_NAME;
                Log.d(Globals.TAG, "(in async task) Connection string: " + db);
                Connection connection = DriverManager.getConnection(db, Globals.DB_USER, Globals.DB_PASS);
                Log.d(Globals.TAG, "(in async task) got connection");
                String query = "SELECT * FROM " + Globals.DB_UTABLE + " WHERE PID = " + Globals.P_ID;
                Log.d(Globals.TAG, query);
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet result = statement.executeQuery();
                Log.d(Globals.TAG, "(in async task) check PID query should be sent to db");
                if (!result.next()) {
                    query = "INSERT INTO " + Globals.DB_UTABLE + " VALUES(" + Globals.U_ID + ", " + Globals.P_ID + ", '"
                            + Globals.P_USER + "')";
                    Log.d(Globals.TAG, query);
                    statement = connection.prepareStatement(query);
                    statement.execute();
                    Log.d(Globals.TAG, "inserted new user into the database");
                }
                result.close();
                connection.close();
                Log.d(Globals.TAG, "connection closed");
            } catch (SQLException e) {
                Log.d(Globals.TAG, "(in async task) Error in checking/inserting PID");
                Log.d(Globals.TAG, "(in async task) SQL exception");
                Log.d(Globals.TAG, "(in async task) Error " + e.getErrorCode() + ": " + e.getSQLState());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
        }
    }


}
