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
import java.util.Random;

/**
 * Created by Guillaume on 2015-09-03.
 */
public class SetTool extends TileTool {

    public SetTool(@NotNull TileSource source) {
        super(source);
    }

    @Override
    public void drawExtra(@NotNull WorldWidget widget, @NotNull Batch batch, @NotNull Vector2 bl, @NotNull Vector2 tr) {
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
    }

    @NotNull
    @Override
    public TileToolOperation createOperation(@NotNull WorldDef worldDef, int tileLayerIndex, int startX, int startY, int endX, int endY) {
        return new SetOperation(source, worldDef, tileLayerIndex, startX, startY, endX, endY);
    }

    public static class SetOperation extends TileToolOperation {

        protected Random rnd = new Random();

        protected List<SetUnit> setUnits = new ArrayList<>();

        public SetOperation(@NotNull TileSource source, @NotNull WorldDef worldDef, int tileLayerIndex, int startX, int startY, int endX, int endY) {
            super(source, worldDef, tileLayerIndex, startX, startY, endX, endY, true);
        }

        @Override
        public void execute() {
            mapOver((int tX, int tY, int tTX, int tTY) -> {
                TileBlockSource tbSrc = source.getTileBlockSource();
                long tiles[] = worldDef.tileLayers.get(tileLayerIndex).setTile(
                        tX, tY,
                        tbSrc.getType(), tbSrc.getTypeIndex(),
                        tTX, tTY + tbSrc.getTileOffset(rnd.nextFloat())
                );
                if (tiles != null) setUnits.add(new SetUnit(tX, tY, tiles));
            });
        }

        @Override
        public void cancel() {
            for (SetUnit unit : setUnits) {
                worldDef.tileLayers.get(tileLayerIndex).setTiles(unit.x, unit.y, unit.tiles);
            }
            setUnits.clear();
        }

        protected static class SetUnit {
            int x, y;
            long[] tiles;

            public SetUnit(int x, int y, long[] tiles) {
                this.x = x;
                this.y = y;
                this.tiles = tiles;
            }
        }
    }
}
