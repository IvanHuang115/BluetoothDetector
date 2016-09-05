package com.example.acer.bluetoothdetector;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Acer on 9/2/2016.
 */
public class Globals {
    public static final String TAG = "Beacon Test";

    public static final String DB_NAME = "BeaconDatabase";
    public static final String DB_PTABLE = "ProximityData";
    public static final String DB_UTABLE = "UserData";
    public static final String DB_BTABLE = "BeaconData";
    public static String P_ID;
    public static String P_USER;
    public static String DB_IP;
    public static String DB_PORT;
    public static String DB_USER;
    public static String DB_PASS;
    public static String UUID;
    public static String MAJOR;
    public static String U_ID;

    public static ResultSet query(String q) {
        ResultSet result = null;
        try {
            String db = "jdbc:mysql://" + Globals.DB_IP + ":" + Globals.DB_PORT + "/" + Globals.DB_NAME;
            Connection connection = DriverManager.getConnection(db, Globals.DB_USER, Globals.DB_PASS);
            PreparedStatement statement = connection.prepareStatement(q);
            result = statement.executeQuery();
            connection.close();
        } catch (SQLException e) {
            Log.d(Globals.TAG, "(in async task) Error in checking/inserting PID");
            Log.d(Globals.TAG, "(in async task) SQL exception");
            Log.d(Globals.TAG, "(in async task) Error " + e.getErrorCode() + ": " + e.getSQLState());
            e.printStackTrace();
        }
        return result;
    }
}
