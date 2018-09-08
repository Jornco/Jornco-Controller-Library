package com.jornco.demo.activity.adapter;

import com.jornco.controller.ble.SensorType;

/**
 * Created by kkopite on 2018/3/6.
 */

public class SensorTypeBean {

    private String msg;
    private SensorType mType;

    public SensorTypeBean(String msg, SensorType type) {
        this.msg = msg;
        mType = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public SensorType getType() {
        return mType;
    }

    public void setType(SensorType type) {
        mType = type;
    }
}
