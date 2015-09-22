package com.coffeebland.cossinlette3.game.entity;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.game.file.TilesetDef;
import com.coffeebland.cossinlette3.utils.Dst;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class Tileset {

    protected float tileSize;
    protected int tileSizePixels;
    protected Regions[] stills;
    protected VariationRegions[] variations;
    protected AnimationRegions[] animations;

    public Tileset(@NotNull TextureAtlas atlas, @NotNull TilesetDef def) {
        this.tileSize = def.tileSize;
        this.tileSizePixels = (int) Dst.getAsPixels(tileSize);

        stills = Stream.of(def.stills)
                .map(sDef -> new Regions(sDef, tileSizePixels, atlas))
                .toArray(Regions[]::new);

        variations = Stream.of(def.variations)
                .map(vDef -> new VariationRegions(vDef, tileSizePixels, atlas))
                .toArray(VariationRegions[]::new);

        animations = Stream.of(def.animations)
                .map(aDef -> new AnimationRegions(aDef, tileSizePixels, atlas))
                .toArray(AnimationRegions[]::new);
    }

    public float getTileSizeMeters() { return tileSize; }
    public int getTileSizePixels() { return tileSizePixels; }

    public float metersToTile(float meters) {
        return meters / tileSize;
    }
    @NotNull public Vector2 metersToTile(@NotNull Vector2 meters) {
        return meters.scl(1f / tileSize);
    }
    public float tileToMeters(float tile) {
        return tile * tileSize;
    }
    @NotNull public Vector2 tileToMeters(@NotNull Vector2 tiles) {
        return tiles.scl(tileSize);
    }
    public float pixToTile(float pixels) {
        return pixels / tileSizePixels;
    }
    @NotNull public Vector2 pixToTile(@NotNull Vector2 pixels) {
        return pixels.scl(1f / tileSizePixels);
    }
    public float tileToPix(float tile) {
        return tile * tileSizePixels;
    }
    @NotNull public Vector2 tileToPix(Vector2 tiles) {
        return tiles.scl(tileSizePixels);
    }

    public Regions[] getStills() {
        return stills;
    }
    public VariationRegions[] getVariations() {
        return variations;
    }
    public AnimationRegions[] getAnimations() {
        return animations;
    }

    public static class Regions {
        protected TextureRegion[][] regions;
        protected int tilesX, tilesY;

        public Regions(@NotNull TilesetDef.PartialDef def, int tileSize, @NotNull TextureAtlas atlas) {
            this.regions = def.getRegions(atlas, tileSize, tileSize);
            this.tilesX = regions[0].length;
            this.tilesY = regions.length;
        }

        public TextureRegion[][] getRegions() { return regions; }
        public int getTilesX() { return tilesX; }
        public int getTilesY() { return tilesY; }
        public int getBlockCount() { return 1; }
    }
    public static class VariationRegions extends Regions {
        protected int frameCount;

        public VariationRegions(@NotNull TilesetDef.VariationDef def, int tileSize, @NotNull TextureAtlas atlas) {
            super(def, tileSize, atlas);
            this.tilesX = def.tilesX;
            this.tilesY = def.tilesY;
            this.frameCount = regions.length / def.tilesY;
        }

        public int getFrameCount() { return frameCount; }
        @Override public int getBlockCount() { return regions[0].length / getTilesX(); }
    }
    public static class AnimationRegions extends VariationRegions {
        protected float fps;

        public AnimationRegions(@NotNull TilesetDef.AnimationDef def, int tileSize, @NotNull TextureAtlas atlas) {
            super(def, tileSize, atlas);
            this.fps = def.fps;
        }
        public float getFps() { return fps; }

        public int getFrameOffset(float cumulatedSeconds) {
            return (int)((cumulatedSeconds * fps) % frameCount) * tilesY;
        }
    }
}
