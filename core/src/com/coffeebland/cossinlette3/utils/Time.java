package com.coffeebland.cossinlette3.utils;

public class Time {
    public static float nano_to_milli(long ns) {
        return ns / 1_000_000.0f;
    }
}
