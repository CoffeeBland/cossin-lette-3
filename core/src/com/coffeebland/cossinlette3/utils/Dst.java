
package com.coffeebland.cossinlette3.utils;

import com.badlogic.gdx.math.Vector2;

public class Dst {
    public static float getAsMeters(int pixels) {
        return pixels * Const.METERS_PER_PIXEL;
    }
    public static float getAsMeters(float pixels) {
        return pixels * Const.METERS_PER_PIXEL;
    }
    public static Vector2 getAsMeters(Vector2 pixels) {
        return pixels.scl(Const.METERS_PER_PIXEL);
    }
    public static float getAsPixels(float meters) {
        return meters / Const.METERS_PER_PIXEL;
    }
    public static Vector2 getAsPixels(Vector2 meters) {
        return meters.scl(1f / Const.METERS_PER_PIXEL);
    }
}
