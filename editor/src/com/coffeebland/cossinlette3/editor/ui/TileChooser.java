package com.coffeebland.cossinlette3.editor.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.coffeebland.cossinlette3.game.entity.TileLayer;
import com.coffeebland.cossinlette3.utils.Textures;
import org.jetbrains.annotations.Nullable;

public class TileChooser extends Widget {

    public static final int WIDTH = 12 * (24 + 1) + 1;

    @Nullable protected Texture checkeredTexture;
    @Nullable protected TileLayer tileLayer;
    protected int tilesX;
    protected int selectedTileX, selectedTileY;

    public TileChooser() {
        addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (tileLayer != null) {
                    int tileX = (int) ((x - 1) / (getTileSize() + 1));
                    int tileY = (int) ((getHeight() - y + 1) / (getTileSize() + 1));
                    int selectedTileY = tileY % tileLayer.getTextureTilesY();
                    int xTileBatch = (tileY / tileLayer.getTextureTilesY());
                    int selectedTileX = tileX + xTileBatch * tileLayer.getTextureTilesY();
                    if (selectedTileX < tileLayer.getTextureTilesX() && selectedTileY < tileLayer.getTextureTilesY()) {
                        TileChooser.this.selectedTileX = selectedTileX;
                        TileChooser.this.selectedTileY = selectedTileY;
                    }
                }
            }
        });
    }

    public int getTileSize() {
        return tileLayer != null ? tileLayer.def.getTilesetDef().tileSize : 24;
    }
    @Override public float getPrefWidth() {
        return WIDTH;
    }
    @Override public float getPrefHeight() {
        if (tileLayer != null) {
            int xTileBatches = (int) Math.ceil(tileLayer.getTextureTilesX() / (float)tilesX);
            return xTileBatches * tileLayer.getTextureTilesY() * (getTileSize() + 1) + 1;
        }
        return super.getPrefHeight();
    }

    public void setTileLayer(@Nullable TileLayer tileLayer) {
        this.tileLayer = tileLayer;
        this.selectedTileX = 0;
        this.selectedTileY = 0;
        if (tileLayer != null) {
            tilesX = WIDTH / tileLayer.def.getTilesetDef().tileSize;
            checkeredTexture = Textures.getCheckeredTexture(
                    tileLayer.def.getTilesetDef().tileSize / 4,
                    new Color(0x4e4a55ff),
                    new Color(0x413d46ff)
            );
        }
        invalidateHierarchy();
    }

    public int getSelectedTileX() { return selectedTileX; }
    public int getSelectedTileY() { return selectedTileY; }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public void performDrawFor(Drawer drawer) {
        if (tileLayer != null && tileLayer.getTexture() != null) {
            int textTilesX = tileLayer.getTextureTilesX();
            int textTilesY = tileLayer.getTextureTilesY();

            int xTileBatches = (int)Math.ceil(textTilesX / (float)tilesX);
            for (int xTileBatch = 0; xTileBatch < xTileBatches; xTileBatch++) {
                // Calculate the remainder x tiles
                int tilesX = Math.min(this.tilesX, textTilesX - xTileBatch * textTilesY);

                for (int tileX = 0; tileX < tilesX; tileX++) {
                    for (int tileY = 0, tilesY = textTilesY; tileY < tilesY; tileY++) {
                        int textTileX = tileX + xTileBatch * tilesY;
                        int textTileY = tileY;

                        int drawX = (int)getX() +
                                tileX * (tileLayer.def.getTilesetDef().tileSize + 1) + 1;
                        int drawY = (int)getY() + (int)(getHeight() -
                                (tileY + xTileBatch * tilesY + 1) * (tileLayer.def.getTilesetDef().tileSize + 1)
                        );

                        drawer.draw(drawX, drawY, textTileX, textTileY);
                    }
                }
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (tileLayer != null) {
            performDrawFor((drawX, drawY, textTileX, textTileY) -> {
                batch.draw(
                        checkeredTexture,
                        drawX, drawY,
                        0, 0,
                        tileLayer.def.getTilesetDef().tileSize,
                        tileLayer.def.getTilesetDef().tileSize
                );
            });
            
            performDrawFor((drawX, drawY, textTileX, textTileY) -> {
                batch.draw(
                        tileLayer.getTexture(),
                        drawX, drawY,
                        textTileX * tileLayer.def.getTilesetDef().tileSize,
                        textTileY * tileLayer.def.getTilesetDef().tileSize,
                        tileLayer.def.getTilesetDef().tileSize,
                        tileLayer.def.getTilesetDef().tileSize
                );
            });

            int xTileBatch = selectedTileX / tilesX;
            int tileX = selectedTileX % tileLayer.getTextureTilesY();
            int tileY = selectedTileY + xTileBatch * tileLayer.getTextureTilesY();

            int drawX = (int)getX() +
                    tileX * (tileLayer.def.getTilesetDef().tileSize + 1) + 1;
            int drawY = (int)getY() + (int)(getHeight() -
                    (tileY + 1) * (tileLayer.def.getTilesetDef().tileSize + 1)
            );

            Textures.drawRect(batch, Color.BLACK,
                    drawX - 1, drawY - 1,
                    tileLayer.def.getTilesetDef().tileSize + 2,
                    tileLayer.def.getTilesetDef().tileSize + 2,
                    1
            );

            Textures.drawRect(batch, Color.WHITE,
                    drawX, drawY,
                    tileLayer.def.getTilesetDef().tileSize,
                    tileLayer.def.getTilesetDef().tileSize,
                    1
            );
        }
    }

    public interface Drawer {
        void draw(int drawX, int drawY, int textTileX, int textTileY);
    }
}
