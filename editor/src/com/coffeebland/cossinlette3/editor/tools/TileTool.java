package com.coffeebland.cossinlette3.editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.editor.ui.TileLayerSource;
import com.coffeebland.cossinlette3.editor.ui.WorldWidget;
import com.coffeebland.cossinlette3.game.entity.Tileset;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.utils.Dst;
import com.coffeebland.cossinlette3.utils.NtN;
import com.coffeebland.cossinlette3.utils.Textures;
import com.coffeebland.cossinlette3.utils.V2;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Guillaume on 2015-08-30.
 */
public abstract class TileTool extends AbsTool<TileToolOperation> {

    public static boolean fromTop() {
        return !(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT));
    }

    @NtN protected TileSource tileSource;
    @NtN protected TileLayerSource tileLayerSource;

    public TileTool(@NtN TileSource tileSource, @NtN TileLayerSource tileLayerSource) {
        this.tileSource = tileSource;
        this.tileLayerSource = tileLayerSource;
    }

    /**
     * Mutates the vector to correspond to the tile position of the meters given;
     * the position is clamped inside the def, and is floored to the nearest integers
     */
    @NtN public Vector2 getTilePos(@NtN Vector2 posMeters, @NtN WorldDef worldDef) {
        @NtN Tileset ts = tileSource.getTileset();
        Vector2 pos = ts.metersToTile(posMeters);
        pos = V2.clamp(pos,
                0, ts.metersToTile(worldDef.width) - tileSource.getSelectedWidth(),
                0, ts.metersToTile(worldDef.height) - tileSource.getSelectedHeight()
        );
        pos = V2.floor(pos);
        return pos;
    }

    public void draw(@NtN WorldWidget widget, @NtN Batch batch) {
        @N WorldDef worldDef = widget.getWorldDef();
        if (worldDef == null) return;
        @NtN Tileset ts = tileSource.getTileset();

        Vector2 pos = getTilePos(V2.get(posMeters), worldDef);
        Vector2 cameraPixels = Dst.getAsPixels(V2.get(widget.getCameraPos()));
        pos = ts.tileToPix(pos)
                .add(widget.getX(), widget.getY())
                .sub(cameraPixels);
        Vector2 bl = V2.get(), tr = V2.get();
        if (initialPosMeters == null) {
            bl.set(pos);
            ts.tileToPix(tr.set(tileSource.getSelectedWidth(), tileSource.getSelectedHeight())).add(bl);
        } else {
            Vector2 initPos = ts.tileToPix(getTilePos(V2.get(initialPosMeters), worldDef))
                    .add(widget.getX(), widget.getY())
                    .sub(cameraPixels);
            V2.min(bl.set(pos), initPos);
            Vector2 tmp = V2.max(V2.get(pos), initPos);
            ts.tileToPix(tr.set(tileSource.getSelectedWidth(), tileSource.getSelectedHeight())).add(tmp);
            V2.claim(tmp);
            V2.claim(initPos);
        }
        Textures.drawRect(batch,
                Color.WHITE,
                bl.x,
                bl.y,
                tr.x - bl.x,
                tr.y - bl.y,
                1
        );
        drawExtra(widget, batch, bl, tr);
        V2.claim(cameraPixels);
        V2.claim(pos);
        V2.claim(bl);
        V2.claim(tr);
    }
    public void drawExtra(@NtN WorldWidget widget, @NtN Batch batch, @NtN Vector2 bl, @NtN Vector2 tr) {}

    @Override public void begin(@NtN WorldDef worldDef) {
        assert pendingOperation == null && initialPosMeters == null;
        initialPosMeters = V2.get(posMeters);
        Vector2 initialTilePos = getTilePos(V2.get(posMeters), worldDef);
        Vector2 tilePos = getTilePos(V2.get(posMeters), worldDef);
        int startX = (int)Math.min(initialTilePos.x, tilePos.x);
        int startY = (int)Math.min(initialTilePos.y, tilePos.y);
        int endX = (int)Math.max(initialTilePos.x, tilePos.x);
        int endY = (int)Math.max(initialTilePos.y, tilePos.y);
        V2.claim(initialTilePos);
        V2.claim(tilePos);
        pendingOperation = createOperation(worldDef, startX, startY, endX, endY);
        pendingOperation.execute();
    }
    @NtN public abstract TileToolOperation createOperation(
            @NtN WorldDef worldDef,
            int startX, int startY, int endX, int endY
    );
    @Override public void update(@NtN WorldDef worldDef) {
        if (pendingOperation != null && initialPosMeters != null) {
            Vector2 tilePos = getTilePos(V2.get(posMeters), worldDef);
            Vector2 initialTilePos = getTilePos(V2.get(initialPosMeters), worldDef);
            int startX = (int)Math.min(initialTilePos.x, tilePos.x);
            int startY = (int)Math.min(initialTilePos.y, tilePos.y);
            int endX = (int)Math.max(initialTilePos.x, tilePos.x);
            int endY = (int)Math.max(initialTilePos.y, tilePos.y);
            V2.claim(tilePos, initialTilePos);
            pendingOperation.update(
                    tileLayerSource.getTileLayerIndex(),
                    startX, startY,
                    endX, endY,
                    fromTop()
            );
        }
    }
}
