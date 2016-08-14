package com.example.acer.bluetoothdetector;

import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.service.RunningAverageRssiFilter;

//TODO not sure if supposed to import from java.sql, or mysql.jdbc
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
/*
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;
*/

import java.util.Calendar;
import java.util.Collection;

//import de.bezier.data.sql;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    private static final String TAG = "Beacon Test";
    private BeaconManager beacMan;

    // All beacons have been set to this UUID
    private static final String REGION_UUID = "A580C8B8-89FE-4548-8A24-472B7DE1224C";
    // Beacon 49893, Major: 0, Minor: 49893
    // Beacon 49994, Major: 0, Minor: 49994
    // Beacon 50179, Major: 0, Minor: 50179
    // Beacon 50337, Major: 0, Minor: 50337

    private static final String DB_IP = "172.20.10.3";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "app_test";
    private static final String DB_USER = "Ivan";
    private static final String DB_PASS = "Ivan";
    private static final String DB_TABLE = "test_data";
    private static final String COL_TIME = "ID";
    private static final String COL_NAME = "NAME";
    private static final String COL_BEAC1 = "";
    private static final String COL_BEAC2 = "";
    private static final String COL_BEAC3 = "";
    private static final String COL_BEAC4 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Activity Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            Log.d(TAG, "Error finding new instance of the driver class");
            e.printStackTrace();
        }

        try {
            String db = "jdbc:mysql://" + DB_IP + ":" + DB_PORT + "/" + DB_NAME;
            Connection connection = DriverManager.getConnection(db, DB_USER, DB_PASS);
            Log.d(TAG, "got connection");

            connection.prepareStatement(db);
            Statement statement = connection.createStatement();
            Log.d(TAG, "created statement");

            String query = "INSERT INTO " + DB_TABLE + " VALUES(2, 'Hello');";
            Log.d(TAG, query);
            ResultSet result = statement.executeQuery(query);
            Log.d(TAG, "query should be sent to db");

        } catch (Exception e) {
            Log.d(TAG, "Error in getting connection or sending query");
            e.printStackTrace();
        }
        */



        // Changes the delay for calculating distance from the default of 20 sec to 2.5 sec
        BeaconManager.setRssiFilterImplClass(RunningAverageRssiFilter.class);
        RunningAverageRssiFilter.setSampleExpirationMilliseconds(2500l);

        beacMan = BeaconManager.getInstanceForApplication(this);

        // Do not have to call beacMan.getBeaconParsers().add() if we stick to AltBeacon protocol
        beacMan.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beacMan.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.d(TAG, "Entered onBeaconServiceC0nnect()");

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
                for(Beacon beac : collection) {
                    Calendar now = Calendar.getInstance();
                    Log.d(TAG, "time (ms)" + now.getTimeInMillis() +
                            " distance: " + beac.getDistance() + " UUID:" + beac.getId1() +
                            " Major:" + beac.getId2() + " Minor:" + beac.getId3());

                    try {
                        Class.forName("com.mysql.jdbc.Driver").newInstance();
                    } catch (Exception e) {
                        Log.d(TAG, "Error finding new instance of the driver class");
                        e.printStackTrace();
                    }

                    try {
                        String db = "jdbc:mysql://" + DB_IP + ":" + DB_PORT + "/" + DB_NAME;
                        Connection connection = DriverManager.getConnection(db, DB_USER, DB_PASS);
                        if (connection == null) Log.d(TAG, "did NOT get connection");
                        else {
                            Log.d(TAG, "got connection");
                            Log.d(TAG, connection.getCatalog());
                        }

                        /*
                        connection.prepareStatement(db);
                        Statement statement = connection.createStatement();
                        Log.d(TAG, "created statement");
                        */

                        String query = "INSERT INTO " + DB_TABLE + " (ID, NAME) VALUES(3, 'Hello3');";
                        //String query = "SELECT * FROM " + DB_TABLE + ";";
                        Log.d(TAG, query);
                        PreparedStatement statement = connection.prepareStatement(query);
                        statement.execute();
                        Log.d(TAG, "query should be sent to db");
                        //Log.d(TAG, "Printing out result: " + result.getRow());

                    } catch (Exception e) {
                        Log.d(TAG, "Error in getting connection or sending query");
                        e.printStackTrace();
                    }

                }
            }
        });

        try {
            beacMan.startMonitoringBeaconsInRegion(reg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
