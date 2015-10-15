package com.coffeebland.cossinlette3.editor.tools;

import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.game.file.PolygonDef;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Guillaume on 2015-09-03.
 */
public class AddPolygonTool extends PolygonTool {
    public AddPolygonTool(@NtN TileSource tileSource) {
        super(tileSource);
    }

    @Override @NtN public PolygonToolOperation createOperation(
            @NtN WorldDef worldDef,
            @NtN Vector2 initialTilePos, @NtN Vector2 tilePos
    ) {
        return new AddPolygonOperation(worldDef, initialTilePos, tilePos);
    }

    public static class AddPolygonOperation extends PolygonToolOperation {

        PolygonDef def;

        public AddPolygonOperation(
                @NtN WorldDef worldDef,
                @NtN Vector2 initialPos,
                @NtN Vector2 targetPos
        ) {
            super(worldDef, initialPos, targetPos);
        }

        @Override
        public void execute() {
            if (def == null) {
                def = new PolygonDef();
                def.points = new float[4];
            }
            if (initialPos.equals(targetPos)) return;
            def.points[0] = initialPos.x;
            def.points[1] = initialPos.y;
            def.points[2] = targetPos.x;
            def.points[3] = targetPos.y;
            worldDef.staticPolygons.add(def);
        }
        @Override
        public void cancel() {
            worldDef.staticPolygons.remove(def);
        }
    }
}
