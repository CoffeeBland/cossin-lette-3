package com.coffeebland.cossinlette3.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.game.entity.Person;
import com.coffeebland.cossinlette3.utils.VPool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PlayerInput implements InputProcessor {

    protected final Map<Integer, Long> keycodes = new HashMap<>();

    @Nullable protected Person person;

    public PlayerInput() {
        this(null);
    }

    public PlayerInput(@Nullable Person person) {
        this.person = person;
    }

    public PlayerInput detach() {
        keycodes.clear();
        return this;
    }

    public boolean isPressed(int keycode) {
        return keycodes.containsKey(keycode);
    }
    @Nullable public Long timeStampAtPress(int keycode) {
        return keycodes.get(keycode);
    }

    @Override public boolean keyDown(int keycode) {
        keycodes.put(keycode, System.currentTimeMillis());
        return true;
    }
    @Override public boolean keyUp(int keycode) {
        keycodes.remove(keycode);
        return true;
    }

    @Override public boolean keyTyped(char character) {
        return false;
    }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }
    @Override public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
    @Override public boolean scrolled(int amount) {
        return false;
    }

    public void update(float delta) {
        if (person != null) {
            @NotNull Vector2 vec = VPool.V2();
            if (isPressed(Input.Keys.LEFT)) vec.add(-1, 0);
            if (isPressed(Input.Keys.UP)) vec.add(0, 1);
            if (isPressed(Input.Keys.RIGHT)) vec.add(1, 0);
            if (isPressed(Input.Keys.DOWN)) vec.add(0, -1);
            if (vec.len2() > 0) {
                person.animFlag(Person.FLAG_WALKING);
                person.orientation = vec.angleRad();
                person.move(vec);
            } else {
                person.animUnflag(Person.FLAG_WALKING);
                person.stop();
            }

            VPool.claim(vec);
        }
    }
}
