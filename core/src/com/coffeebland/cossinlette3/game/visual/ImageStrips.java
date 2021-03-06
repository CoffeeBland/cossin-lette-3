package com.coffeebland.cossinlette3.game.visual;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.utils.N;
import com.coffeebland.cossinlette3.utils.NtN;

import java.util.BitSet;
import java.util.EnumSet;
import java.util.SortedSet;
import java.util.TreeSet;

public class ImageStrips {

    public enum NewStripFlags {
        KEEP_FRAME, KEEP_REMAINING
    }

    @NtN public final SortedSet<ImageStripResolver> resolvers = new TreeSet<>();
    @N protected ImageStrip currentStrip;
    protected float fps, frameLength, durationRemaining;
    protected int frameX;

    public ImageStrips() { }

    public void resolve(@NtN BitSet flags, EnumSet<NewStripFlags> newStripFlags) {
        resolveStrip: {
            for (ImageStripResolver resolver : resolvers) {
                if (resolver.conditionsMet(flags)) {
                    currentStrip = resolver.imageStrip;
                    break resolveStrip;
                }
            }

            // Could not resolve strip
            currentStrip = null;
            return;
        }

        this.fps = currentStrip.fps;
        this.frameLength = 1000 / fps;
        if (!newStripFlags.contains(NewStripFlags.KEEP_FRAME)) frameX = 0;
        else frameX %= currentStrip.framesX;
        if (!newStripFlags.contains(NewStripFlags.KEEP_REMAINING)) durationRemaining = frameLength;
    }
    public void resolve(@NtN BitSet flags) {
        resolve(flags, EnumSet.noneOf(NewStripFlags.class));
    }

    public void render(@NtN Batch batch, float x, float y, float orientation, float scale) {
        if (currentStrip != null) {
            currentStrip.render(batch, x, y, frameX, orientation, scale);
        }
    }
    public void render(@NtN Batch batch, Vector2 pos, float orientation, float scale) {
        render(batch, pos.x, pos.y, orientation, scale);
    }
    public void render(@NtN Batch batch, float x, float y, float orientation, float scale, @NtN Color color) {
        if (currentStrip != null) {
            currentStrip.render(batch, x, y, frameX, orientation, scale, color);
        }
    }
    public void render(@NtN Batch batch, Vector2 pos, float orientation, float scale, @NtN Color color) {
        render(batch, pos.x, pos.y, orientation, scale, color);
    }

    public void update(float delta) {
        if (currentStrip != null) {
            durationRemaining -= delta;
            while (durationRemaining < 0) {
                durationRemaining += frameLength;
                frameX = (frameX + 1) % currentStrip.framesX;
            }
        }
    }
}
