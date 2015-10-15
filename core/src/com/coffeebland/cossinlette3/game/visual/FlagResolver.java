package com.coffeebland.cossinlette3.game.visual;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.coffeebland.cossinlette3.game.file.CharsetDef;
import com.coffeebland.cossinlette3.utils.N;
import com.coffeebland.cossinlette3.utils.NtN;

import java.util.BitSet;

public class FlagResolver extends ImageStripResolver {

    @N int[][] flags;

    public FlagResolver(int priority, ImageStrip imageStrip, @N int[][] flags) {
        super(priority, imageStrip);
        this.flags = flags;
    }
    public FlagResolver(@NtN TextureAtlas atlas, int priority, CharsetDef def) {
        this(priority, def.getImageStrip(atlas), def.conditions);
    }

    @Override public boolean conditionsMet(@NtN BitSet flags) {
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
