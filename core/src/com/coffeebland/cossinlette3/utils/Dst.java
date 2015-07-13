package com.coffeebland.cossinlette3.utils;

public class Dst {
    public static float getAsMeters(int pixels) {
        return pixels * Const.METERS_PER_PIXEL;
    }
    public static float getAsPixels(float meters) {
        return meters / Const.METERS_PER_PIXEL;
    }
}
