package com.jornco.controller;

/**
 * Created by kkopite on 2018/9/13.
 */

public class Test {

    @org.junit.Test
    public void test_isCorrect() {
        byte a = (byte) 0xff;
        System.out.println(a << 8);
    }
}
