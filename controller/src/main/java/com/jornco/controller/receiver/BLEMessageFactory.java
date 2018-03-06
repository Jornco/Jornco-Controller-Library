package com.jornco.controller.receiver;

import android.util.SparseArray;

import com.jornco.controller.ble.SensorType;
import com.jornco.controller.ble.SensorTypeMap;
import com.jornco.controller.util.BLELog;

import java.util.Arrays;

/**
 * Created by kkopite on 2018/3/6.
 */

public class BLEMessageFactory implements IBLEMessageFactory {
    @Override
    public BLEMessage createBLEMessage(String address, String msg, SparseArray<SensorType> array) {
        BLEMessage message = new BLEMessage(address, msg);
        if (msg != null) {
            if (msg.startsWith("#F")) {
                // #F101,1,12,13,*
                String replace = msg.replaceFirst("#F", "").replace(",*", "");
                String[] split = replace.split(",");
                if (split.length >= 3) {
                    // 起码要三个, 第一个为101 表示typeID,  第二个表示端口
                    message.setType(SensorTypeMap.getType(split[0]));
                    message.setPort(Integer.parseInt(split[1]));
                    String[] data = Arrays.copyOfRange(split,2, split.length - 1);
                    message.setRecData(data);
                } else {
                    BLELog.log("返回的传感器数据错误: " + msg);
                }
            }
        }
        return message;
    }
}
