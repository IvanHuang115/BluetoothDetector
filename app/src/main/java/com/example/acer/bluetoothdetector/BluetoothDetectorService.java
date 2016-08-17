package com.example.acer.bluetoothdetector;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.service.RunningAverageRssiFilter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;

/**
 * Created by Acer on 8/15/2016.
 */

public class BluetoothDetectorService extends Service implements BeaconConsumer {

    private static final String TAG = "Beacon Test";
    private BeaconManager beacMan;
    // All beacons have been set to this UUID
    private static final String REGION_UUID = "A580C8B8-89FE-4548-8A24-472B7DE1224C";
    // Beacon 49893, Major: 0, Minor: 49893 ==> Beacon1
    // Beacon 49994, Major: 0, Minor: 49994 ==> Beacon2
    // Beacon 50179, Major: 0, Minor: 50179 ==> Beacon3
    // Beacon 50337, Major: 0, Minor: 50337 ==> Beacon4

    private static String P_ID;
    private static String P_USER;
    private static String DB_IP = "137.110.97.150";
    private static String DB_PORT = "3306";
    private static final String DB_NAME = "app_test";
    private static final String DB_USER = "Ivan";
    private static final String DB_PASS = "Ivan";
    private static final String DB_TABLE = "bluetoothdetectortest";
    private static final String COL_PID = "PID";
    private static final String COL_MONTH = "MONTH";
    private static final String COL_DAY = "DAY";
    private static final String COL_MS = "MS";
    private static final String COL_BEAC = "BEACON";
    private static final String COL_RSSI = "RSSI";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(BluetoothDetectorService.this, "Service started, please remember to press " +
                "the stop service button before destroying the app in the application manager",
                Toast.LENGTH_LONG).show();

        Bundle b = intent.getExtras();
        P_USER = b.getString("USER");
        DB_IP = b.getString("IP");
        DB_PORT = b.getString("PORT");

        TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        P_ID = tManager.getDeviceId();
        Log.d(TAG, "device id: " + P_ID);

        // Changes the delay for calculating distance from the default of 20 sec to 2.5 sec
        BeaconManager.setRssiFilterImplClass(RunningAverageRssiFilter.class);
        RunningAverageRssiFilter.setSampleExpirationMilliseconds(2500l);

        beacMan = BeaconManager.getInstanceForApplication(BluetoothDetectorService.this);
        // Do not have to call beacMan.getBeaconParsers().add() if we stick to AltBeacon protocol
        beacMan.bind(BluetoothDetectorService.this);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beacMan.unbind(BluetoothDetectorService.this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.d(TAG, "Entered onBeaconServiceConnect()");

        // Only detects beacons with a specific UUID
        final Region reg = new Region("RadBeacons", Identifier.parse(REGION_UUID), null, null);

        beacMan.addMonitorNotifier(new MonitorNotifier() {

            @Override
            public void didEnterRegion(Region region) {
                try {
                    Log.d(TAG, "didEnterRegion");
                    beacMan.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    Log.d(TAG, "didExitRegion");
                    beacMan.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });

        beacMan.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {

                if (collection.isEmpty()) {
                    beacMan.unbind(BluetoothDetectorService.this);
                    Intent i = new Intent(BluetoothDetectorService.this, BluetoothDetectorService.class);
                    stopService(i);
                    Log.d(TAG, "Service stopped because no beacons in range");
                }

                // TODO replace with UTC time format in a string
                Calendar now = Calendar.getInstance();
                int month = now.get(Calendar.MONTH);
                int day = now.get(Calendar.DAY_OF_MONTH);
                long time = now.getTimeInMillis();

                for(Beacon beac : collection) {

                    Log.d(TAG, "time (ms)" + time +
                            " distance: " + beac.getDistance() + " UUID:" + beac.getId1() +
                            " Major:" + beac.getId2() + " Minor:" + beac.getId3() +
                            " RSSI:" + beac.getRssi());

                    try {
                        Class.forName("com.mysql.jdbc.Driver").newInstance();
                    } catch (Exception e) {
                        Log.d(TAG, "Error finding new instance of the driver class");
                        e.printStackTrace();
                    }

                    try {
                        String db = "jdbc:mysql://" + DB_IP + ":" + DB_PORT + "/" + DB_NAME;
                        Log.d(TAG, "Connection string: " + db);
                        Connection connection = DriverManager.getConnection(db, DB_USER, DB_PASS);
                        Log.d(TAG, "got connection");

                        //String query = "INSERT INTO " + DB_TABLE + " (ID, NAME) VALUES(" + ms + ", 'Hello3');";
                        String query = "insert into " + DB_TABLE + " values('" + P_ID + "', '" +
                                beac.getId3().toString() + "', " + month + ", " + day + ", " +
                                time + ", " + beac.getDistance() + ", " + beac.getRssi() + ");";
                        Log.d(TAG, query);
                        PreparedStatement statement = connection.prepareStatement(query);
                        statement.execute();
                        Log.d(TAG, "query should be sent to db");
                        connection.close();
                        Log.d(TAG, "connection closed");

                    } catch (Exception e) {
                        Log.d(TAG, "Error in sending query");
                        e.printStackTrace();
                    }

                }
            }
        });

        try {
            beacMan.startMonitoringBeaconsInRegion(reg);
        } catch (RemoteException e) {
            Log.d(TAG, "problem in startMonitoringBeaconsInRegion");
            e.printStackTrace();
        }
    }
}
