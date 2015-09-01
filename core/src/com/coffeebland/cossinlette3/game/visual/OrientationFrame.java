package com.coffeebland.cossinlette3.game.visual;

public class OrientationFrame {
    public final int frameY;
    public final boolean flip;
    public final float startAngle, endAngle;

    public OrientationFrame(int frameY, boolean flip, float startAngle, float endAngle) {
        this.frameY = frameY;
        this.flip = flip;
        this.startAngle = startAngle;
        this.endAngle = endAngle;
    }
}
