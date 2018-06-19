package com.jornco.controller;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.jornco.controller.ble.IronbotWriterCallback;
import com.jornco.controller.error.BLEWriterError;

/**
 * Created by kkopite on 2017/11/29.
 */

class DisconnectedWriterStrategy implements IWriterStrategy {

    private String address;

    DisconnectedWriterStrategy(String address) {
        this.address = address;
    }

    @Override
    public void write(byte[] data, IronbotWriterCallback callback) {
        callback.writerFailure(address, data, new BLEWriterError(address, data, "当前设备已断开"));
    }

    @Override
    public void writeSuccess() {

    }

    @Override
    public void writeFailure() {

    }

    @Override
    public void start(BluetoothGatt gatt, BluetoothGattCharacteristic writerBGC) {

    }

    @Override
    public void stop() {

    }
}
