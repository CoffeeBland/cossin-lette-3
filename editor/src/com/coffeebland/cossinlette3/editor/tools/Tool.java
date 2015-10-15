package com.coffeebland.cossinlette3.editor.tools;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.editor.OperationExecutor;
import com.coffeebland.cossinlette3.editor.ui.Operation;
import com.coffeebland.cossinlette3.editor.ui.WorldWidget;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Guillaume on 2015-09-03.
 */
public interface Tool {

    @NtN Vector2 getPosMeters();
    @N Vector2 getInitialPosMeters();
    @N Operation getPendingOperation();

    void draw(@NtN WorldWidget widget, @NtN Batch batch);
    default <T extends Tool> void transferState(@NtN T tool, @NtN WorldDef worldDef) {
        if (tool.getInitialPosMeters() != null && tool.getPendingOperation() != null) {
            getPosMeters().set(tool.getInitialPosMeters());
            tool.cancel();
            begin(worldDef);
            getPosMeters().set(tool.getPosMeters());
            update(worldDef);
        } else {
            getPosMeters().set(tool.getPosMeters());
        }
    }

    void begin(@NtN WorldDef worldDef);
    void complete(@NtN OperationExecutor executor);
    void cancel();
    void update(@NtN WorldDef worldDef);
}
