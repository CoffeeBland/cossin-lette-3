package com.coffeebland.cossinlette3.utils;

import com.badlogic.gdx.math.Vector2;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;

public class VPool {

    protected static final Stack<Vector2> V2_STACK = new Stack<>();

    @NotNull
    public static Vector2 V2() {
        return V2_STACK.isEmpty() ? new Vector2() : V2_STACK.pop();
    }
    @NotNull
    public static Vector2 V2(float x, float y) {
        return V2().set(x, y);
    }
    @NotNull
    public static Vector2 V2(@NotNull Vector2 vector) {
        return V2().set(vector);
    }
    public static void claim(@NotNull Vector2 vector) {
        vector.setZero();
        V2_STACK.push(vector);
    }
}
