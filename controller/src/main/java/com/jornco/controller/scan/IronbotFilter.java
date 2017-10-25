package com.jornco.controller.scan;

import android.bluetooth.BluetoothDevice;

/**
 * Created by kkopite on 2017/10/25.
 */

public interface IronbotFilter {

    boolean filter(BluetoothDevice info);
}
