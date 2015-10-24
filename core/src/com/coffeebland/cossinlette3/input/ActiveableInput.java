package com.coffeebland.cossinlette3.input;

import com.badlogic.gdx.InputProcessor;

/**
 * Created by Guillaume on 2015-09-28.
 */
public abstract class ActiveableInput implements InputProcessor, UpdateableInput {

    protected boolean enabled = true;

    public void enable() { enabled = true; }
    public void disable() { enabled = false; }
    public boolean isEnabled() { return enabled; }

    @Override public final boolean keyDown(int keycode) {
        return isEnabled() && handleKeyDown(keycode);
    }
    @Override public final boolean keyUp(int keycode) {
        return isEnabled() && handleKeyUp(keycode);
    }
    @Override public final boolean keyTyped(char character) {
        return isEnabled() && handleKeyTyped(character);
    }
    @Override public final boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return isEnabled() && handleTouchDown(screenX, screenY, pointer, button);
    }
    @Override public final boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return isEnabled() && handleTouchUp(screenX, screenY, pointer, button);
    }
    @Override public final boolean touchDragged(int screenX, int screenY, int pointer) {
        return isEnabled() && handleTouchDragged(screenX, screenY, pointer);
    }
    @Override public final boolean mouseMoved(int screenX, int screenY) {
        return isEnabled() && handleMouseMoved(screenX, screenY);
    }
    @Override public final boolean scrolled(int amount) {
        return isEnabled() && handleScrolled(amount);
    }
    @Override public final boolean updateInput(float delta) {
        return isEnabled() && handleUpdateInput(delta);
    }

    public boolean handleKeyDown(int keycode) { return false; }
    public boolean handleKeyUp(int keycode) { return false; }
    public boolean handleKeyTyped(char character) { return false; }
    public boolean handleTouchDown(int screenX, int screenY, int pointer, int button) { return false; }
    public boolean handleTouchUp(int screenX, int screenY, int pointer, int button) { return false; }
    public boolean handleTouchDragged(int screenX, int screenY, int pointer) { return false; }
    public boolean handleMouseMoved(int screenX, int screenY) { return false; }
    public boolean handleScrolled(int amount) { return false; }
    public boolean handleUpdateInput(float delta) { return false; }
}