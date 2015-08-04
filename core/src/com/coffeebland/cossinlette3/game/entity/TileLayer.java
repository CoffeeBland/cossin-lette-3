package com.coffeebland.cossinlette3.game.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.coffeebland.cossinlette3.game.GameCamera;
import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.game.file.TileLayerDef;
import com.coffeebland.cossinlette3.game.file.TilesetDef;
import org.jetbrains.annotations.NotNull;

import static com.coffeebland.cossinlette3.game.file.TileLayerDef.*;
import static com.coffeebland.cossinlette3.utils.Const.METERS_PER_PIXEL;

public class TileLayer extends Actor {

    public static final int
            TYPE_STILL = 0,
            TYPE_VAR = 1,
            TYPE_ANIM = 2;

    @NotNull protected TileLayerDef def;
    @NotNull protected TilesetDef tileset;
    @NotNull protected TextureAtlas atlas;
    
    protected float cumulatedSeconds = 0;

    public TileLayer(@NotNull TileLayerDef def, @NotNull TilesetDef tileset, @NotNull TextureAtlas atlas) {
        super(def);

        this.def = def;
        this.tileset = tileset;
        this.atlas = atlas;
    }

    @Override public void addToWorld(@NotNull GameWorld world) {
        super.addToWorld(world);
    }

    @Override public void removeFromWorld() {
        super.removeFromWorld();
    }

    public int getFrameOffset(int frameCount, int fps) {
        return (int)((cumulatedSeconds * fps) % frameCount) + 1;
    }

    @Override public void update(float delta) {
        cumulatedSeconds = (cumulatedSeconds + delta / 1000) % 1000;
    }

    public class TileRow extends Actor {

        protected int row;

        public TileRow(int row) {
            super(--row);
            this.row = row;
        }

        @SuppressWarnings("PointlessBitwiseExpression")
        @Override
        public void render(@NotNull SpriteBatch batch, @NotNull GameCamera camera) {
            super.render(batch, camera);

            long[][] rowTiles = def.tiles[row];

            int y = row;
            for (int x = 0, tilesX = rowTiles.length; x < tilesX; x++) {
                long[] tiles = rowTiles[x];

                for (int i = 0; i < tiles.length; i++) {
                    long tile = tiles[i];

                    int type = (int)((tile & TYPE_MASK) >> TYPE_MASK_SHIFT);
                    int typeIndex = (int)((tile & INDEX_MASK) >> INDEX_MASK_SHIFT);
                    int tileX = (int)((tile & TILE_X_MASK) >> TILE_X_MASK_SHIFT);
                    int tileY = (int)((tile & TILE_Y_MASK) >> TILE_Y_MASK_SHIFT);

                    TextureAtlas.AtlasRegion region;
                    switch (type) {
                        case TYPE_STILL:
                            region = tileset.stills[typeIndex].getRegion(atlas);
                            break;
                        case TYPE_VAR:
                            region = tileset.variations[typeIndex].getRegion(atlas);
                            break;
                        case TYPE_ANIM:
                            region = tileset.animations[typeIndex].getRegion(atlas);
                            tileY +=;
                            break;
                        default:
                            throw new RuntimeException("Unexpected tile type");
                    }

                    batch.draw(region,
                            -camera.getPos().x / METERS_PER_PIXEL + x * tileset.tileSize,
                            -camera.getPos().y / METERS_PER_PIXEL + y * tileset.tileSize,
                            tileX * tileset.tileSize,
                            tileY * tileset.tileSize,
                            tileset.tileSize,
                            tileset.tileSize,
                            1, 1, 0
                    );
                }
            }
        }
    }
}
