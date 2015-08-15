package com.coffeebland.cossinlette3.game.visual;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.coffeebland.cossinlette3.game.file.CharsetDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;

public class FlagResolver extends ImageStripResolver {

    @Nullable int[][] flags;

    public FlagResolver(int priority, ImageStrip imageStrip, @Nullable int[][] flags) {
        super(priority, imageStrip);
        this.flags = flags;
    }
    public FlagResolver(@NotNull TextureAtlas atlas, int priority, CharsetDef def) {
        this(priority, def.getImageStrip(atlas), def.conditions);
    }

    @Override public boolean conditionsMet(@NotNull BitSet flags) {
        if (this.flags == null) return true;

        checkFlagSets: for (int[] flagSet : this.flags) {
            for (int flag: flagSet) {
                if (!flags.get(flag)) continue checkFlagSets;
            }
            return true;
        }
        return false;
    }
}
