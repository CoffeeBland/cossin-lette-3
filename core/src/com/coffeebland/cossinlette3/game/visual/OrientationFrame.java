package com.coffeebland.cossinlette3.game.visual;

import com.coffeebland.cossinlette3.game.file.WorldFile;

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
    public OrientationFrame(int frameY, boolean flip, double startAngle, double endAngle) {
        this(frameY, flip, (float)startAngle, (float)endAngle);
    }
    public OrientationFrame(WorldFile.OrientationFrameDef def) {
        this(def.frameY, def.flip, def.startAngle, def.endAngle);;
    }
}
