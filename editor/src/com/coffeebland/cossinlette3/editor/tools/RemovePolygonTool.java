package com.coffeebland.cossinlette3.editor.tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.editor.ui.WorldWidget;
import com.coffeebland.cossinlette3.game.file.PolygonDef;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.utils.Dst;
import com.coffeebland.cossinlette3.utils.Textures;
import com.coffeebland.cossinlette3.utils.V2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

/**
 * Created by Guillaume on 2015-09-14.
 */
public class RemovePolygonTool extends AbsTool<RemovePolygonTool.RemovePolygonOperation> {

    @NtN Color color = new Color(1, 0, 1, 0.5f);

    public RemovePolygonTool() {}

    protected void drawRect(@NtN WorldWidget widget, @NtN Batch batch) {
        Vector2 minPix;
        Vector2 maxPix;
        if (initialPosMeters == null) {
            minPix = Dst.getAsPixels(V2.get(posMeters));
            maxPix = V2.get(minPix);
        } else {
            minPix = Dst.getAsPixels(V2.get(posMeters));
            maxPix = Dst.getAsPixels(V2.get(initialPosMeters));
            Vector2 tmpMax = V2.get(maxPix);
            V2.max(maxPix, minPix);
            V2.min(minPix, tmpMax);
            V2.claim(tmpMax);
        }
        Vector2 cameraPixels = Dst.getAsPixels(V2.get(widget.getCameraPos()));
        minPix.add(widget.getX(), widget.getY()).sub(cameraPixels);
        maxPix.add(widget.getX(), widget.getY()).sub(cameraPixels);
        Textures.drawFilledRect(batch, color, minPix, maxPix.sub(minPix));
        V2.claim(minPix, maxPix, cameraPixels);
    }
    @Override
    public void draw(@NtN WorldWidget widget, @NtN Batch batch) {
        drawRect(widget, batch);
    }

    @Override
    public void begin(@NtN WorldDef worldDef) {
        assert pendingOperation == null && initialPosMeters == null;
        initialPosMeters = V2.get(posMeters);
        pendingOperation = new RemovePolygonOperation(worldDef, V2.get(initialPosMeters), V2.get(posMeters));
        pendingOperation.execute();
    }

    @Override
    public void update(@NtN WorldDef worldDef) {
        if (pendingOperation != null && initialPosMeters != null) {
            pendingOperation.update(V2.get(initialPosMeters), V2.get(posMeters));
        }
    }

    public static class RemovePolygonOperation extends PolygonToolOperation {

        public static class RemoveUnit {
            PolygonDef def;
            int index;
        }

        Stack<RemoveUnit> unitsStack = new Stack<>();
        @N Vector2 bl, tl, tr, br;

        public RemovePolygonOperation(@NtN WorldDef worldDef, @NtN Vector2 initialPos, @NtN Vector2 targetPos) {
            super(worldDef, initialPos, targetPos);
        }

        protected void updateBox() {
            Vector2 min = V2.min(V2.get(initialPos), targetPos);
            Vector2 max = V2.max(V2.get(initialPos), targetPos);

            if (bl == null) bl = V2.get(min.x, min.y);
            else bl.set(min);
            if (tl == null) tl = V2.get(min.x, max.y);
            else tl.set(min.x, max.y);
            if (tr == null) tr = V2.get(max.x, max.y);
            else tr.set(max);
            if (br == null) br = V2.get(max.x, min.y);
            else br.set(max.x, min.y);

            V2.claim(min);
            V2.claim(max);
        }

        protected boolean contains(
                @NtN Vector2 a1, @NtN Vector2 a2,
                @NtN Vector2 b) {
            float minX = Math.min(a1.x, a2.x);
            float maxX = Math.max(a1.x, a2.x);
            float minY = Math.min(a1.y, a2.y);
            float maxY = Math.max(a1.y, a2.y);

            return b.x > minX
                    && b.x < maxX
                    && b.y > minY
                    && b.y < maxY;
        }
        protected boolean intersectLines(
                @NtN Vector2 a1, @NtN Vector2 a2,
                @NtN Vector2 b1, @NtN Vector2 b2
        ) {
            Vector2 tmp = V2.get();
            boolean result = false;
            intersection: {
                boolean intersected = Intersector.intersectLines(a1, a2, b1, b2, tmp);
                if (!intersected) break intersection;

                // Make sure the interseciton is actually along a common axis

                // For x
                float minX = Math.max(Math.min(a1.x, a2.x), Math.min(b1.x, b2.x));
                float maxX = Math.min(Math.max(a1.x, a2.x), Math.max(b1.x, b2.x));
                if (tmp.x < minX || tmp.x > maxX) break intersection;

                // For y
                float minY = Math.max(Math.min(a1.y, a2.y), Math.min(b1.y, b2.y));
                float maxY = Math.min(Math.max(a1.y, a2.y), Math.max(b1.y, b2.y));
                if (tmp.y < minY || tmp.y > maxY) break intersection;

                result = true;
            }
            V2.claim(tmp);
            return result;
        }
        @Override
        public void execute() {
            updateBox();
            assert bl != null && tl != null && tr != null && br != null;

            defs: for (int i = worldDef.staticPolygons.size() - 1; i >= 0; i--) {
                PolygonDef def = worldDef.staticPolygons.get(i);

                for (int ptI = 0; ptI < def.points.length - 2; ptI += 2) {
                    Vector2 pt1 = V2.get(def.points[ptI], def.points[ptI + 1]);
                    Vector2 pt2 = V2.get(def.points[ptI + 2], def.points[ptI + 3]);

                    if (contains(bl, tr, pt1)
                            || contains(bl, tr, pt2)
                            || intersectLines(pt1, pt2, bl, tl)
                            || intersectLines(pt1, pt2, tl, tr)
                            || intersectLines(pt1, pt2, tr, br)
                            || intersectLines(pt1, pt2, br, bl)
                    ) {
                        RemoveUnit unit = new RemoveUnit();
                        unit.index = i;
                        unit.def = def;
                        worldDef.staticPolygons.remove(i);
                        unitsStack.push(unit);
                        continue defs;
                    }

                    V2.claim(pt1, pt2);
                }
            }
        }

        @Override
        public void cancel() {
            while (!unitsStack.isEmpty()) {
                RemoveUnit unit = unitsStack.pop();
                worldDef.staticPolygons.add(unit.index, unit.def);
            }
            assert bl != null && tl != null && tr != null && br != null;
            V2.claim(bl, tl, tr, br);
            bl = tl = tr = br = null;
        }
    }
}
