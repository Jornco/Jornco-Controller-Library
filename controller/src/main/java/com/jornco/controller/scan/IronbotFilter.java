package com.jornco.controller.scan;

/**
 * 过滤接口
 * Created by kkopite on 2017/10/25.
 */

public interface IronbotFilter {

    /**
     *
     * @param name 扫描到的蓝牙设备名称
     * @return false 就过滤掉
     */
    boolean filter(String name);
}
