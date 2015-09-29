package com.coffeebland.cossinlette3.editor.tools;

import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.editor.OperationExecutor;
import com.coffeebland.cossinlette3.editor.ui.Operation;
import com.coffeebland.cossinlette3.utils.V2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Guillaume on 2015-09-24.
 */
public abstract class AbsTool<Op extends Operation> implements Tool {
    @NotNull protected Vector2 posMeters = V2.get();
    @Nullable protected Vector2 initialPosMeters;
    @Nullable protected Op pendingOperation;

    @Override @NotNull public Vector2 getPosMeters() { return posMeters; }
    @Override @Nullable public Vector2 getInitialPosMeters() { return initialPosMeters; }
    @Override @Nullable public Op getPendingOperation() { return pendingOperation; }

    @Override public void complete(@NotNull OperationExecutor executor) {
        if (pendingOperation != null && initialPosMeters != null) {
            executor.execute(pendingOperation, false);
            pendingOperation = null;
            V2.claim(initialPosMeters);
            initialPosMeters = null;
        }
    }
    @Override public void cancel() {
        if (pendingOperation != null && initialPosMeters != null) {
            pendingOperation.cancel();
            pendingOperation = null;
            V2.claim(initialPosMeters);
            initialPosMeters = null;
        }
    }
}
