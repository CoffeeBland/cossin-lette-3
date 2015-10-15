package com.coffeebland.cossinlette3.game.file;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.coffeebland.cossinlette3.game.visual.ImageStrip;
import com.coffeebland.cossinlette3.game.visual.OrientationFrame;
import com.coffeebland.cossinlette3.utils.N;
import com.coffeebland.cossinlette3.utils.NtN;

import java.util.ArrayList;
import java.util.List;

public class CharsetDef {

    public static final int
            FLAG_WALKING = 0;

    public String src;
    public int width;
    public int height;
    public int decalX;
    public int decalY;
    public float fps;
    public int[] orientations;
    protected List<OrientationFrame> frames;
    @N public int[][] conditions;
    protected ImageStrip imageStrip;

    public CharsetDef() {}

    public List<OrientationFrame> getOrientationFrames() {
        if (frames != null) return frames;

        frames = new ArrayList<>();

        for (int i = 0; i < orientations.length; i += 4) {

            int frame = orientations[i];
            boolean flip = orientations[i + 1] == 1;
            float startAngle = (float)(orientations[i + 2] / 8.0 * Math.PI);
            float endAngle = (float)(orientations[i + 3] / 8.0 * Math.PI);

            frames.add(new OrientationFrame(frame, flip, startAngle, endAngle));
        }

        return frames;
    }
    @NtN public ImageStrip getImageStrip(@NtN TextureAtlas atlas) {
        if (imageStrip != null) return imageStrip;

        return new ImageStrip(
                atlas, src,
                width, height,
                decalX, decalY,
                fps, getOrientationFrames()
        );
    }
}
