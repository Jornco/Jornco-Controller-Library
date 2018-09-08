package com.jornco.controller.ble;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

/**
 * Created by kkopite on 2018/3/6.
 */

public class SensorBean {

    @IntRange(from = 0, to = 5)
    private final int port;
    @NonNull
    private final SensorType mType;

    public SensorBean(int port, @NonNull SensorType type) {
        this.port = port;
        mType = type;
    }

    public int getPort() {
        return port;
    }

    @NonNull
    public SensorType getType() {
        return mType;
    }
}
