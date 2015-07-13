package com.coffeebland.cossinlette3.state;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class State<StateArgs> implements InputProcessor {
    public static final float TRANSITION_SHORT = 250;
    public static final float TRANSITION_MEDIUM = 500;
    public static final float TRANSITION_LONG = 1000;

    public State() {}

    protected StateManager stateManager;
    protected Color backgroundColor;

    public void setStateManager(@Nullable StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @NotNull public Color getBackgroundColor() {
        if (backgroundColor == null) {
            backgroundColor = Color.BLACK.cpy();
        }
        return backgroundColor;
    }
    public void setBackgroundColor(@NotNull Color color) {
        this.backgroundColor = color;
    }

    public abstract boolean shouldBeReused();
    public abstract void render(@NotNull SpriteBatch batch);
    public abstract void update(float delta);
    public void resize(int width, int height) {}

    public void onTransitionInStart(boolean firstTransition, @Nullable StateArgs args) {}
    public void onTransitionInFinish() {}
    public void onTransitionOutStart() {}
    public void onTransitionOutFinish() {}

    @Nullable public InputProcessor getInputProcessor() { return this; }
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(int amount) { return false; }
}
