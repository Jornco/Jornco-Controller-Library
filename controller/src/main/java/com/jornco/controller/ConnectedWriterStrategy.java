package com.jornco.controller;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.text.TextUtils;

import com.jornco.controller.ble.IronbotWriterCallback;
import com.jornco.controller.error.BLEWriterError;
import com.jornco.controller.util.BLELog;

/**
 * Created by kkopite on 2017/11/29.
 */

class ConnectedWriterStrategy implements IWriterStrategy {

    private static final int MOST_WRITE_LENGTH = 20;

    private BluetoothGattCharacteristic mWriterBGC;
    private BluetoothGatt mGatt;
    private IronbotWriterCallback mCallback;
    private String address;

    ConnectedWriterStrategy(String address) {
        this.address = address;
    }

    @Override
    public void write(String data, IronbotWriterCallback callback) {
        BLELog.log("对地址: " + address + " 发送: " + data);
        if(TextUtils.isEmpty(data) || (data.length() > MOST_WRITE_LENGTH)){
            callback.writerFailure(address, data, new BLEWriterError(address, data, "发送数据不能大于20个字符长度或者小于0"));
            return;
        }
        synchronized (this){
            BLELog.log("发送下一个");
            if((mGatt == null) || (mWriterBGC == null)){
                callback.writerFailure(address, data, new BLEWriterError(address, data, "当前设备可能已经断开"));
                return;
            } else if (mCallback != null) {
                callback.writerFailure(address, data, new BLEWriterError(address, data, "发送太快或者没做好同步, 上一个指令还未发送成功回调"));
            }
            mCallback = callback;
        }
        mWriterBGC.setValue(data);
        mGatt.writeCharacteristic(mWriterBGC);
    }

    @Override
    public void writeSuccess() {
        synchronized (this){
            if(mCallback != null) {
                IronbotWriterCallback tmp = this.mCallback;
                mCallback = null;
                tmp.writerSuccess(address);
            }
//            BLELog.log("发送完毕准备值为null");
//            mCallback = null;
        }
    }

    @Override
    public void writeFailure() {
        synchronized (this){
            if(mCallback != null) {
                String data = "";
                if (mWriterBGC != null) {
                    data = new String(mWriterBGC.getValue());
                }
                IronbotWriterCallback tmp = this.mCallback;
                mCallback = null;
                tmp.writerFailure(address, data, new BLEWriterError(address, data, "发送出现异常"));
            }
//            mCallback = null;
        }
    }

    @Override
    public void start(BluetoothGatt gatt, BluetoothGattCharacteristic writerBGC) {
        mGatt = gatt;
        mWriterBGC = writerBGC;
    }

    @Override
    public void stop() {
        synchronized (this) {
            mWriterBGC = null;
            mGatt = null;
            if(mCallback != null){
                mCallback.writerFailure(address, "", new BLEWriterError(address, "", "当前设备断开"));
            }
            mCallback = null;
        }
    }
}
