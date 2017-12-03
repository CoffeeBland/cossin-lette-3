package com.coffeebland.cossinlette3.editor.input;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.coffeebland.cossinlette3.utils.NtN;

import java.util.*;

public abstract class KeyInput extends InputListener {
    @NtN protected final Map<Integer, Long> keycodes = new HashMap<>();
    @NtN protected final Set<Integer> relevantKeys;

    public KeyInput(@NtN Integer... relevantKeys) {
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
    public boolean isKeyDown(int keycode) {
        return keycodes.containsKey(keycode);
    }

    public void update(float delta) {
        for (Map.Entry<Integer, Long> pressedKey: keycodes.entrySet()) {
            onInputUpdate(pressedKey.getKey(), pressedKey.getValue(), delta);
        }
    }

    public abstract void onInputDown(int keyCode);
    public abstract void onInputUpdate(int keyCode, long pressTime, float delta);
    public abstract void onInputUp(int keyCode, long pressTime);
}
