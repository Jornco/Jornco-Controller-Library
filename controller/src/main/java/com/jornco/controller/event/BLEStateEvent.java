package com.jornco.controller.event;

import com.jornco.controller.BLEState;

/**
 * Created by kkopite on 2017/10/25.
 */

public class BLEStateEvent {

    private final String address;
    private final BLEState state;

    public BLEStateEvent(String address, BLEState state) {
        this.address = address;
        this.state = state;
    }

    public String getAddress() {
        return address;
    }

    public BLEState getState() {
        return state;
    }
}
