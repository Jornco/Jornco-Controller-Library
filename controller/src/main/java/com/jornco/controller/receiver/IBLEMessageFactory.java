package com.jornco.controller.receiver;

/**
 * Created by kkopite on 2018/3/6.
 */

public interface IBLEMessageFactory {

    BLEMessage createBLEMessage(String address, byte[] data);

    // 解析塔克传回的数据
    BLEMessage createTuckBLEMessage(String address, byte[] data);
}
