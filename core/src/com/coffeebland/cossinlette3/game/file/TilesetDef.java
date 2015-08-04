package com.coffeebland.cossinlette3.game.file;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.jetbrains.annotations.NotNull;

public class TilesetDef {
    public int tileSize;
    public StillDef[] stills;
    public VariationDef[] variations;
    public AnimationDef[] animations;

    public TilesetDef() {}

    public static class PartialDef {
        public String src;
        protected TextureAtlas.AtlasRegion region;

        public PartialDef() {}

        @NotNull public TextureAtlas.AtlasRegion getRegion(TextureAtlas atlas) {
            return region == null ? region = atlas.findRegion(src) : region;
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

        public int getFrameOffset(TextureAtlas.AtlasRegion region, TilesetDef tileset, float cumulatedSeconds) {
            int frameCount = (region.getRegionHeight() / tileset.tileSize) / tilesY;
            int frame = (int)((cumulatedSeconds * fps) % frameCount);
            return frame * tilesY;
        }

        public AnimationDef() {}
    }
}
