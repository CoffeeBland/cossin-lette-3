package com.coffeebland.cossinlette3.state;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.coffeebland.cossinlette3.game.*;
import com.coffeebland.cossinlette3.game.entity.Person;
import com.coffeebland.cossinlette3.game.file.PersonDef;
import com.coffeebland.cossinlette3.game.file.SaveFile;
import com.coffeebland.cossinlette3.game.file.WorldFile;
import com.coffeebland.cossinlette3.game.visual.ImageStrip;
import com.coffeebland.cossinlette3.game.visual.ImageStripResolver;
import com.coffeebland.cossinlette3.game.visual.OrientationFrame;
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
        setBackgroundColor(world.backgroundColor);

        PersonDef def = new PersonDef();
        def.radius = 0.40f;
        def.x = 0;
        def.y = 5;
        def.speed = 3f;
        def.headHeight = 1f;
        def.density = 1f;
        player = new Person(def);
        assert world != null;
        player.addToWorld(world);
        world.camera.moveTo(player);

        player.imageStrips.resolvers.add(new ImageStripResolver(0,
                new ImageStrip("sprites/charset_neutre.png", 64, 96, 32, 20, 4, Arrays.asList(
                        new OrientationFrame(1, true, -7.0 / 8.0 * PI, -5.0 / 8.0 * PI),
                        new OrientationFrame(0, false, -5.0 / 8.0 * PI, -3.0 / 8.0 * PI),
                        new OrientationFrame(1, false, -3.0 / 8.0 * PI, -1.0 / 8.0 * PI),
                        new OrientationFrame(2, false, -1.0 / 8.0 * PI, 1.0 / 8.0 * PI),
                        new OrientationFrame(3, false, 1.0 / 8.0 * PI, 3.0 / 8.0 * PI),
                        new OrientationFrame(4, false, 3.0 / 8.0 * PI, 5.0 / 8.0 * PI),
                        new OrientationFrame(3, true, 5.0 / 8.0 * PI, 7.0 / 8.0 * PI),
                        new OrientationFrame(2, true, 7.0 / 8.0 * PI, 9.0 / 8.0 * PI)
                ))
        ) {
            @Override public boolean conditionsMet(@NotNull BitSet flags) {
                return true;
            }
        });
        player.imageStrips.resolvers.add(new ImageStripResolver(1,
                new ImageStrip("sprites/charset_marche.png", 64, 96, 32, 20, 10, Arrays.asList(
                        new OrientationFrame(1, true, -7.0 / 8.0 * PI, -5.0 / 8.0 * PI),
                        new OrientationFrame(0, false, -5.0 / 8.0 * PI, -3.0 / 8.0 * PI),
                        new OrientationFrame(1, false, -3.0 / 8.0 * PI, -1.0 / 8.0 * PI),
                        new OrientationFrame(2, false, -1.0 / 8.0 * PI, 1.0 / 8.0 * PI),
                        new OrientationFrame(3, false, 1.0 / 8.0 * PI, 3.0 / 8.0 * PI),
                        new OrientationFrame(4, false, 3.0 / 8.0 * PI, 5.0 / 8.0 * PI),
                        new OrientationFrame(3, true, 5.0 / 8.0 * PI, 7.0 / 8.0 * PI),
                        new OrientationFrame(2, true, 7.0 / 8.0 * PI, 9.0 / 8.0 * PI)
                ))
        ) {
            @Override public boolean conditionsMet(@NotNull BitSet flags) {
                return flags.get(Person.FLAG_WALKING);
            }
        });
        player.resolveImageStrips();

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
