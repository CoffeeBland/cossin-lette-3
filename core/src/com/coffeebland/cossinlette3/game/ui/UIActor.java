package com.coffeebland.cossinlette3.game.ui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.coffeebland.cossinlette3.game.GameUI;
import com.coffeebland.cossinlette3.input.UpdateableInput;
import com.coffeebland.cossinlette3.utils.N;
import com.coffeebland.cossinlette3.utils.NtN;

import java.util.Iterator;

/**
 * Created by Guillaume on 2015-10-22.
 */
public abstract class UIActor implements InputProcessor, UpdateableInput {

    public static final float
            PRIORITY_DIALOG = 5;

    protected boolean shouldBeRemoved;
    @N protected GameUI ui;
    protected float priority;

    public UIActor(float priority) {
        this.priority = priority;
    }

    public float getPriority() { return priority; }
    public void setPriority(float priority) {
        this.priority = priority;
    }

    public void addToUI(@NtN GameUI ui) {
        assert this.ui == null;
        shouldBeRemoved = false;
        this.ui = ui;
        ui.getActors().add(this);
    }
    public void removeFromUI(Iterator<UIActor> iterator) {
        this.ui = null;
        iterator.remove();
    }

    public void flagForRemoval() { shouldBeRemoved = true; }
    public boolean shouldBeRemovedFromActors() { return shouldBeRemoved; }

    public void render(@NtN Batch batch) { }
    public void update(float delta) {}
    @Override public boolean updateInput(float delta) { return false; }
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(int amount) { return false; }
}
