package com.jornco.controller.scan;

import com.jornco.controller.IronbotInfo;

/**
 * Created by kkopite on 2017/10/25.
 */

public interface IronbotSearcherCallback {
    void onIronbotFound(IronbotInfo info);
}
