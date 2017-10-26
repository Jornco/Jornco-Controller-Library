package com.jornco.controller.receiver;

/**
 *
 * Created by kkopite on 2017/10/25.
 */

public interface BLEReceiver {
    boolean onReceiveMessage(BLEMessage message);
}
