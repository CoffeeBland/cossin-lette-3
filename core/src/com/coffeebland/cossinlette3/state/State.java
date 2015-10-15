package com.coffeebland.cossinlette3.state;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Guillaume on 2015-10-08.
 */
public interface State<StateArgs> extends InputProcessor {

    void setStateManager(@Nullable StateManager stateManager);
    @NotNull Color getBackgroundColor();

    default boolean shouldBeReused() { return false; }
    void render(@NotNull Batch batch);
    void update(float delta);
    default void resize(int width, int height) {}

    default void onPrepare(@Nullable StateArgs args, StateManager.Notifier notifier) { notifier.prepared(); }
    default void onPrepareUpdate(float delta) {}
    default void onTransitionInStart() {}
    default void onTransitionInFinish() {}
    default void onTransitionOutStart() {}
    default void onTransitionOutFinish() {}
    default void onDispose() {}

    @Nullable default InputProcessor getInputProcessor() { return this; }
    @Override default boolean keyDown(int keycode) { return false; }
    @Override default boolean keyUp(int keycode) { return false; }
    @Override default boolean keyTyped(char character) { return false; }
    @Override default boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override default boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override default boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override default boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override default boolean scrolled(int amount) { return false; }
}
