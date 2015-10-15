package com.coffeebland.cossinlette3.editor.tools;

import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.editor.ui.Operation;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.utils.V2;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Guillaume on 2015-09-03.
 */
public abstract class PolygonToolOperation implements Operation {

    @NtN WorldDef worldDef;
    @NtN Vector2 initialPos;
    @NtN Vector2 targetPos;

    public PolygonToolOperation(
            @NtN WorldDef worldDef,
            @NtN Vector2 initialPos,
            @NtN Vector2 targetPos
    ) {
        this.worldDef = worldDef;
        this.initialPos = initialPos;
        this.targetPos = targetPos;
    }

    public void update(@NtN Vector2 initialPos, @NtN Vector2 targetPos) {
        cancel();
        V2.claim(this.initialPos, this.targetPos);
        this.initialPos = initialPos;
        this.targetPos = targetPos;
        execute();
    }
}
