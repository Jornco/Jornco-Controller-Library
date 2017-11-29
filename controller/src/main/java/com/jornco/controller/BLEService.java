package com.jornco.controller;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by kkopite on 2017/11/29.
 */

public class BLEService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new BLEBinder();
    }


}
