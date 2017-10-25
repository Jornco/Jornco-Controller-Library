package com.jornco.controller.receiver;

/**
 * Created by kkopite on 2017/10/25.
 */

public interface BLEReceiver {
    boolean receiver(BLEMessage message);

    /**
     * Created by kkopite on 2017/10/25.
     */

    class BLEMessage {
    }
}
