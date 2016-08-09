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

import java.util.Calendar;
import java.util.Collection;

//import de.bezier.data.sql;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    private static final String TAG = "Beacon Test";
    // All beacons have been set to this UUID
    private static final String REGION_UUID = "A580C8B8-89FE-4548-8A24-472B7DE1224C";

    // Beacon 49893, Major: 0, Minor: 49893
    // Beacon 49994, Major: 0, Minor: 49994
    // Beacon 50179, Major: 0, Minor: 50179
    // Beacon 50337, Major: 0, Minor: 50337

    private BeaconManager beacMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Activity Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        Log.d(TAG, "Entered onBeaconServiceCOnnect()");
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
