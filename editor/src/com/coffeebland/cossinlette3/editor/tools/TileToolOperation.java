package com.coffeebland.cossinlette3.editor.tools;

import com.coffeebland.cossinlette3.editor.ui.Operation;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.utils.NtN;

/**
 * Created by Guillaume on 2015-08-30.
 */
public abstract class TileToolOperation implements Operation {

    @NtN protected WorldDef worldDef;
    protected int selectedTileX, selectedTileY, selectedWidth, selectedHeight;
    @NtN protected TileBlockSource tileBlockSource;
    protected int tileLayerIndex, startX, startY, endX, endY;
    protected boolean fromTop;

    public TileToolOperation(
            @NtN TileSource source,
            @NtN WorldDef worldDef,
            int tileLayerIndex,
            int startX, int startY,
            int endX, int endY,
            boolean fromTop
    ) {
        selectedTileX = source.getSelectedTileX();
        selectedTileY = source.getSelectedTileY();
        selectedWidth = source.getSelectedWidth();
        selectedHeight = source.getSelectedHeight();
        tileBlockSource = source.getTileBlockSource();
        this.worldDef = worldDef;
        this.tileLayerIndex = tileLayerIndex;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.fromTop = fromTop;
    }

    public void update(int tileLayerIndex, int startX, int startY, int endX, int endY, boolean fromTop) {
        if (this.tileLayerIndex == tileLayerIndex
                && this.startX == startX
                && this.startY == startY
                && this.endX == endX
                && this.endY == endY
                && this.fromTop == fromTop) return;
        cancel();
        this.tileLayerIndex = tileLayerIndex;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.fromTop = fromTop;
        execute();
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    protected void mapOver(@NtN Mapper mapper) {
        /**
         * s => selected
         * t => tile
         * w => width
         * h => height
         * r => ramp
         * f => fill
         */
        int sTX = selectedTileX;
        int sTY = selectedTileY;
        int sW = selectedWidth;
        int sH = selectedHeight;

        int tW = endX - startX + sW;
        int tH = endY - startY + sH;

        int rW = sW / 2;
        int rH = sH / 2;

        int sTLeft = sTX, sTRight = sTX + sW - 1;
        int sTBottom = sTY, sTTop = sTY - sH + 1;

        int fW = tW - rW * 2;
        int fH = tH - rH * 2;

        for (int x = 0; x < tW; x++) {

            int fTX;
            if (x < rW) fTX = sTLeft + x;
            else if (x >= rW + fW) fTX = sTRight - (x - rW - fW);
            else {
                int uncoveredW = sW - rW * 2;
                int fX = x - rW;
                fTX = sTX + rW + fX * uncoveredW / fW;
            }
            for (int y = 0; y < tH; y++) {

                int fTY;
                if (y < rH) fTY = sTBottom - y;
                else if (y >= rH + fH) fTY  = sTTop + rH - (y - rH - fH) - 1;
                else {
                    int uncoveredH = sH - rH * 2;
                    int fY = y - rH;
                    fTY = sTY - rH - fY * uncoveredH / fH;
                }
                mapper.map(startX + x, startY + y, fTX, fTY);
            }
        }
    }

    public interface Mapper {
        void map(int tileX, int tileY, int tilesetTileX, int tilesetTileY);
    }
}
