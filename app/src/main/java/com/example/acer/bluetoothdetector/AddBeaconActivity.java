package com.example.acer.bluetoothdetector;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

public class AddBeaconActivity extends AppCompatActivity {

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
    private static String MINOR;
    private static String TXPOWER;
    private static String LOCATION;



    EditText ET_UID;
    EditText ET_UUID;
    EditText ET_MAJOR;
    EditText ET_MINOR;
    EditText ET_TXPOWER;
    EditText ET_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beacon);

        ET_UID = (EditText) findViewById(R.id.ET_ADDBEACON_UID);
        ET_UUID = (EditText) findViewById(R.id.ET_ADDBEACON_UUID);
        ET_MAJOR = (EditText) findViewById(R.id.ET_ADDBEACON_MAJOR);
        ET_MINOR = (EditText) findViewById(R.id.ET_ADDBEACON_MINOR);
        ET_TXPOWER = (EditText) findViewById(R.id.ET_ADDBEACON_TXPOWER);
        ET_LOCATION = (EditText) findViewById(R.id.ET_ADDBEACON_LOCATION);

        /*
        SharedPreferences sp = getSharedPreferences("BluetoothDetectorData", Context.MODE_PRIVATE);
        Globals.P_USER = sp.getString("SP_USER", "");
        Globals.DB_IP = sp.getString("SP_IP", "");
        Globals.DB_PORT = sp.getString("SP_PORT", "");
        Globals.DB_USER = sp.getString("SP_DBUSER", "");
        Globals.DB_PASS = sp.getString("SP_DBPASS", "");
        Globals.UUID = sp.getString("SP_UUID", "");
        Globals.MAJOR = sp.getString("SP_MAJOR", "");
        Globals.U_ID = sp.getString("SP_UID", "");
        */

        ET_UID.setText(Globals.U_ID);
        ET_UUID.setText(Globals.UUID);
        ET_MAJOR.setText(Globals.MAJOR);
    }

    public void addBeaconPressed(View v) {
        Globals.U_ID = ET_UID.getText().toString();
        Globals.UUID = ET_UUID.getText().toString();
        Globals.MAJOR = ET_MAJOR.getText().toString();
        MINOR = ET_MINOR.getText().toString();
        TXPOWER = ET_TXPOWER.getText().toString();
        LOCATION = ET_LOCATION.getText().toString();

        new AddBeaconTask().execute();

        Toast.makeText(this, "Beacon added", Toast.LENGTH_LONG).show();
    }

    private class AddBeaconTask extends AsyncTask<URL, Integer, Long> {

        @Override
        protected Long doInBackground(URL... params) {

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (Exception e) {
                Log.d(Globals.TAG, "(in add beacon task) Error finding new instance of the driver class");
                e.printStackTrace();
                return null;
            }

            try {
                String db = "jdbc:mysql://" + Globals.DB_IP + ":" + Globals.DB_PORT + "/" + Globals.DB_NAME;
                Log.d(Globals.TAG, "Connection string: " + db);
                Connection connection = DriverManager.getConnection(db, Globals.DB_USER, Globals.DB_PASS);
                Log.d(Globals.TAG, "got connection");

                String query = "INSERT INTO " + Globals.DB_BTABLE + " VALUES(" + Globals.U_ID + ", '" + Globals.UUID +
                        "', "  + Globals.MAJOR + ", " + MINOR + ", " + TXPOWER + ", '" + LOCATION + "')";

                Log.d(Globals.TAG, query);
                PreparedStatement statement = connection.prepareStatement(query);
                statement.execute();
                Log.d(Globals.TAG, "query should be sent to db");
                connection.close();
                Log.d(Globals.TAG, "connection closed");

            } catch (SQLException e) {
                Log.d(Globals.TAG, "SQL exception");
                Log.d(Globals.TAG, "Error " + e.getErrorCode() + ": " + e.getSQLState());
                e.printStackTrace();
            }

            return null;
        }
    }


}
