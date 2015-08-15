package com.coffeebland.cossinlette3.game.visual;

import org.jetbrains.annotations.NotNull;

import java.util.BitSet;

public abstract class ImageStripResolver implements Comparable<ImageStripResolver> {
    public final int priority;
    @NotNull public final ImageStrip imageStrip;

    public ImageStripResolver(int priority, @NotNull ImageStrip imageStrip) {
        this.priority = priority;
        this.imageStrip = imageStrip;
    }

    @Override public int compareTo(@NotNull ImageStripResolver compared) {
        return Integer.compare(compared.priority, priority);
    }

    public abstract boolean conditionsMet(@NotNull BitSet flags);

}
