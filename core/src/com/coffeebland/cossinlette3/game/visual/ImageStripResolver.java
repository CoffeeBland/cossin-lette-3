package com.coffeebland.cossinlette3.game.visual;

import com.coffeebland.cossinlette3.utils.NtN;

import java.util.BitSet;

public abstract class ImageStripResolver implements Comparable<ImageStripResolver> {
    public final int priority;
    @NtN public final ImageStrip imageStrip;

    public ImageStripResolver(int priority, @NtN ImageStrip imageStrip) {
        this.priority = priority;
        this.imageStrip = imageStrip;
    }

    @Override public int compareTo(@NtN ImageStripResolver compared) {
        return Integer.compare(compared.priority, priority);
    }

    public abstract boolean conditionsMet(@NtN BitSet flags);

}
