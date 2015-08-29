package com.coffeebland.cossinlette3.game.visual;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ImageStrip extends ImageSheet {
    protected final float fps;
    protected final List<OrientationFrame> frames;

    public ImageStrip(@NotNull TextureAtlas atlas, @NotNull String src, int frameWidth, int frameHeight, int decalX, int decalY, float fps, List<OrientationFrame> frames) {
        super(atlas, src, frameWidth, frameHeight, decalX, decalY);
        this.frames = frames;
        this.fps = fps;
    }

    public void render(@NotNull Batch batch, float x, float y, int frameX, float orientation, float scale) {
        for (OrientationFrame frame : frames) {
            if (orientation >= frame.startAngle && orientation < frame.endAngle) {
                render(batch, x, y, frameX, frame.frameY, scale, frame.flip);
                return;
            }
        }
    }
    public void render(@NotNull Batch batch, float x, float y, int frameX, float orientation, float scale, @NotNull Color tint) {
        for (OrientationFrame frame : frames) {
            if (orientation >= frame.startAngle && orientation < frame.endAngle) {
                render(batch, x, y, frameX, frame.frameY, scale, frame.flip, tint);
                return;
            }
        }
    }
}
