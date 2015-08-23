package com.coffeebland.cossinlette3.editor.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.coffeebland.cossinlette3.game.entity.Tileset;
import com.coffeebland.cossinlette3.utils.Textures;
import com.coffeebland.cossinlette3.utils.Time;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class TileChooser extends Widget {

    public static final int WIDTH = 12 * (24 + 1) + 1;
    public static final Color TILE_BG = new Color(0, 0, 0, 0.2f);

    @NotNull protected Tileset tileset;
    protected int tilesX;
    protected float cumulatedSeconds = 0;
    protected long lastNano;

    @NotNull protected final List<TileBlock<?>> tileBlocks = new ArrayList<>();
    protected int selectedBlockIndex = 0,
            selectedTileX = 0, selectedTileY = 0,
            selectedWidth = 1, selectedHeight = 1;

    public TileChooser(@NotNull Tileset tileset) {
        this.tileset = tileset;
        tilesX = Math.max(WIDTH / (tileset.getTileSizePixels() + 1), 1);

        int tileDrawY = 0;
        tileDrawY = addBlocks(tileset.getAnimations(), tileDrawY, AnimationBlock::new, true);
        tileDrawY = addBlocks(tileset.getVariations(), tileDrawY, VariationBlock::new, false);
        tileDrawY = addBlocks(tileset.getVariations(), tileDrawY, StillBlock::new, false);
        tileDrawY = addBlocks(tileset.getStills(), tileDrawY, StillBlock::new, true);

        invalidateHierarchy();
        addListener(new ClickListener() {

            int downTileX = -1, downTileY = -1;

            @Override public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
                drag: if (isPressed()) {
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

            @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean parent = super.touchDown(event, x, y, pointer, button);
                down: if (parent) {
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
            (T[] tileRegions, int tileDrawY, BiFunction<T, Integer, TileBlock<T>> creator, boolean preventSizeOverlaps) {
        int tileDrawX = 0;
        int remainderJump = 0;
        for (T tileRegion : tileRegions) {
            if (preventSizeOverlaps) tileDrawX = 0;
            for (int i = 0; i < tileRegion.getBlockCount(); i++) {
                TileBlock<T> block = creator.apply(tileRegion, i);
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

    public abstract class TileBlock<T extends Tileset.Regions> {

        protected int tileDrawX, tileDrawY;
        protected @NotNull T regions;
        protected int block;

        public TileBlock(@NotNull T regions, int block) {
            this.regions = regions;
            this.block = block;
        }

        public void drawBackground(@NotNull Batch batch) {
            Textures.drawFilledRect(
                    batch, TILE_BG,
                    (int) getDrawX(tileDrawX), (int) getDrawY(tileDrawY) - (getTilesY() - 1) * (tileset.getTileSizePixels() + 1),
                    getTilesX() * (tileset.getTileSizePixels() + 1) - 1, getTilesY() * (tileset.getTileSizePixels() + 1) - 1
            );
        }
        public abstract void draw(@NotNull Batch batch);
        public void drawSelection(@NotNull Batch batch) {
            Textures.drawRect(
                    batch, Color.WHITE,
                    (int) getDrawX(tileDrawX + selectedTileX), (int) getDrawY(tileDrawY + selectedTileY),
                    selectedWidth * (tileset.getTileSizePixels() + 1) - 1, selectedHeight * (tileset.getTileSizePixels() + 1) - 1,
                    1
            );

            Textures.drawRect(
                    batch, Color.BLACK,
                    (int) getDrawX(tileDrawX + selectedTileX) - 1, (int) getDrawY(tileDrawY + selectedTileY) - 1,
                    selectedWidth * (tileset.getTileSizePixels() + 1) + 1, selectedHeight * (tileset.getTileSizePixels() + 1) + 1,
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

        public StillBlock(@NotNull T regions, int block) {
            super(regions, block);
        }

        @SuppressWarnings("UnnecessaryLocalVariable")
        @Override
        public void draw(@NotNull Batch batch) {
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
    }
    public class VariationBlock<T extends Tileset.VariationRegions> extends TileBlock<T> {

        public VariationBlock(@NotNull T vars, int block) {
            super(vars, block);
        }

        public int getFrameOffset(float cumulatedSeconds) {
            return (int)((cumulatedSeconds) % regions.getFrameCount()) * getTilesY();
        }
        @Override public void draw(@NotNull Batch batch) {
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

        public AnimationBlock(@NotNull T vars, int block) {
            super(vars, block);
        }

        @Override public int getFrameOffset(float cumulatedSeconds) {
            return regions.getFrameOffset(cumulatedSeconds);
        }
    }
}
