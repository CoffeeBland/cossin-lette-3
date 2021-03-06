package com.coffeebland.cossinlette3.editor.tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.editor.ui.WorldWidget;
import com.coffeebland.cossinlette3.game.entity.Tileset;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.utils.*;

/**
 * Created by Guillaume on 2015-09-03.
 */
public abstract class PolygonTool extends AbsTool<PolygonToolOperation> {

    @NtN TileSource tileSource;

    public PolygonTool(@NtN TileSource tileSource) {
        this.tileSource = tileSource;
    }

    @NtN public Vector2 getPartialTilePos(@NtN Vector2 posMeters, @NtN WorldDef worldDef) {
        Vector2 pos = posMeters;
        pos = V2.clamp(pos,
                0, worldDef.width,
                0, worldDef.height
        );
        pos = V2.round(pos.scl(8)).scl(1f / 8f);
        return pos;
    }

    protected void drawGrid(
            @NtN WorldWidget widget,
            @NtN Batch batch,
            @NtN Vector2 cameraPixels
    ) {
        Tileset ts = tileSource.getTileset();

        Color partial = Color.BLACK.cpy();
        partial.a = 0.25f;
        float halfTile = ts.getTileSizePixels() / 2f;
        int nx = (int)Math.floor(ts.pixToTile(widget.getWidth()) * 2);
        for (int x = 0; x < nx; x++) {
            Textures.drawFilledRect(
                    batch, partial,
                    widget.getX() - (cameraPixels.x % halfTile) + x * halfTile - 1,
                    widget.getY(),
                    2,
                    widget.getHeight()
            );
        }
        int ny = (int)Math.floor(ts.pixToTile(widget.getHeight()) * 2);
        for (int y = 0; y < ny; y++) {
            Textures.drawFilledRect(
                    batch, partial,
                    widget.getX(),
                    widget.getY() - (cameraPixels.y % halfTile) + y * halfTile - 1,
                    widget.getWidth(),
                    2
            );
        }
    }
    protected void drawTool(
            @NtN WorldWidget widget,
            @NtN WorldDef worldDef,
            @NtN Batch batch,
            @NtN Vector2 cameraPixels
    ) {
        Vector2 pos = getPartialTilePos(V2.get(posMeters), worldDef);
        pos = Dst.getAsPixels(pos)
                .add(widget.getX(), widget.getY())
                .sub(cameraPixels);

        Textures.drawFilledRect(batch,
                Color.BLACK,
                pos.x - 2, pos.y - 2,
                4, 4
        );
        Textures.drawFilledRect(batch,
                Color.WHITE,
                pos.x - 1, pos.y - 1,
                2, 2
        );

        V2.claim(pos);
    }
    @Override public void draw(@NtN WorldWidget widget, @NtN Batch batch) {
        @N WorldDef worldDef = widget.getWorldDef();
        if (worldDef == null) return;

        Vector2 cameraPixels = Dst.getAsPixels(V2.get(widget.getCameraPos()));

        drawGrid(widget, batch, cameraPixels);
        drawTool(widget, worldDef, batch, cameraPixels);

        V2.claim(cameraPixels);
    }

    @Override public void begin(@NtN WorldDef worldDef) {
        assert pendingOperation == null && initialPosMeters == null;
        initialPosMeters = V2.get(posMeters);
        Vector2 initialTilePos = getPartialTilePos(V2.get(initialPosMeters), worldDef);
        Vector2 tilePos = getPartialTilePos(V2.get(posMeters), worldDef);
        pendingOperation = createOperation(worldDef, initialTilePos, tilePos);
    }
    @NtN public abstract PolygonToolOperation createOperation(
            @NtN WorldDef worldDef,
            @NtN Vector2 initialTilePos,
            @NtN Vector2 tilePos
    );
    @Override public void update(@NtN WorldDef worldDef) {
        if (pendingOperation != null && initialPosMeters != null) {
            Vector2 initialTilePos = getPartialTilePos(V2.get(initialPosMeters), worldDef);
            Vector2 tilePos = getPartialTilePos(V2.get(posMeters), worldDef);
            pendingOperation.update(initialTilePos, tilePos);
        }
    }
}
