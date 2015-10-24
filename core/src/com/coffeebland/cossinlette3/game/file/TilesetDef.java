package com.coffeebland.cossinlette3.game.file;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.coffeebland.cossinlette3.utils.Dst;
import com.coffeebland.cossinlette3.utils.NtN;
import com.coffeebland.cossinlette3.utils.Textures;

public class TilesetDef {
    public float tileSize;
    protected int tileSizePixels;
    public StillDef[] stills;
    public VariationDef[] variations;
    public AnimationDef[] animations;

    public TilesetDef() {}

    public int getTileSizePixels() {
        return tileSizePixels == 0 ? tileSizePixels = (int)Dst.getAsPixels(tileSize) : tileSizePixels;
    }
    public float getTileSizeMeters() {
        return tileSize;
    }

    public static class PartialDef {
        public String src;

        public PartialDef() {}

        @NtN public TextureRegion[][] getRegions(@NtN TextureAtlas atlas, int tileWidth, int tileHeight) {
            return Textures.get(atlas, src, tileWidth, tileHeight);
        }
    }
    public static class StillDef extends PartialDef {
        public StillDef() {}
    }
    public static class VariationDef extends PartialDef {
        public int tilesX, tilesY;

        public VariationDef() {}
    }
    public static class AnimationDef extends VariationDef {
        public float fps;

        public AnimationDef() {}
    }
}
