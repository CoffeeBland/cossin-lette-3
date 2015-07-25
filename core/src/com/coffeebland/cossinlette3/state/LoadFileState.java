package com.coffeebland.cossinlette3.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.coffeebland.cossinlette3.game.file.SaveFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LoadFileState extends State<Void> {

    protected FileHandle[] saveFiles = {};

    @Override
    public boolean shouldBeReused() {
        return true;
    }

    @Override
    public void onTransitionInStart(boolean firstTransition, @Nullable Void aVoid) {
        saveFiles = Gdx.files.local(SaveFile.SAVE_FOLDER).list();
    }

    @Override
    public void onTransitionInFinish() {
        SaveFile file = SaveFile.read(saveFiles[0]);
        new StateManager.TransitionArgs<>(GameState.class)
                .setLength(TRANSITION_SHORT, TRANSITION_LONG)
                .setColor(Color.BLACK)
                .setArgs(file)
                .beginSwitch(stateManager);
    }

    @Override
    public void render(@NotNull SpriteBatch batch) {

    }

    @Override
    public void update(float delta) {

    }
}
