package com.coffeebland.cossinlette3.game.visual;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.coffeebland.cossinlette3.game.file.ImageStripDef;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImageStrip extends ImageSheet {
    protected final float fps;
    protected final List<OrientationFrame> frames;

    public ImageStrip(@NotNull String src, int frameWidth, int frameHeight, int decalX, int decalY, float fps, List<OrientationFrame> frames) {
        super(src, frameWidth, frameHeight, decalX, decalY);
        this.frames = frames;
        this.fps = fps;
    }
    public ImageStrip(ImageStripDef def) {
        super(def);
        fps = def.fps;
        frames = new ArrayList<>();
        frames.addAll(def.orientationFrameDefs.stream().map(OrientationFrame::new).collect(Collectors.toList()));
    }

    public void render(@NotNull SpriteBatch batch, float x, float y, int frameX, float orientation, float scale) {
        for (OrientationFrame frame : frames) {
            if (orientation >= frame.startAngle && orientation < frame.endAngle) {
                render(batch, x, y, frameX, frame.frameY, scale, frame.flip);
                return;
            }
        }
    }
    public void render(@NotNull SpriteBatch batch, float x, float y, int frameX, float orientation, float scale, @NotNull Color tint) {
        for (OrientationFrame frame : frames) {
            if (orientation >= frame.startAngle && orientation < frame.endAngle) {
                render(batch, x, y, frameX, frame.frameY, scale, frame.flip, tint);
                return;
            }
        }
    }
}
