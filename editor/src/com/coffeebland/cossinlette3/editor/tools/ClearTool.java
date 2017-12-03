package com.coffeebland.cossinlette3.editor.tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.editor.ui.TileLayerSource;
import com.coffeebland.cossinlette3.editor.ui.WorldWidget;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.utils.NtN;
import com.coffeebland.cossinlette3.utils.Textures;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guillaume on 2015-09-03.
 */
public class ClearTool extends TileTool {

    public ClearTool(@NtN TileSource tileSource, @NtN TileLayerSource tileLayerSource) {
        super(tileSource, tileLayerSource);
    }

    @Override
    public void drawExtra(@NtN WorldWidget widget, @NtN Batch batch, @NtN Vector2 bl, @NtN Vector2 tr) {
        Textures.drawFilledRect(
                batch, Color.WHITE,
                tr.x + 2, bl.y - 3,
                5, 1
        );

        Textures.drawFilledRect(
                batch, Color.WHITE,
                tr.x + 2, bl.y - 5,
                5, 1
        );

        Textures.drawFilledRect(
                batch, Color.WHITE,
                tr.x + 4, bl.y - 6,
                1, 5
        );
    }

    @NtN
    @Override
    public TileToolOperation createOperation(@NtN WorldDef worldDef, int startX, int startY, int endX, int endY) {
        return new ClearOperation(
                tileSource, worldDef,
                tileLayerSource.getTileLayerIndex(),
                startX, startY,
                endX, endY
        );
    }

    public static class ClearOperation extends TileToolOperation {

        protected List<ClearUnit> clearUnits = new ArrayList<>();

        public ClearOperation(@NtN TileSource source, @NtN WorldDef worldDef, int tileLayerIndex, int startX, int startY, int endX, int endY) {
            super(source, worldDef, tileLayerIndex, startX, startY, endX, endY, true);
        }

        @Override
        public void execute() {
            mapOver((int tX, int tY, int tTX, int tTY) -> {
                long tiles[] = worldDef.tileLayers.get(tileLayerIndex).setTiles(tX, tY, new long[0]);
                if (tiles != null) clearUnits.add(new ClearUnit(tX, tY, tiles));
            });
        }

        @Override
        public void cancel() {
            for (ClearUnit unit : clearUnits) {
                worldDef.tileLayers.get(tileLayerIndex).setTiles(unit.x, unit.y, unit.tiles);
            }
            clearUnits.clear();
        }

        protected static class ClearUnit {
            int x, y;
            long[] tiles;

            public ClearUnit(int x, int y, long[] tiles) {
                this.x = x;
                this.y = y;
                this.tiles = tiles;
            }
        }
    }
}
