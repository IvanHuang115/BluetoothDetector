package com.example.acer.bluetoothdetector;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by Acer on 9/2/2016.
 */
public class BeaconArrayAdapter extends BaseAdapter {

    ArrayList<Beacon> beaconArray;
    Context context;

    public BeaconArrayAdapter(Context c, ArrayList<Beacon> beacons) {
        context = c;
        beaconArray = beacons;
    }

    @Override
    public int getCount() {
        return beaconArray.size();
    }

    @Override
    public Object getItem(int position) {
        return beaconArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) ((Beacon) getItem(position)).minor;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = li.inflate(R.layout.row_beacon, parent, false);

        TextView tv_location = (TextView) row.findViewById(R.id.TV_BEACON_LOCATION);
        TextView tv_uuid = (TextView) row.findViewById(R.id.TV_BEACON_UUID);
        TextView tv_major = (TextView) row.findViewById(R.id.TV_BEACON_MAJOR);
        TextView tv_minor = (TextView) row.findViewById(R.id.TV_BEACON_MINOR);
        TextView tv_txpower = (TextView) row.findViewById(R.id.TV_BEACON_TXPOWER);

        Beacon beac = beaconArray.get(position);
        tv_location.setText(beac.location);
        tv_uuid.setText("UUID: " + beac.UUID);
        tv_major.setText("Major: " + Integer.toString(beac.major));
        tv_minor.setText("Minor: " + Integer.toString(beac.minor));
        tv_txpower.setText("TX: " + Integer.toString(beac.txpower));
        return row;
    }


}

class Beacon {
    int UID;
    String UUID;
    int major;
    int minor;
    int txpower;
    String location;

    public Beacon(int UID, String UUID, int major, int minor, int txpower, String location) {
        this.UID = UID;
        this.UUID = UUID;
        this.major = major;
        this.minor = minor;
        this.txpower = txpower;
        this.location = location;
    }
}
