package com.coffeebland.cossinlette3.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.coffeebland.cossinlette3.game.GameUI;
import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.game.MovementInput;
import com.coffeebland.cossinlette3.game.UpdateInputMultiplexer;
import com.coffeebland.cossinlette3.game.entity.Person;
import com.coffeebland.cossinlette3.game.file.SaveFile;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.utils.N;
import com.coffeebland.cossinlette3.utils.NtN;
import com.coffeebland.cossinlette3.utils.event.Tag;

public class GameState extends StateImpl<SaveFile> {

    @N protected SaveFile saveFile;
    @NtN protected Preferences prefs;

    @NtN protected UpdateInputMultiplexer input = new UpdateInputMultiplexer();

    @N protected GameWorld world;
    @N protected Person player;
    @N protected MovementInput movementInput;

    @N protected GameUI ui;

    public GameState() {
        setBackgroundColor(new Color(0, 0, 0, 1));
        prefs = Gdx.app.getPreferences("main");
    }

    @Override public InputProcessor getInputProcessor() { return input; }
    @NtN @Override public Color getBackgroundColor() {
        return world != null ? world.getBackgroundColor(): super.getBackgroundColor();
    }

    @Override public void onPrepare(@N SaveFile file, StateManager.Notifier notifier) {
        super.onPrepare(file, notifier);

        saveFile = file != null ? file : SaveFile.getNewSaveFile();

        load(saveFile.worldFile.getPath(), WorldDef.class, wd -> {
            ui = new GameUI(eventManager, assetManager);
            input.addProcessor(ui);

            world = new GameWorld(eventManager, assetManager, wd, saveFile, p -> {
                assert world != null;
                player = p;
                world.getCamera().moveTo(player);
                movementInput = new MovementInput(player);
                input.addProcessor(movementInput);
            });

            eventManager.post(Tag.ASSETS, notifier::prepared);
        });
    }

    @Override
    public void onTransitionInFinish() {
        super.onTransitionInFinish();
        eventManager.post(500, () -> { assert ui != null; ui.speak(); });
    }

    @Override public void resize(int width, int height) {
        if (world != null) world.resize(width, height);
        if (ui != null) ui.resize(width, height);
    }
    @Override public void render(@NtN Batch batch) {
        if (world != null) world.render(batch);
        if (ui != null) ui.render(batch);
    }
    @Override public void update(float delta) {
        super.update(delta);
        input.updateInput(delta);
        if (world != null) world.update(delta);
        if (ui != null) ui.update(delta);
    }
    @Override public void onDispose() {
        super.onDispose();
        if (world != null) world.dispose();
        if (ui != null) ui.dispose();
    }
}
