package com.coffeebland.cossinlette3.editor.tools;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.editor.OperationExecutor;
import com.coffeebland.cossinlette3.editor.ui.Operation;
import com.coffeebland.cossinlette3.editor.ui.WorldWidget;
import com.coffeebland.cossinlette3.game.entity.Tileset;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.utils.V2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Guillaume on 2015-09-03.
 */
public abstract class PolygonTool implements Tool {

    @NotNull protected TileSource tileSource;
    @NotNull Vector2 posMeters = V2.get();
    @Nullable Vector2 initialPosMeters;
    @Nullable PolygonToolOperation pendingOperation;

    public PolygonTool(@NotNull TileSource tileSource) {
        this.tileSource = tileSource;
    }

    @Override @NotNull public Vector2 getPosMeters() { return posMeters; }
    @Override @Nullable public Vector2 getInitialPosMeters() { return initialPosMeters; }
    @Override @Nullable public Operation getPendingOperation() { return pendingOperation; }

    @NotNull public Vector2 getPartialTilePos(@NotNull Vector2 posMeters, @NotNull WorldDef worldDef) {
        @NotNull Tileset ts = tileSource.getTileset();
        Vector2 pos = posMeters.scl(4f / ts.getTileSizeMeters());
        pos = V2.clamp(pos, 0, worldDef.width, 0, worldDef.height);
        pos = V2.floor(pos);
        return pos;
    }

    @Override public void draw(@NotNull WorldWidget widget, @NotNull Batch batch) {

    }

    @Override public void begin(@NotNull WorldDef worldDef) {
        assert pendingOperation == null && initialPosMeters == null;
        initialPosMeters = V2.get(posMeters);
    }
    @Override public void update(@NotNull WorldDef worldDef) {
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

    }
}
