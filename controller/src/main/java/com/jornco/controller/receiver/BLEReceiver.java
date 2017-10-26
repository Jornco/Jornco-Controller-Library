package com.jornco.controller.receiver;

/**
 *
 * Created by kkopite on 2017/10/25.
 */

public interface BLEReceiver {

    // 检查是否是需要的信息
    boolean onReceiveMessage(BLEMessage message);

}
