package com.jornco.controller;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.jornco.controller.ble.IronbotWriterCallback;

/**
 * Created by kkopite on 2017/11/29.
 */

interface IWriterStrategy {
    void write(byte[] data, IronbotWriterCallback callback);
    void writeSuccess();
    void writeFailure();
    void start(BluetoothGatt gatt, BluetoothGattCharacteristic writerBGC);
    void stop();
}
