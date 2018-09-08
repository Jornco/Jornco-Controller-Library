package com.jornco.controller.ble;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kkopite on 2018/3/6.
 */

public class SensorTypeMap {

    private static Map<String, SensorType> map = new HashMap<>();

    static {
        // typeID 对应 类型
        map.put("001", SensorType.COLOR);
        map.put("002", SensorType.ULTRASONIC);
        map.put("003", SensorType.COLOR);
        map.put("003", SensorType.TRACKING);
        map.put("004", SensorType.KEY);
        map.put("005", SensorType.HUMITURE);
        map.put("006", SensorType.PHOTOSENSITIVE);
        map.put("007", SensorType.VOICE);
        map.put("008", SensorType.INFRARED_REC);
        map.put("101", SensorType.RGB);
        map.put("102", SensorType.LED);
        map.put("103", SensorType.BEE);
        map.put("104", SensorType.INFRARED_SEND);
        map.put("105", SensorType.SEGMENT_DISPLAYS);

    }

    public static SensorType getType(String typeID) {
        return map.get(typeID);
    }

}
