package com.coffeebland.cossinlette3.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Json;
import com.coffeebland.cossinlette3.game.entity.*;
import com.coffeebland.cossinlette3.game.file.PersonDef;
import com.coffeebland.cossinlette3.game.file.SaveFile;
import com.coffeebland.cossinlette3.game.file.TilesetDef;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.utils.*;
import com.coffeebland.cossinlette3.utils.event.EventManager;
import com.coffeebland.cossinlette3.utils.event.Tag;

import java.util.*;

public class GameWorld {

    public static boolean RENDER_DEBUG = false;

    @NtN protected EventManager eventManager;
    @NtN protected AssetManager assetManager;

    @N protected CharsetAtlas charsetAtlas;
    @N protected Tileset tileset;

    protected int width, height;
    @NtN protected Color backgroundColor;

    @NtN protected final World box2D;
    @NtN protected final GameCamera camera;
    @NtN protected final Box2DDebugRenderer debugRenderer;

    @N protected Person player;
    @NtN protected final List<Actor> actors;
    @NtN protected final Map<String, List<Actor>> namedActors;
    @NtN protected Comparator<Actor> comparator;

    public GameWorld(
            @NtN EventManager eventManager,
            @NtN AssetManager assetManager,
            @NtN WorldDef def,
            @NtN SaveFile saveFile,
            @NtN PlayerCreationListener listener
    ) {
        this.eventManager = eventManager;
        this.assetManager = assetManager;
        this.backgroundColor = def.backgroundColor;
        this.width = def.width;
        this.height = def.height;

        box2D = new World(V2.get(), false);
        actors = new ArrayList<>();
        namedActors = new HashMap<>();
        comparator = (lhs, rhs) -> Float.compare(lhs.getPriority(), rhs.getPriority());
        camera = new GameCamera(this);
        debugRenderer = new Box2DDebugRenderer();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        loadAssets(def);
        eventManager.post(Tag.ASSETS, () -> {
            createObjects(def, saveFile);
            assert getPlayer() != null;
            listener.onPlayerCreated(getPlayer());
        });
    }

    protected void loadAssets(@NtN WorldDef def) {
        String tilesetAtlasPath = "img/game/" + def.imgSrc + ".atlas";
        String tilesetDefPath = "img/game/" + def.imgSrc + ".tileset.json";
        String charsetAtlasPath = "img/game/charset.atlas";
        eventManager.cancelTag(Tag.ASSETS);
        assetManager.load(tilesetAtlasPath, TextureAtlas.class);
        assetManager.load(tilesetDefPath, TilesetDef.class);
        assetManager.load(charsetAtlasPath, CharsetAtlas.class);
        eventManager.post(Tag.ASSETS, () -> {
            TextureAtlas tilesetAtlas = assetManager.get(tilesetAtlasPath, TextureAtlas.class);
            charsetAtlas = assetManager.get(charsetAtlasPath, CharsetAtlas.class);

            TilesetDef tilesetDef = assetManager.get(tilesetDefPath, TilesetDef.class);
            tileset = new Tileset(tilesetAtlas, tilesetDef);
        });
    }
    protected void createObjects(@NtN WorldDef def, @NtN SaveFile saveFile) {
        assert tileset != null;
        assert charsetAtlas != null;

        // Bounding map
        BodyDef boundDef = new BodyDef();
        boundDef.type = BodyDef.BodyType.StaticBody;
        Body bound = box2D.createBody(boundDef);
        ChainShape chainShape = new ChainShape();
        Vector2[] chain = new Vector2[] {
                V2.get(0, 0),
                V2.get(getWidth(), 0),
                V2.get(getWidth(), getHeight()),
                V2.get(0, getHeight()),
                V2.get(0, 0)
        };
        chainShape.createChain(chain);
        bound.createFixture(chainShape, 0);
        V2.claim(chain);

        def.staticPolygons.stream()
                .map(PolygonActor::new)
                .forEach(pa -> pa.addToWorld(this));
        def.tileLayers.stream()
                .map(tld -> new TileLayer(tld, tileset))
                .forEach(tl -> tl.addToWorld(this));
        def.people.stream()
                .map(pd -> new Person(pd, charsetAtlas))
                .forEach(p -> p.addToWorld(this));

        // Create the player
        PersonDef playerDef = new Json().fromJson(PersonDef.class, Gdx.files.internal("misc/player.def.json"));
        playerDef.x = saveFile.x;
        playerDef.y = saveFile.y;
        player = new Person(playerDef, charsetAtlas);
        player.addToWorld(this);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    @NtN public Color getBackgroundColor() { return backgroundColor; }

    @NtN public World getBox2D() { return box2D; }
    @NtN public GameCamera getCamera() { return camera; }
    @N public Person getPlayer() { return player; }
    @NtN public List<Actor> getActors() { return actors; }
    @NtN public List<Actor> getNamed(@NtN String name) {
        if (namedActors.containsKey(name)) return namedActors.get(name);
        namedActors.put(name, new ArrayList<>());
        return getNamed(name);
    }

    public void resize(int width, int height) {
        camera.updateToSize(width, height);
    }
    public void render(@NtN Batch batch) {

        int hW = Gdx.graphics.getWidth() / 2;
        int hH = Gdx.graphics.getHeight() / 2;
        batch.getTransformMatrix().translate(hW, hH, 0);
        batch.begin();

        Collections.sort(actors, comparator);
        for (Actor actor : actors) actor.render(batch, camera);

        batch.end();
        batch.getTransformMatrix().translate(-hW, -hH, 0);

        if (RENDER_DEBUG) debugRenderer.render(box2D, camera.underlyingCamera().combined);
    }
    public void update(float delta) {

        // The delta is in millis and box2d expects seconds
        box2D.step(delta / 1000f, Const.VELOCITY_ITERATIONS, Const.POSITION_ITERATIONS);

        for (Iterator<Actor> iterator = actors.iterator(); iterator.hasNext(); ) {
            Actor actor = iterator.next();

            if (actor.shouldBeRemovedFromActors()) actor.removeFromWorld(iterator);
            else actor.update(delta);
        }

        camera.update(delta);
    }
    public void dispose() {
        box2D.dispose();
    }

    public interface PlayerCreationListener {
        void onPlayerCreated(@NtN Person player);
    }
}
