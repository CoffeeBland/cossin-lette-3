package com.coffeebland.cossinlette3.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.game.PlayerInput;
import com.coffeebland.cossinlette3.game.entity.Person;
import com.coffeebland.cossinlette3.game.entity.TileLayer;
import com.coffeebland.cossinlette3.game.file.PersonDef;
import com.coffeebland.cossinlette3.game.file.SaveFile;
import com.coffeebland.cossinlette3.game.file.TileLayerDef;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.game.visual.Charset;
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

        TileLayerDef tlDef = new TileLayerDef();
        tlDef.tiles = new long[worldDef.height][worldDef.width][0];
        tlDef.addTile(0, 1, TileLayer.TYPE_ANIM, 1, 0, 1);
        tlDef.addTile(1, 1, TileLayer.TYPE_ANIM, 1, 1, 1);
        tlDef.addTile(0, 2, TileLayer.TYPE_ANIM, 1, 0, 0);
        tlDef.addTile(1, 2, TileLayer.TYPE_ANIM, 1, 1, 0);
        worldDef.tileLayers.add(tlDef);

        TileLayerDef tlDef2 =new TileLayerDef();
        tlDef2.tiles = new long[worldDef.height][worldDef.width][0];
        tlDef2.priority = 1;
        tlDef2.addTile(0, 2, TileLayer.TYPE_STILL, 0, 8, 0);
        worldDef.tileLayers.add(tlDef2);

        world = new GameWorld(worldDef, saveFile);
        setBackgroundColor(world.getBackgroundColor());

        PersonDef def = new PersonDef();
        def.radius = 0.40f;
        def.x = 0;
        def.y = 5;
        def.speed = 3f;
        def.density = 1f;
        player = new Person(def);
        assert world != null;
        player.addToWorld(world);
        world.camera.moveTo(player);

        player.setImageStrips(new Charset(
                world.getAtlas(),
                Gdx.files.internal("img/game/cossin.charset.json")
        ));

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
