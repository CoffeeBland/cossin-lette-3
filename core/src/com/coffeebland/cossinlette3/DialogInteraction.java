package com.coffeebland.cossinlette3;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.game.entity.Actor;
import com.coffeebland.cossinlette3.input.UpdateableInput;
import com.coffeebland.cossinlette3.state.GameState;
import com.coffeebland.cossinlette3.utils.N;
import com.coffeebland.cossinlette3.utils.NtN;
import com.coffeebland.cossinlette3.utils.V2;

/**
 * Created by Guillaume on 2015-10-12.
 */
public abstract class DialogInteraction implements Action, InputProcessor, UpdateableInput {

    @N protected Action nextAction;
    @NtN protected Source<Actor> actorSource;
    @NtN protected Source<String> displayNameSource;

    @N protected Actor source;
    @N protected String displayName;

    public DialogInteraction(
            @N Action nextAction,
            @NtN Source<Actor> actorSource,
            @NtN Source<String> displayNameSource) {
        this.nextAction = nextAction;
        this.actorSource = actorSource;
        this.displayNameSource = displayNameSource;
    }

    @Override
    public void execute(@NtN GameState state, @NtN GameWorld world, @NtN Context ctx) {
        source = actorSource.eval(state, world, ctx);
        displayName = displayNameSource.eval(state, world, ctx);
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.ENTER:
            case Input.Keys.C:
                return true;

            default:
                return false;
        }
    }
    @Override public boolean keyUp(int keycode) { return true; }
    @Override public boolean keyTyped(char character) { return true; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }

    @Override public boolean scrolled(int amount) { return true; }

    public void render(@NtN Batch batch) {
        @NtN Vector2 size = V2.get();
        @NtN Vector2 pos = V2.get();
        renderContent(batch, pos, size);
        V2.claim(size, pos);
    }
    public abstract void renderContent(
            @NtN Batch batch,
            @NtN Vector2 pos,
            @NtN Vector2 size
    );

    @Override
    public boolean update(float delta) {
        return true;
    }

    public abstract int getWidth();
    public abstract int getHeight();
}
