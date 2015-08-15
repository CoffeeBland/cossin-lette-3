package com.coffeebland.cossinlette3.game.entity;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.coffeebland.cossinlette3.game.file.TilesetDef;
import com.coffeebland.cossinlette3.utils.Dst;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class Tileset {

    protected float tileSize;
    protected int tileSizePixels;
    protected TextureRegion[][][] stills;
    protected TextureRegion[][][] variations;
    protected AnimationRegions[] animations;

    public Tileset(@NotNull TextureAtlas atlas, @NotNull TilesetDef def) {
        this.tileSize = def.tileSize;
        this.tileSizePixels = (int) Dst.getAsPixels(tileSize);

        stills = Stream.of(def.stills)
                .map(sDef -> sDef.getRegions(atlas, tileSizePixels, tileSizePixels))
                .toArray(TextureRegion[][][]::new);

        variations = Stream.of(def.variations)
                .map(vDef -> vDef.getRegions(atlas, tileSizePixels, tileSizePixels))
                .toArray(TextureRegion[][][]::new);

        animations = Stream.of(def.animations)
                .map(aDef -> new AnimationRegions(aDef, aDef.getRegions(atlas, tileSizePixels, tileSizePixels)))
                .toArray(AnimationRegions[]::new);
    }

    public float getTileSizeMeters() { return tileSize; }
    public int getTileSizePixels() { return tileSizePixels; }

    public TextureRegion[][] getStills(int typeIndex) {
        return stills[typeIndex];
    }
    public TextureRegion[][] getVariations(int typeIndex) {
        return variations[typeIndex];
    }
    public AnimationRegions getAnimations(int typeIndex) {
        return animations[typeIndex];
    }

    public static class AnimationRegions {
        protected TextureRegion[][] regions;
        protected float fps;
        protected int frameCount;
        protected int tilesY;

        public AnimationRegions(TilesetDef.AnimationDef def, TextureRegion[][] regions) {
            this.regions = regions;
            this.fps = def.fps;
            this.tilesY = def.tilesY;
            this.frameCount = regions.length / def.tilesY;
        }

        public int getFrameOffset(float cumulatedSeconds) {
            return (int)((cumulatedSeconds * fps) % frameCount) * tilesY;
        }
    }
}
