package com.jornco.controller.util;

import android.util.Log;

import com.jornco.controller.BuildConfig;

/**
 * 日志打印
 * Created by kkopite on 2017/10/25.
 */

public class BLELog {

    private static final String TAG = "BLELog";

    public static void log(String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "log: " + msg);
        }
    }
}
