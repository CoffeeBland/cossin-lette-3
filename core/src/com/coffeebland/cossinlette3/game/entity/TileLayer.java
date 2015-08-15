package com.coffeebland.cossinlette3.game.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.coffeebland.cossinlette3.game.GameCamera;
import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.game.file.TileLayerDef;
import org.jetbrains.annotations.NotNull;

import static com.coffeebland.cossinlette3.game.file.TileLayerDef.*;
import static com.coffeebland.cossinlette3.utils.Const.METERS_PER_PIXEL;

public class TileLayer extends Actor {

    public static final int
            TYPE_STILL = 0,
            TYPE_VAR = 1,
            TYPE_ANIM = 2;

    @NotNull protected long[][][] tiles;
    @NotNull protected Tileset tileset;
    
    protected float cumulatedSeconds = 0;
    @NotNull protected TileRow[] rows;

    public TileLayer(@NotNull TileLayerDef def, @NotNull Tileset tileset) {
        super(def);

        this.tiles = def.tiles;
        this.tileset = tileset;
        rows = new TileRow[def.tiles.length];
        for (int rowI = 0; rowI < rows.length; rowI++) {
            rows[rowI] = new TileRow(rowI);
        }
    }

    @NotNull public Tileset getTileset() {
        return tileset;
    }

    @Override public void addToWorld(@NotNull GameWorld world) {
        super.addToWorld(world);
        for (TileRow row : rows) {
            row.addToWorld(world);
        }
    }

    @Override public void removeFromWorld() {
        super.removeFromWorld();
        for (TileRow row : rows) {
            row.removeFromWorld();
        }
    }

    @Override public void update(float delta) {
        cumulatedSeconds = (cumulatedSeconds + delta / 1000) % 1000;
    }

    public class TileRow extends Actor {

        protected int row;

        public TileRow(int row) {
            super(-row * tileset.getTileSizeMeters() + TileLayer.this.priority);
            this.row = row;
        }

        @SuppressWarnings("PointlessBitwiseExpression")
        @Override
        public void render(@NotNull SpriteBatch batch, @NotNull GameCamera camera) {
            super.render(batch, camera);

            long[][] rowTiles = tiles[row];

            int y = row;
            int tilePixels = tileset.getTileSizePixels();
            for (int x = 0; x < rowTiles.length; x++) {
                long[] tiles = rowTiles[x];

                for (long tile : tiles) {
                    int type = (int) ((tile & TYPE_MASK) >> TYPE_MASK_SHIFT);
                    int typeIndex = (int) ((tile & INDEX_MASK) >> INDEX_MASK_SHIFT);
                    int tileX = (int) ((tile & TILE_X_MASK) >> TILE_X_MASK_SHIFT);
                    int tileY = (int) ((tile & TILE_Y_MASK) >> TILE_Y_MASK_SHIFT);

                    TextureRegion[][] regions;
                    switch (type) {
                        case TYPE_STILL:
                            regions = tileset.getStills(typeIndex);
                            break;
                        case TYPE_VAR:
                            regions = tileset.getVariations(typeIndex);
                            break;
                        case TYPE_ANIM:
                            Tileset.AnimationRegions aRegions = tileset.getAnimations(typeIndex);
                            regions = aRegions.regions;
                            tileY += aRegions.getFrameOffset(cumulatedSeconds);
                            break;
                        default:
                            throw new RuntimeException("Unexpected tile type");
                    }

                    batch.draw(regions[tileY][tileX],
                            -camera.getPos().x / METERS_PER_PIXEL + x * tilePixels,
                            -camera.getPos().y / METERS_PER_PIXEL + y * tilePixels,
                            tileX * tilePixels,
                            tileY * tilePixels,
                            tilePixels,
                            tilePixels,
                            1, 1, 0
                    );
                }
            }
        }
    }
}
