package com.coffeebland.cossinlette3.utils;

import com.badlogic.gdx.math.Vector2;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;

public class V2 {

    protected static final Stack<Vector2> V2_STACK = new Stack<>();

    @NotNull
    public static Vector2 get() {
        return V2_STACK.isEmpty() ? new Vector2() : V2_STACK.pop();
    }
    @NotNull
    public static Vector2 get(float x, float y) {
        return get().set(x, y);
    }
    @NotNull
    public static Vector2 get(@NotNull Vector2 vector) {
        return get().set(vector);
    }
    public static void claim(@NotNull Vector2 vector) {
        vector.setZero();
        V2_STACK.push(vector);
    }
    public static void claim(@NotNull Iterable<Vector2> vectors) {
        for (Vector2 vector : vectors) claim(vector);
    }
    public static void claim(@NotNull Vector2[] vectors) {
        for (Vector2 vector : vectors) claim(vector);
    }
    public static void claim(@NotNull Vector2 a, @NotNull Vector2 b) {
        claim(a);
        claim(b);
    }
    public static void claim(@NotNull Vector2 a, @NotNull Vector2 b, @NotNull Vector2 c) {
        claim(a);
        claim(b);
        claim(c);
    }
    public static void claim(@NotNull Vector2 a, @NotNull Vector2 b, @NotNull Vector2 c, @NotNull Vector2 d) {
        claim(a);
        claim(b);
        claim(c);
        claim(d);
    }

    @NotNull public static Vector2 floor(@NotNull Vector2 vec) {
        return vec.set((float)Math.floor(vec.x), (float)Math.floor(vec.y));
    }
    @NotNull public static Vector2 ceil(@NotNull Vector2 vec) {
        return vec.set((float)Math.ceil(vec.x), (float)Math.ceil(vec.y));
    }
    @NotNull public static Vector2 round(@NotNull Vector2 vec) {
        return vec.set((float)Math.round(vec.x), (float)Math.round(vec.y));
    }
    @NotNull public static Vector2 clamp(@NotNull Vector2 vec, float minX, float maxX, float minY, float maxY) {
        if (minX >= maxX) vec.x = (minX + maxX) / 2;
        else vec.x = Math.max(Math.min(vec.x, maxX), minX);
        if (minY >= maxY) vec.y = (minY + maxY) / 2;
        else vec.y = Math.max(Math.min(vec.y, maxY), minY);
        return vec;
    }
    @NotNull public static Vector2 min(@NotNull Vector2 target, @NotNull Vector2 other) {
        return target.set(Math.min(target.x, other.x), Math.min(target.y, other.y));
    }
    @NotNull public static Vector2 max(@NotNull Vector2 target, @NotNull Vector2 other) {
        return target.set(Math.max(target.x, other.x), Math.max(target.y, other.y));
    }
}
