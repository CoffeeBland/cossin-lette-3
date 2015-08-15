package com.coffeebland.cossinlette3.editor.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.coffeebland.cossinlette3.game.entity.TileLayer;
import org.jetbrains.annotations.Nullable;

public class TileChooser extends Widget {

    public static final int WIDTH = 12 * (24 + 1);

    @Nullable protected TileLayer tileLayer;
    protected int tilesX;
    protected int selectedTileX, selectedTileY;

    public TileChooser() {
        addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (tileLayer != null) {
                    /*int tileSize = getTileSize();
                    int tileX = (int) (x / (tileSize + 1));
                    int tileY = (int) ((getHeight() - y) / (tileSize + 1));

                    int[][] animations = tileLayer.getAnimations();
                    int animRows = (int)Math.ceil(animations.length / (float)tilesX);
                    if (tileY < animRows) {
                        int tile = tileX + tileY * tilesX;
                        if (tile < animations.length) {
                            TileChooser.this.selectedTileX = -1;
                            TileChooser.this.selectedTileY = tile;
                        }

                    } else {
                        tileY -= animRows;

                        int selectedTileY = tileY % tileLayer.getTextureTilesY();
                        int xTileBatch = (tileY / tileLayer.getTextureTilesY());
                        int selectedTileX = tileX + xTileBatch * tilesX;
                        if (selectedTileX < tileLayer.getTextureTilesX() && selectedTileY < tileLayer.getTextureTilesY()) {
                            TileChooser.this.selectedTileX = selectedTileX;
                            TileChooser.this.selectedTileY = selectedTileY;
                        }
                    }*/
                }
            }
        });
    }

    public int getTileSize() {
        return tileLayer != null ? tileLayer.getTileset().getTileSizePixels() : 24;
    }
    @Override public float getPrefWidth() {
        return WIDTH;
    }
    @Override public float getPrefHeight() {
        if (tileLayer != null) {
            /*int xTileBatches = (int) Math.ceil(tileLayer.getTextureTilesX() / (float)tilesX);
            int animRows = (int)Math.ceil(tileLayer.getAnimations().length / (float)tilesX);
            return (animRows + xTileBatches * tileLayer.getTextureTilesY()) * (getTileSize() + 1) - 1;*/
        }
        return super.getPrefHeight();
    }

    public void setTileLayer(@Nullable TileLayer tileLayer) {
        this.tileLayer = tileLayer;
        this.selectedTileX = 0;
        this.selectedTileY = 0;
        if (tileLayer != null) {
            tilesX = WIDTH / (getTileSize() + 1);
        }
        invalidateHierarchy();
    }

    public int getSelectedTileX() { return selectedTileX; }
    public int getSelectedTileY() { return selectedTileY; }

    /*@SuppressWarnings("UnnecessaryLocalVariable")
    public void performDrawFor(Drawer drawer) {
        if (tileLayer != null && tileLayer.getTexture() != null) {

            // First off, consider the animations
            int[][] animations = tileLayer.getAnimations();
            int tileSize = getTileSize();
            for (int animI = 0; animI < animations.length; animI++) {
                int tileX = animI % tilesX;
                int tileY = animI / tilesX;

                int drawX = (int)getX() + tileX * (tileSize + 1);
                int drawY = (int)getY() + (int)(getHeight() + 1 -
                        (tileY + 1) * (tileSize + 1)
                );

                drawer.draw(drawX, drawY, -1, animI);
            }

            // Render the regular tiles
            int textTilesX = tileLayer.getTextureTilesX();
            int textTilesY = tileLayer.getTextureTilesY();
            int animRows = (int)Math.ceil(animations.length / (float)tilesX);

            int xTileBatches = (int)Math.ceil(textTilesX / (float)tilesX);
            for (int xTileBatch = 0; xTileBatch < xTileBatches; xTileBatch++) {
                // Calculate the remainder x tiles
                int tilesX = Math.min(this.tilesX, textTilesX - xTileBatch * this.tilesX);

                for (int tileX = 0; tileX < tilesX; tileX++) {
                    for (int tileY = 0, tilesY = textTilesY; tileY < tilesY; tileY++) {
                        int textTileX = tileX + xTileBatch * this.tilesX;
                        int textTileY = tileY;

                        int drawX = (int)getX() + tileX * (tileSize + 1);
                        int drawY = (int)getY() + (int)(getHeight() + 1 -
                                (tileY + xTileBatch * tilesY + 1 + animRows) * (tileSize + 1)
                        );

                        drawer.draw(drawX, drawY, textTileX, textTileY);
                    }
                }
            }
        }
    }*/

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        /*if (tileLayer != null) {
            performDrawFor((drawX, drawY, textTileX, textTileY) -> {
                if (textTileX == TileLayer.TAG_ANIM) {
                    int[] anim = tileLayer.getAnimation(textTileY);
                    int frameOffset = tileLayer.getFrameOffset(anim);

                    textTileX = anim[frameOffset * 2];
                    textTileY = anim[frameOffset * 2 + 1];
                }

                int tileSize = getTileSize();
                batch.draw(
                        tileLayer.getTexture(),
                        drawX, drawY,
                        textTileX * tileSize,
                        textTileY * tileSize,
                        tileSize,
                        tileSize
                );
            });

            int tileX, tileY;
            int tileSize = getTileSize();
            if (selectedTileX == TileLayer.TAG_ANIM) {
                tileX = selectedTileY % tilesX;
                tileY = selectedTileY / tilesX;

            } else {
                int xTileBatch = selectedTileX / tilesX;
                int animRows = (int)Math.ceil(tileLayer.getAnimations().length / (float)tilesX);
                tileX = selectedTileX % tilesX;
                tileY = selectedTileY + xTileBatch * tileLayer.getTextureTilesY() + animRows;
            }

            int drawX = (int) getX() + tileX * (tileSize + 1);
            int drawY = (int) getY() + (int) (getHeight() - (tileY + 1) * (tileSize + 1)) + 1;

            Textures.drawRect(batch, Color.WHITE,
                    drawX, drawY,
                    tileSize,
                    tileSize,
                    1
            );

            Textures.drawRect(batch, Color.BLACK,
                    drawX - 1, drawY - 1,
                    tileSize + 2,
                    tileSize + 2,
                    1
            );
        }*/
    }

    public interface Drawer {
        void draw(int drawX, int drawY, int textTileX, int textTileY);
    }
}
