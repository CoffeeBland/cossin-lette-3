package com.coffeebland.cossinlette3.editor.tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.editor.ui.TileLayerSource;
import com.coffeebland.cossinlette3.editor.ui.WorldWidget;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.utils.NtN;
import com.coffeebland.cossinlette3.utils.Textures;

import java.util.Random;

/**
 * Created by Guillaume on 2015-08-30.
 */
public class AddTool extends TileTool {

    public AddTool(@NtN TileSource tS, @NtN TileLayerSource tLS) {
        super(tS, tLS);
    }

    @Override
    @NtN
    public TileToolOperation createOperation(
            @NtN WorldDef worldDef,
            int startX, int startY, int endX, int endY
    ) {
        return new AddOperation(
                tileSource, worldDef,
                tileLayerSource.getTileLayerIndex(),
                startX, startY,
                endX, endY,
                fromTop()
        );
    }

    @Override
    public void drawExtra(@NtN WorldWidget widget, @NtN Batch batch, @NtN Vector2 bl, @NtN Vector2 tr) {
        Textures.drawFilledRect(
                batch, Color.WHITE,
                tr.x + 2, bl.y - 4,
                5, 1
        );
        Textures.drawFilledRect(
                batch, Color.WHITE,
                tr.x + 4, bl.y - 6,
                1, 5
        );

        if (fromTop()) {
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

    public static class AddOperation extends TileToolOperation {

        protected Random rnd = new Random();

        public AddOperation(
                @NtN TileSource source,
                @NtN WorldDef worldDef,
                int tileLayerIndex,
                int startX, int startY,
                int endX, int endY,
                boolean fromTop
        ) {
            super(source, worldDef, tileLayerIndex, startX, startY, endX, endY, fromTop);
        }

        @Override
        public void execute() {
            mapOver((int tX, int tY, int tTX, int tTY) -> {
                TileBlockSource tbSrc = tileBlockSource;
                worldDef.tileLayers.get(tileLayerIndex).addTile(
                        tX, tY,
                        tbSrc.getType(), tbSrc.getTypeIndex(),
                        tTX, tTY + tbSrc.getTileOffset(rnd.nextFloat()),
                        fromTop
                );
            });
        }

        @Override
        public void cancel() {
            mapOver((int tX, int tY, int tTX, int tTY) -> worldDef.tileLayers.get(tileLayerIndex).removeTile(tX, tY, fromTop) );
        }
    }

}
