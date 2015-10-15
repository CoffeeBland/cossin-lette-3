
package com.coffeebland.cossinlette3.editor.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.coffeebland.cossinlette3.editor.tools.TileBlockSource;
import com.coffeebland.cossinlette3.editor.tools.TileSource;
import com.coffeebland.cossinlette3.game.entity.TileLayer;
import com.coffeebland.cossinlette3.game.entity.Tileset;
import com.coffeebland.cossinlette3.utils.func.QuadFunction;
import com.coffeebland.cossinlette3.utils.Textures;
import com.coffeebland.cossinlette3.utils.Time;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TileChooser extends Widget implements TileSource {

    public static final int WIDTH = 12 * (24 + 1) + 1;
    public static final Color TILE_BG = new Color(0, 0, 0, 0.2f);

    @NtN protected Tileset tileset;
    protected int tilesX;
    protected float cumulatedSeconds = 0;
    protected long lastNano;

    @NtN protected final List<TileBlock<?>> tileBlocks = new ArrayList<>();
    protected int selectedBlockIndex = 0,
            selectedTileX = 0, selectedTileY = 0,
            selectedWidth = 1, selectedHeight = 1;

    public TileChooser(@NtN Tileset tileset) {
        this.tileset = tileset;
        tilesX = Math.max(WIDTH / (tileset.getTileSizePixels() + 1), 1);

        int tileDrawY = 0;
        tileDrawY = addBlocks(tileset.getAnimations(), TileLayer.TYPE_ANIM, tileDrawY, AnimationBlock::new, true);
        tileDrawY = addBlocks(tileset.getVariations(), TileLayer.TYPE_VAR, tileDrawY, VariationBlock::new, false);
        tileDrawY = addBlocks(tileset.getVariations(), TileLayer.TYPE_VAR, tileDrawY, StillBlock::new, false);
        tileDrawY = addBlocks(tileset.getStills(), TileLayer.TYPE_STILL, tileDrawY, StillBlock::new, true);

        invalidateHierarchy();
        addListener(new ClickListener() {

            int downTileX = -1, downTileY = -1;

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
                drag:
                if (isPressed()) {
                    if (downTileX == -1 || downTileY == -1) break drag;
                    TileBlock<?> current = tileBlocks.get(selectedBlockIndex);
                    int tileX = Math.min(Math.max(getTileX(x), current.tileDrawX), current.tileDrawX + current.getTilesX() - 1);
                    int tileY = Math.min(Math.max(getTileY(y), current.tileDrawY), current.tileDrawY + current.getTilesY() - 1);

                    if (tileX < downTileX) {
                        selectedTileX = tileX - current.tileDrawX;
                        selectedWidth = downTileX - tileX + 1;
                    } else {
                        selectedTileX = downTileX - current.tileDrawX;
                        selectedWidth = tileX - downTileX + 1;
                    }

                    if (tileY < downTileY) {
                        selectedTileY = downTileY - current.tileDrawY;
                        selectedHeight = downTileY - tileY + 1;
                    } else {
                        selectedTileY = tileY - current.tileDrawY;
                        selectedHeight = tileY - downTileY + 1;
                    }
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean parent = super.touchDown(event, x, y, pointer, button);
                down:
                if (parent) {
                    downTileX = getTileX(x);
                    downTileY = getTileY(y);
                    Optional<TileBlock<?>> oBlock = tileBlocks.stream().filter(block -> block.isInside(downTileX, downTileY)).findAny();
                    if (!oBlock.isPresent()) {
                        downTileX = downTileY = -1;
                        break down;
                    }
                    TileBlock block = oBlock.get();
                    selectedBlockIndex = tileBlocks.indexOf(block);
                    selectedTileX = downTileX - block.tileDrawX;
                    selectedTileY = downTileY - block.tileDrawY;
                    selectedWidth = 1;
                    selectedHeight = 1;
                }
                return parent;
            }
        });
    }

    public void addBlock(TileBlock block, int tileX, int minTileY) {
        block.tileDrawX = tileX;
        block.tileDrawY = minTileY;

        while (block.collides()) {
            block.tileDrawY++;
        }

        tileBlocks.add(block);
    }
    public <T extends Tileset.Regions> int addBlocks
            (T[] tileRegions, int type, int tileDrawY, QuadFunction<T, Integer, Integer, Integer, TileBlock<T>> creator, boolean preventSizeOverlaps) {
        int tileDrawX = 0;
        int remainderJump = 0;
        for (int index = 0; index < tileRegions.length; index++) {
            T tileRegion = tileRegions[index];
            if (preventSizeOverlaps) tileDrawX = 0;
            for (int blockIndex = 0; blockIndex < tileRegion.getBlockCount(); blockIndex++) {
                TileBlock<T> block = creator.apply(tileRegion, blockIndex, type, index);
                if (tileDrawX + block.getTilesX() > tilesX) tileDrawX = 0;
                addBlock(block, tileDrawX, tileDrawY);
                tileDrawX += block.getTilesX();
                tileDrawY = Math.max(tileDrawY, block.tileDrawY);
            }
            if (preventSizeOverlaps) tileDrawY += tileRegion.getTilesY();
            else remainderJump = Math.max(tileRegion.getTilesY(), remainderJump);
        }

        return tileDrawY + remainderJump;
    }

    @Override public float getPrefWidth() {
        return WIDTH;
    }
    @Override public float getPrefHeight() {
        Optional<? extends TileBlock> oBlock = tileBlocks.stream()
                .sorted((lhs, rhs) -> (rhs.tileDrawY + rhs.getTilesY()) - (lhs.tileDrawY + lhs.getTilesY()))
                .findFirst();
        if (!oBlock.isPresent()) return super.getPrefHeight();
        TileBlock block = oBlock.get();
        return (block.tileDrawY + block.getTilesY()) * (tileset.getTileSizePixels() + 1) + 1;
    }

    @Override @NtN public Tileset getTileset() { return tileset; }
    @Override public TileBlockSource getTileBlockSource() { return tileBlocks.get(selectedBlockIndex); }
    @Override public int getSelectedTileX() {
        TileBlock tileBlock = tileBlocks.get(selectedBlockIndex);
        return selectedTileX + tileBlock.block * tileBlock.getTilesX();
    }
    @Override public int getSelectedTileY() { return selectedTileY; }
    @Override public int getSelectedWidth() { return selectedWidth; }
    @Override public int getSelectedHeight() { return selectedHeight; }

    protected float getDrawX(int tileDrawX) {
        return getX() + tileDrawX * (tileset.getTileSizePixels() + 1) + 1;
    }
    protected int getTileX(float drawX) {
        return (int)(drawX - getX() - 1) / (tileset.getTileSizePixels() + 1);
    }
    protected float getDrawY(int tileDrawY) {
        return getY() + getHeight() - ((tileDrawY + 1) * (tileset.getTileSizePixels() + 1) + 1) + 1;
    }
    protected int getTileY(float drawY) {
        return (int)(getY() + getHeight() - drawY - 1)  / (tileset.getTileSizePixels() + 1);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        long nano = System.nanoTime();
        float delta = Time.nanoToMillis(nano - lastNano);
        lastNano = nano;
        if (lastNano == delta) return;
        cumulatedSeconds = (cumulatedSeconds + delta / 1000) % 1000;

        // Since we want to batch drawing with the tileset, we do the post and the draw in different loops
        for (TileBlock block : tileBlocks) block.drawBackground(batch);
        for (TileBlock block : tileBlocks) block.draw(batch);
        tileBlocks.get(selectedBlockIndex).drawSelection(batch);
    }

    public abstract class TileBlock<T extends Tileset.Regions> implements TileBlockSource {

        protected int tileDrawX, tileDrawY;
        protected @NtN T regions;
        protected int block;
        protected int type, typeIndex;

        public TileBlock(@NtN T regions, int block, int type, int typeIndex) {
            this.regions = regions;
            this.block = block;
            this.type = type;
            this.typeIndex = typeIndex;
        }

        public void drawBackground(@NtN Batch batch) {
            Textures.drawFilledRect(
                    batch, TILE_BG,
                    getDrawX(tileDrawX),
                    getDrawY(tileDrawY) - (getTilesY() - 1) * (tileset.getTileSizePixels() + 1),
                    getTilesX() * (tileset.getTileSizePixels() + 1) - 1,
                    getTilesY() * (tileset.getTileSizePixels() + 1) - 1
            );
        }
        public abstract void draw(@NtN Batch batch);
        public void drawSelection(@NtN Batch batch) {
            Textures.drawRect(
                    batch, Color.WHITE,
                    getDrawX(tileDrawX + selectedTileX),
                    getDrawY(tileDrawY + selectedTileY),
                    selectedWidth * (tileset.getTileSizePixels() + 1) - 1,
                    selectedHeight * (tileset.getTileSizePixels() + 1) - 1,
                    1
            );

            Textures.drawRect(
                    batch, Color.BLACK,
                    getDrawX(tileDrawX + selectedTileX) - 1,
                    getDrawY(tileDrawY + selectedTileY) - 1,
                    selectedWidth * (tileset.getTileSizePixels() + 1) + 1,
                    selectedHeight * (tileset.getTileSizePixels() + 1) + 1,
                    1
            );
        }
        public int getTilesX() {
            return Math.min(regions.getTilesX(), tilesX);
        }
        public int getTilesY() {
            int xTileBatches = (int) Math.ceil(regions.getTilesX() / (float)getTilesX());
            return regions.getTilesY() * xTileBatches;
        }
        @Override public int getType() { return type; }
        @Override public int getTypeIndex() { return typeIndex; }

        public boolean collides() {
            return tileBlocks.stream().anyMatch((tb) ->
                            tb.tileDrawX < tileDrawX + getTilesX() &&
                                    tb.tileDrawX + tb.getTilesX() > tileDrawX &&
                                    tb.tileDrawY < tileDrawY + getTilesY() &&
                                    tb.tileDrawY + tb.getTilesY() > tileDrawY
            );
        }
        public boolean isInside(int tileX, int tileY) {
            return tileX >= tileDrawX
                    && tileX < tileDrawX + getTilesX()
                    && tileY >= tileDrawY
                    && tileY < tileDrawY + getTilesY();
        }
    }
    public class StillBlock<T extends Tileset.Regions> extends TileBlock<T> {

        public StillBlock(@NtN T regions, int block, int type, int typeIndex) {
            super(regions, block, type, typeIndex);
        }

        @SuppressWarnings("UnnecessaryLocalVariable")
        @Override
        public void draw(@NtN Batch batch) {
            int tilePixels = tileset.getTileSizePixels();
            int varsTilesX = getTilesX();
            int varsTilesY = getTilesY();
            int xTileBatches = (int)Math.ceil(varsTilesX / (float)tilesX);

            for (int xTileBatch = 0; xTileBatch < xTileBatches; xTileBatch++) {
                // Calculate the remainder x tiles
                int tilesX = Math.min(TileChooser.this.tilesX, varsTilesX - xTileBatch * TileChooser.this.tilesX);
                int tilesY = varsTilesY;
                for (int tileDrawX = 0; tileDrawX < tilesX; tileDrawX++) {
                    for (int tileDrawY = 0; tileDrawY < tilesY; tileDrawY++) {
                        int tileX = tileDrawX + xTileBatch * TileChooser.this.tilesX;
                        int tileY = tileDrawY;

                        batch.draw(regions.getRegions()[tileY][tileX + block * regions.getTilesX()],
                                getDrawX(this.tileDrawX + tileDrawX),
                                getDrawY(this.tileDrawY + tileDrawY),
                                (tileX + (block * regions.getTilesX())) * tilePixels,
                                tileY * tilePixels,
                                tilePixels,
                                tilePixels,
                                1, 1, 0

                        );
                    }
                }
            }
        }

        @Override public int getTilesY() {
            return (int)(super.getTilesY() * (regions.getRegions().length / (float)regions.getTilesY()));
        }

        @Override public int getTileOffset(float offset) { return 0; }
    }
    public class VariationBlock<T extends Tileset.VariationRegions> extends TileBlock<T> {

        public VariationBlock(@NtN T vars, int block, int type, int typeIndex) {
            super(vars, block, type, typeIndex);
        }

        @Override public int getTileOffset(float offset) {
            return getFrameOffset(offset * regions.getFrameCount());
        }
        public int getFrameOffset(float cumulatedSeconds) {
            return (int)((cumulatedSeconds) % regions.getFrameCount()) * getTilesY();
        }
        @Override public void draw(@NtN Batch batch) {
            int tilePixels = tileset.getTileSizePixels();
            int offset = getFrameOffset(cumulatedSeconds);

            for (int tileX = 0; tileX < regions.getTilesX(); tileX++) {
                for (int tileY = 0; tileY < regions.getTilesY(); tileY++) {
                    batch.draw(regions.getRegions()[tileY + offset][tileX + block * regions.getTilesX()],
                            getDrawX(tileDrawX + tileX),
                            getDrawY(tileDrawY + tileY),
                            (tileX + (block * regions.getTilesX())) * tilePixels,
                            tileY * tilePixels,
                            tilePixels,
                            tilePixels,
                            1, 1, 0

                    );
                }
            }
        }
    }
    public class AnimationBlock<T extends Tileset.AnimationRegions> extends VariationBlock<T> {

        public AnimationBlock(@NtN T vars, int block, int type, int typeIndex) {
            super(vars, block, type, typeIndex);
        }

        @Override public int getTileOffset(float offset) { return 0; }
        @Override public int getFrameOffset(float cumulatedSeconds) {
            return regions.getFrameOffset(cumulatedSeconds);
        }
    }
}
