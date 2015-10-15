package com.coffeebland.cossinlette3.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.coffeebland.cossinlette3.game.file.SaveFile;
import com.coffeebland.cossinlette3.utils.N;
import com.coffeebland.cossinlette3.utils.NtN;

public class LoadFileState extends StateImpl<Void> {

    protected FileHandle[] saveFiles = {};

    @Override
    public boolean shouldBeReused() {
        return true;
    }

    @Override
    public void onPrepare(@N Void nil, StateManager.Notifier notifier) {
        super.onPrepare(nil, notifier);
        saveFiles = Gdx.files.local(SaveFile.SAVE_FOLDER).list();
        notifier.prepared();
    }

    @Override
    public void onTransitionInFinish() {
        super.onTransitionInFinish();
        SaveFile file = SaveFile.read(saveFiles[0]);
        new StateManager.TransitionArgs<>(GameState.class)
                .setLength(TRANSITION_SHORT, TRANSITION_LONG)
                .setColor(Color.BLACK)
                .setArgs(file)
                .beginSwitch(stateManager);
    }

    @Override
    public void render(@NtN Batch batch) {

    }
}
