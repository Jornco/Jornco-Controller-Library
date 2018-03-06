package com.jornco.controller.ble;

/**
 * 该传感器类型是主动传, 还是接收的
 */
public enum DataType {
    // 用户手动传递
    SEND,
    // 200ms接收信息的
    REC
}
