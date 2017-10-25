package com.jornco.controller;

/**
 * Created by kkopite on 2017/10/25.
 */

public class IronbotInfo {

    private final String name;
    private final String address;
    private BLEState state;

    public IronbotInfo(String name, String address) {
        this(name, address, BLEState.DISCONNECT);
    }

    public IronbotInfo(String name, String address, BLEState state) {
        this.name = name;
        this.address = address;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public BLEState getState() {
        return state;
    }

    public void setState(BLEState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "IronbotInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", state=" + state +
                '}';
    }
}
