package com.coffeebland.cossinlette3.game.visual;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.coffeebland.cossinlette3.game.file.ImageStripsDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.EnumSet;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ImageStrips {

    public enum NewStripFlags {
        KEEP_FRAME, KEEP_REMAINING;
    }

    @NotNull public final SortedSet<ImageStripResolver> resolvers = new TreeSet<>();
    @Nullable protected ImageStrip currentStrip;
    protected float fps, frameLength, durationRemaining;
    protected int frameX;

    public ImageStrips() { }
    public ImageStrips(ImageStripsDef def) {
        resolvers.addAll(def.resolverDefs.stream().map(ImageStripResolver.FlagResolver::new).collect(Collectors.toList()));
    }

    public void resolve(@NotNull BitSet flags, EnumSet<NewStripFlags> newStripFlags) {
        resolveStrip:
        {
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
    public void resolve(@NotNull BitSet flags) {
        resolve(flags, EnumSet.noneOf(NewStripFlags.class));
    }

    public void render(@NotNull SpriteBatch batch, float x, float y, float orientation, float scale) {
        if (currentStrip != null) {
            currentStrip.render(batch, x, y, frameX, orientation, scale);
        }
    }
    public void render(@NotNull SpriteBatch batch, float x, float y, float orientation, float scale, @NotNull Color color) {
        if (currentStrip != null) {
            currentStrip.render(batch, x, y, frameX, orientation, scale, color);
        }
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
