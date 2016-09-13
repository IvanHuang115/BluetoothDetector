package com.example.acer.bluetoothdetector;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DevicesActivity extends AppCompatActivity {

    ListView listBeacons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        listBeacons = (ListView) findViewById(R.id.LV_BEACONS);
        // listBeacons is set in onPostExecute() of the beacon task
        new BeaconDisplayTask().execute();


    }

    @Override
    protected void onResume() {
        super.onResume();
        new BeaconDisplayTask().execute();
    }

    public void addBeaconActivityPressed(View v) {
        Intent i = new Intent(this, AddBeaconActivity.class);
        startActivity(i);
    }

    // this class is responsible for getting an arraylist of beacons from the database and
    // pass it to the adapter to populate the listview
    private class BeaconDisplayTask extends AsyncTask<URL, Integer, Long> {

        ArrayList<Beacon> beacons;

        @Override
        protected void onPreExecute() {
            Log.d(Globals.TAG, "inside beacondisplaytask pre");
            super.onPreExecute();
            beacons = new ArrayList<Beacon>();
        }

        // connect to the database and get an array of beacons
        @Override
        protected Long doInBackground(URL... params) {

            String bQuery = "SELECT * FROM " + Globals.DB_BTABLE;
            ResultSet res = Globals.query(bQuery);

            /*
            // debug values
            beacons.add(new Beacon(0, "abc", 1, 2, 3, "microwave"));
            beacons.add(new Beacon(0, "def", 4, 5, 6, "fridge"));
            beacons.add(new Beacon(0, "ghi", 7, 8, 9, "fan"));
            */
            if (res == null) return null;
            try {
                while (res.next()) {
                    int uid = res.getInt("UID");
                    String uuid = res.getString("UUID");
                    int major = res.getInt("MAJOR");
                    int minor = res.getInt("MINOR");
                    int txpower = res.getInt("TXPOWER");
                    String location = res.getString("LOCATION");
                    beacons.add(new Beacon(uid, uuid, major, minor, txpower, location));
                }
            } catch (SQLException e) {
                Log.d(Globals.TAG, "in beacon display task: error in getting db stuff");
            }
            return null;
        }

        // create a new adapter for the listview with the array of beacons
        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);


            listBeacons.setAdapter(new BeaconArrayAdapter(DevicesActivity.this, beacons));

        }
    }

}



