package com.jornco.controller.ble;

/**
 * 接受设备传回信息的类型
 * Created by kkopite on 2018/3/6.
 */

public enum  SensorType {
    // 再加一个描述信息会不会好点?

    // 没有选择
    NONE(DataType.NONE, "0"),

    // 接受信息的传感器
    COLOR(DataType.REC, "001"),
    ULTRASONIC(DataType.REC, "002"),
    TRACKING(DataType.REC, "003"),
    KEY(DataType.REC, "004"),
    HUMITURE(DataType.REC, "005"),
    PHOTOSENSITIVE(DataType.REC, "006"),
    VOICE(DataType.REC, "007"),
    INFRARED_REC(DataType.REC, "008"),

    // 发送信息的传感器
    RGB(DataType.SEND, "101"),
    LED(DataType.SEND, "102"),
    BEE(DataType.SEND, "103"),
    INFRARED_SEND(DataType.SEND, "104"),
    SEGMENT_DISPLAYS(DataType.SEND, "105");

    private final DataType mDataType;
    private final String typeId;

    SensorType(DataType type, String typeId) {
        mDataType = type;
        this.typeId = typeId;
    }

    public String getTypeId() {
        return typeId;
    }

    public DataType getDataType() {
        return mDataType;
    }
}

