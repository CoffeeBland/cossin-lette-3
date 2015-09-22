package com.coffeebland.cossinlette3.game.file;

import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.utils.Dst;
import org.jetbrains.annotations.NotNull;

public class PolygonDef extends ActorDef {
    public float x, y;
    public float[] points;

    public PolygonDef() {}

    public float[] getPixelPoints(@NotNull float[] tmpPoints, @NotNull Vector2 offset) {
        float[] pixelPoints = tmpPoints.length < points.length ? new float[points.length] : tmpPoints;
        for (int i = 0; i < points.length; i+=2) {
            pixelPoints[i] = Dst.getAsPixels(points[i] - offset.x);
            pixelPoints[i + 1] = Dst.getAsPixels(points[i + 1] - offset.y);
        }
        return pixelPoints;
    }
}
