package com.jornco.controller.receiver;

import android.util.SparseArray;

import com.jornco.controller.ble.SensorType;

/**
 * Created by kkopite on 2018/3/6.
 */

public interface IBLEMessageFactory {

    BLEMessage createBLEMessage(String address, String msg, SparseArray<SensorType> array);
}
