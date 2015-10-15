package com.coffeebland.cossinlette3.game.script;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.game.entity.Actor;
import com.coffeebland.cossinlette3.input.UpdateableInput;
import com.coffeebland.cossinlette3.state.GameState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Guillaume on 2015-10-12.
 */
public class DialogInteraction implements Action, InputProcessor, UpdateableInput {

    @Nullable protected String sourceName;
    @Nullable protected Actor source;
    @NotNull protected String sourceDisplayName;
    @Nullable protected Action nextAction;

    public DialogInteraction(@Nullable Action nextAction, @Nullable String sourceName, @Nullable String sourceDisplayName) {
        this.nextAction = nextAction;
        this.sourceName = sourceName;
        this.sourceDisplayName = sourceDisplayName == null ? "?" : sourceDisplayName;
    }
    public DialogInteraction() {
        this(null, null, null);
    }

    @Override
    public void execute(@NotNull GameState gameState, @NotNull GameWorld gameWorld) {
        source = sourceName != null ? gameWorld.getNamed(sourceName).stream().findFirst().orElse(null) : null;
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

    public void render(@NotNull Batch batch) {

    }
    @Override
    public boolean update(float delta) {
        return true;
    }
}
