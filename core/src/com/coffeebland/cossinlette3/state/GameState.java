package com.coffeebland.cossinlette3.state;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.game.MovementInput;
import com.coffeebland.cossinlette3.game.RenderInputMultiplexer;
import com.coffeebland.cossinlette3.game.entity.Person;
import com.coffeebland.cossinlette3.game.file.SaveFile;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.utils.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GameState extends StateImpl<SaveFile> {

    @Nullable protected GameWorld world;
    @Nullable protected SaveFile saveFile;
    @NotNull protected RenderInputMultiplexer input = new RenderInputMultiplexer();
    @Nullable protected MovementInput movementInput;

    protected Person player;

    public GameState() {
        setBackgroundColor(new Color(0, 0, 0, 1));
    }

    @Override public InputProcessor getInputProcessor() { return input; }
    @NotNull @Override public Color getBackgroundColor() {
        return world != null ? world.getBackgroundColor(): super.getBackgroundColor();
    }

    @Override public void onPrepare(@Nullable SaveFile file, StateManager.Notifier notifier) {
        super.onPrepare(file, notifier);

        saveFile = file != null ? file : SaveFile.getNewSaveFile();

        load(saveFile.worldFile.getPath(), WorldDef.class, wd -> {
            world = new GameWorld(eventManager, assetManager, wd, saveFile, p -> {
                player = p;
                assert world != null;
                world.getCamera().moveTo(player);
                movementInput = new MovementInput(player);
                input.addProcessor(movementInput);
            });
            eventManager.post(Tag.ASSETS, notifier::prepared);
        });
    }
    @Override public void onDispose() {
        super.onDispose();
        if (world != null) world.dispose();
    }

    @Override public void resize(int width, int height) {
        if (world != null) world.resize(width, height);
    }

    @Override public void render(@NotNull Batch batch) {
        assert world != null;
        world.render(batch);
    }
    @Override public void update(float delta) {
        super.update(delta);
        input.update(delta);
        assert world != null; world.update(delta);
    }
}
