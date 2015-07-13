package com.coffeebland.cossinlette3.state;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.coffeebland.cossinlette3.game.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.BitSet;

import static java.lang.Math.PI;

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

        WorldFile worldFile = WorldFile.read(saveFile.worldFile);
        world = worldFile.createGameWorld(saveFile);
        setBackgroundColor(world.getBackgroundColor());

        player = new Person(new WorldFile.PersonDef(0.5f, 1f, 0.01f, 0, 5));
        assert world != null;
        player.addToWorld(world);
        world.getCamera().moveTo(player);

        player.imageStrips.resolvers.add(new Person.ImageStripResolver(0,
                new Person.ImageStrip("sprites/still.png", 64, 80, 32, 10, 12, Arrays.asList(
                        new Person.OrientationFrames(0, -7.0/8.0 * PI, -5.0/8.0 * PI),
                        new Person.OrientationFrames(1, -5.0/8.0 * PI, -3.0/8.0 * PI),
                        new Person.OrientationFrames(2, -3.0/8.0 * PI, -1.0/8.0 * PI),
                        new Person.OrientationFrames(3, -1.0/8.0 * PI, 1.0/8.0 * PI),
                        new Person.OrientationFrames(4, 1.0/8.0 * PI, 3.0/8.0 * PI),
                        new Person.OrientationFrames(5, 3.0/8.0 * PI, 5.0/8.0 * PI),
                        new Person.OrientationFrames(6, 5.0/8.0 * PI, 7.0/8.0 * PI),
                        new Person.OrientationFrames(7, 7.0/8.0 * PI, 9.0/8.0 * PI)
                ))
        ) {
            @Override public boolean conditionsMet(@NotNull BitSet flags) {
                return true;
            }
        });
        player.imageStrips.resolvers.add(new Person.ImageStripResolver(1,
                new Person.ImageStrip("sprites/walk.png", 64, 80, 32, 10, 12, Arrays.asList(
                        new Person.OrientationFrames(0, -7.0/8.0 * PI, -5.0/8.0 * PI),
                        new Person.OrientationFrames(1, -5.0/8.0 * PI, -3.0/8.0 * PI),
                        new Person.OrientationFrames(2, -3.0/8.0 * PI, -1.0/8.0 * PI),
                        new Person.OrientationFrames(3, -1.0/8.0 * PI, 1.0/8.0 * PI),
                        new Person.OrientationFrames(4, 1.0/8.0 * PI, 3.0/8.0 * PI),
                        new Person.OrientationFrames(5, 3.0/8.0 * PI, 5.0/8.0 * PI),
                        new Person.OrientationFrames(6, 5.0/8.0 * PI, 7.0/8.0 * PI),
                        new Person.OrientationFrames(7, 7.0/8.0 * PI, 9.0/8.0 * PI)
                ))
        ) {
            @Override public boolean conditionsMet(@NotNull BitSet flags) {
                return flags.get(Person.FLAG_WALKING);
            }
        });
        player.resolveImageStrips();

        playerInput = new PlayerInput(player);
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
