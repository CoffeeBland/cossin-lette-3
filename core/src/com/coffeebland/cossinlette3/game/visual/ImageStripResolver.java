package com.coffeebland.cossinlette3.game.visual;

import com.coffeebland.cossinlette3.game.file.FlagResolverDef;
import org.jetbrains.annotations.NotNull;

import java.util.BitSet;
import java.util.List;

public abstract class ImageStripResolver implements Comparable<ImageStripResolver> {
    public final int priority;
    public final ImageStrip imageStrip;

    public ImageStripResolver(int priority, ImageStrip imageStrip) {
        this.priority = priority;
        this.imageStrip = imageStrip;
    }

    @Override public int compareTo(@NotNull ImageStripResolver compared) {
        return Integer.compare(compared.priority, priority);
    }

    public abstract boolean conditionsMet(@NotNull BitSet flags);

    public static class FlagResolver extends ImageStripResolver {

        List<Integer> flags;

        public FlagResolver(int priority, ImageStrip imageStrip, List<Integer> flags) {
            super(priority, imageStrip);
            this.flags = flags;
        }
        public FlagResolver(FlagResolverDef def) {
            this(def.priority, new ImageStrip(def.imageStripDef), def.flags);
        }

        @Override public boolean conditionsMet(@NotNull BitSet flags) {
            for (int flag : this.flags) {
                if (!flags.get(flag)) return false;
            }
            return true;
        }
    }
}
