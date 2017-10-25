package com.jornco.controller;

import android.util.Log;

/**
 * Created by kkopite on 2017/10/25.
 */

public class BLELog {

    private static final String TAG = "BLELog";

    public static void log(String msg){
        Log.e(TAG, "log: " + msg);
    }

}
