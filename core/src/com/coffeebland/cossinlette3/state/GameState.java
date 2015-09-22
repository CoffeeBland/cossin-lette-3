package com.coffeebland.cossinlette3.state;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.game.PlayerInput;
import com.coffeebland.cossinlette3.game.entity.Person;
import com.coffeebland.cossinlette3.game.file.PersonDef;
import com.coffeebland.cossinlette3.game.file.SaveFile;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GameState extends State<SaveFile> {

    @Nullable protected GameWorld world;
    @Nullable protected SaveFile saveFile;
    @Nullable protected PlayerInput playerInput;

    protected Person player;

    public GameState() {
        setBackgroundColor(new Color(0, 0, 0, 1));
    }

    @Override public boolean shouldBeReused() {
        return false;
    }

    @Override public InputProcessor getInputProcessor() {
        return playerInput;
    }

    @Override public void onTransitionInStart(boolean firstTransition, @Nullable SaveFile saveFile) {
        if (saveFile == null) {
            saveFile = SaveFile.getNewSaveFile();
        }

        WorldDef worldDef = WorldDef.read(saveFile.worldFile);

        world = new GameWorld(worldDef, saveFile);
        setBackgroundColor(world.getBackgroundColor());

        PersonDef def = new PersonDef();
        def.radius = 0.4f;
        def.speed = 4f;
        def.density = 1f;
        def.charset = "cossin";
        player = new Person(def, world.getCharsetAtlas());
        assert world != null;
        player.addToWorld(world);
        world.camera.moveTo(player);

        playerInput = new PlayerInput(player);
    }
    @Override public void onTransitionOutFinish() {
        if (world != null) world.dispose();
    }

    @Override
    public void resize(int width, int height) {
        if (world != null) world.resize(width, height);
    }

    @Override
    public void render(@NotNull SpriteBatch batch) {
        assert world != null;
        world.render(batch);
    }

    @Override
    public void update(float delta) {
        assert playerInput != null && world != null;

        playerInput.update(delta);
        world.update(delta);
    }
}
