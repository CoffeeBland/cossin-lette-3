package com.coffeebland.cossinlette3.editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.editor.OperationExecutor;
import com.coffeebland.cossinlette3.editor.ui.Operation;
import com.coffeebland.cossinlette3.editor.ui.WorldWidget;
import com.coffeebland.cossinlette3.editor.ui.TileLayerSource;
import com.coffeebland.cossinlette3.game.entity.Tileset;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.utils.Dst;
import com.coffeebland.cossinlette3.utils.Textures;
import com.coffeebland.cossinlette3.utils.V2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Guillaume on 2015-08-30.
 */
public abstract class TileTool implements Tool {

    public static boolean fromTop() {
        return !(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT));
    }

    @NotNull protected TileSource tileSource;
    @NotNull protected TileLayerSource tileLayerSource;
    @NotNull protected Vector2 posMeters = V2.get();
    @Nullable protected Vector2 initialPosMeters;
    @Nullable protected TileToolOperation pendingOperation;

    public TileTool(@NotNull TileSource tileSource, @NotNull TileLayerSource tileLayerSource) {
        this.tileSource = tileSource;
        this.tileLayerSource = tileLayerSource;
    }

    @Override @NotNull public Vector2 getPosMeters() { return posMeters; }
    @Override @Nullable public Vector2 getInitialPosMeters() { return initialPosMeters; }
    @Override @Nullable public Operation getPendingOperation() { return pendingOperation; }

    /**
     * Mutates the vector to correspond to the tile position of the meters given;
     * the position is clamped inside the def, and is floored to the nearest integers
     */
    @NotNull public Vector2 getTilePos(@NotNull Vector2 posMeters, @NotNull WorldDef worldDef) {
        @NotNull Tileset ts = tileSource.getTileset();
        Vector2 pos = ts.metersToTile(posMeters);
        pos = V2.clamp(pos,
                0, ts.metersToTile(worldDef.width) - tileSource.getSelectedWidth(),
                0, ts.metersToTile(worldDef.height) - tileSource.getSelectedHeight()
        );
        pos = V2.floor(pos);
        return pos;
    }

    public void draw(@NotNull WorldWidget widget, @NotNull Batch batch) {
        @Nullable WorldDef worldDef = widget.getWorldDef();
        if (worldDef == null) return;
        @NotNull Tileset ts = tileSource.getTileset();

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
    public void drawExtra(@NotNull WorldWidget widget, @NotNull Batch batch, @NotNull Vector2 bl, @NotNull Vector2 tr) {}

    @Override public void begin(@NotNull WorldDef worldDef) {
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
    @NotNull public abstract TileToolOperation createOperation(
            @NotNull WorldDef worldDef,
            int startX, int startY, int endX, int endY
    );
    @Override public void update(@NotNull WorldDef worldDef) {
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
    @Override public void complete(@NotNull OperationExecutor executor) {
        if (pendingOperation != null && initialPosMeters != null) {
            executor.execute(pendingOperation, false);
            pendingOperation = null;
            V2.claim(initialPosMeters);
            initialPosMeters = null;
        }
    }
    @Override public void cancel() {
        if (pendingOperation != null && initialPosMeters != null){
            pendingOperation.cancel();
            pendingOperation = null;
            V2.claim(initialPosMeters);
            initialPosMeters = null;
        }
    }
}
