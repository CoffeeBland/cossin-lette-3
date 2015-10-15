package com.coffeebland.cossinlette3.utils;

import com.badlogic.gdx.math.Vector2;

import java.util.Stack;

public class V2 {

    protected static final Stack<Vector2> V2_STACK = new Stack<>();

    @NtN public static Vector2 get() {
        return V2_STACK.isEmpty() ? new Vector2() : V2_STACK.pop();
    }
    @NtN public static Vector2 get(float x, float y) {
        return get().set(x, y);
    }
    @NtN public static Vector2 get(@NtN Vector2 vector) {
        return get().set(vector);
    }
    public static void claim(@NtN Vector2 vector) {
        vector.setZero();
        V2_STACK.push(vector);
    }
    public static void claim(@NtN Iterable<Vector2> vectors) {
        for (Vector2 vector : vectors) claim(vector);
    }
    public static void claim(@NtN Vector2[] vectors) {
        for (Vector2 vector : vectors) claim(vector);
    }
    public static void claim(@NtN Vector2 a, @NtN Vector2 b) {
        claim(a);
        claim(b);
    }
    public static void claim(@NtN Vector2 a, @NtN Vector2 b, @NtN Vector2 c) {
        claim(a);
        claim(b);
        claim(c);
    }
    public static void claim(@NtN Vector2 a, @NtN Vector2 b, @NtN Vector2 c, @NtN Vector2 d) {
        claim(a);
        claim(b);
        claim(c);
        claim(d);
    }

    @NtN public static Vector2 floor(@NtN Vector2 vec) {
        return vec.set((float)Math.floor(vec.x), (float)Math.floor(vec.y));
    }
    @NtN public static Vector2 ceil(@NtN Vector2 vec) {
        return vec.set((float)Math.ceil(vec.x), (float)Math.ceil(vec.y));
    }
    @NtN public static Vector2 round(@NtN Vector2 vec) {
        return vec.set((float)Math.round(vec.x), (float)Math.round(vec.y));
    }
    @NtN public static Vector2 clamp(@NtN Vector2 vec, float minX, float maxX, float minY, float maxY) {
        if (minX >= maxX) vec.x = (minX + maxX) / 2;
        else vec.x = Math.max(Math.min(vec.x, maxX), minX);
        if (minY >= maxY) vec.y = (minY + maxY) / 2;
        else vec.y = Math.max(Math.min(vec.y, maxY), minY);
        return vec;
    }
    @NtN public static Vector2 min(@NtN Vector2 target, @NtN Vector2 other) {
        return target.set(Math.min(target.x, other.x), Math.min(target.y, other.y));
    }
    @NtN public static Vector2 max(@NtN Vector2 target, @NtN Vector2 other) {
        return target.set(Math.max(target.x, other.x), Math.max(target.y, other.y));
    }
}
