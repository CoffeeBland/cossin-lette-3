package com.coffeebland.cossinlette3.editor.tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.editor.ui.WorldWidget;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.utils.Textures;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.coffeebland.cossinlette3.game.file.TileLayerDef.NO_TILE;

/**
 * Created by Guillaume on 2015-09-01.
 */
public class RemoveTool extends TileTool {

    public RemoveTool(@NotNull TileSource source, boolean fromTop) {
        super(source, fromTop);
    }

    @NotNull
    @Override
    public TileToolOperation createOperation(
            @NotNull WorldDef worldDef, int tileLayerIndex,
            int startX, int startY, int endX, int endY
    ) {
        return new RemoveOperation(
                source, worldDef,
                tileLayerIndex,
                startX, startY,
                endX, endY,
                fromTop
        );
    }

    @Override
    public void drawExtra(@NotNull WorldWidget widget, @NotNull Batch batch, @NotNull Vector2 bl, @NotNull Vector2 tr) {
        Textures.drawFilledRect(
                batch, Color.WHITE,
                tr.x + 2, bl.y - 4,
                5, 1
        );

        if (fromTop) {
            Textures.drawFilledRect(
                    batch, Color.WHITE,
                    tr.x + 3, bl.y,
                    3, 1
            );
        } else {
            Textures.drawFilledRect(
                    batch, Color.WHITE,
                    tr.x + 3, bl.y - 8,
                    3, 1
            );
        }
    }

    public static class RemoveOperation extends TileToolOperation {

        protected List<RemoveUnit> removedUnits = new ArrayList<>();

        public RemoveOperation(@NotNull TileSource source,
                               @NotNull WorldDef worldDef,
                               int tileLayerIndex,
                               int startX, int startY,
                               int endX, int endY,
                               boolean fromTop) {
            super(source, worldDef, tileLayerIndex, startX, startY, endX, endY, fromTop);
        }

        @Override
        public void execute() {
            mapOver((tileX, tileY, tilesetTileX, tilesetTileY) -> {
                long tile = worldDef.tileLayers.get(tileLayerIndex).removeTile(tileX, tileY, fromTop);
                if (tile != NO_TILE) {
                    removedUnits.add(new RemoveUnit(tileX, tileY, tile));
                }
            });
        }

        @Override
        public void cancel() {
            for (RemoveUnit unit : removedUnits) {
                worldDef.tileLayers.get(tileLayerIndex).addTile(unit.x, unit.y, unit.tile, fromTop);
            }
            removedUnits.clear();
        }

        protected static class RemoveUnit {
            int x, y;
            long tile;

            public RemoveUnit(int x, int y, long tile) {
                this.x = x;
                this.y = y;
                this.tile = tile;
            }
        }
    }
}
