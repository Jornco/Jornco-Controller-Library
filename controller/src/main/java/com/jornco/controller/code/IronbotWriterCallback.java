package com.jornco.controller.code;

import com.jornco.controller.error.BLEWriterError;

/**
 * 写入蓝牙设备成功/失败回调
 * Created by kkopite on 2017/10/25.
 */

public interface IronbotWriterCallback {

    void writerSuccess();

    void writerFailure(BLEWriterError error);

}
