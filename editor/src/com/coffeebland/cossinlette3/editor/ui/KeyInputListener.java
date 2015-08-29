package com.coffeebland.cossinlette3.editor.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class KeyInputListener extends InputListener {
    @NotNull protected final Map<Integer, Long> keycodes = new HashMap<>();
    @NotNull protected final Set<Integer> relevantKeys;

    public KeyInputListener(@NotNull Integer... relevantKeys) {
        this.relevantKeys = new HashSet<>(Arrays.asList(relevantKeys));
    }

    @Override public boolean keyDown(InputEvent event, int keycode) {
        if (relevantKeys.contains(keycode)) {
            keycodes.put(keycode, System.currentTimeMillis());
            onInputDown(keycode);
            return true;
        }
        return super.keyDown(event, keycode);
    }

    @Override public boolean keyUp(InputEvent event, int keycode) {
        if (keycodes.containsKey(keycode)) {
            onInputUp(keycode, keycodes.get(keycode));
            keycodes.remove(keycode);
            return true;
        }
        return super.keyUp(event, keycode);
    }

    public void updateInputs(float delta) {
        for (Map.Entry<Integer, Long> pressedKey: keycodes.entrySet()) {
            onInputUpdate(pressedKey.getKey(), pressedKey.getValue(), delta);
        }
    }

    public abstract void onInputDown(int keyCode);
    public abstract void onInputUpdate(int keyCode, long pressTime, float delta);
    public abstract void onInputUp(int keyCode, long pressTime);
}
