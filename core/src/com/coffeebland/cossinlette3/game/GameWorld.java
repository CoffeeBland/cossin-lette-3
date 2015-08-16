package com.coffeebland.cossinlette3.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.coffeebland.cossinlette3.game.entity.Actor;
import com.coffeebland.cossinlette3.game.entity.PolygonActor;
import com.coffeebland.cossinlette3.game.entity.TileLayer;
import com.coffeebland.cossinlette3.game.entity.Tileset;
import com.coffeebland.cossinlette3.game.file.*;
import com.coffeebland.cossinlette3.utils.Const;
import com.coffeebland.cossinlette3.utils.VPool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GameWorld {
    @NotNull public final World box2D;
    @NotNull public final List<Actor> actors;
    @NotNull protected Comparator<Actor> comparator;
    @NotNull public final GameCamera camera;
    @NotNull public final Box2DDebugRenderer debugRenderer;

    @NotNull protected WorldDef def;
    @Nullable protected TextureAtlas atlas;
    @Nullable protected Tileset tileset;

    public GameWorld(@NotNull WorldDef def, @Nullable SaveFile saveFile) {
        this.def = def;

        box2D = new World(VPool.V2(), false);
        actors = new ArrayList<>();
        comparator = (lhs, rhs) -> Float.compare(lhs.getPriority(), rhs.getPriority());
        camera = new GameCamera();
        debugRenderer = new Box2DDebugRenderer();

        for (PolygonDef polyDef: def.staticPolygons) {
            PolygonActor poly = new PolygonActor(polyDef);
            poly.addToWorld(this);
        }

        for (TileLayerDef tileLayerDef : def.tileLayers) {
            TileLayer layer = new TileLayer(tileLayerDef, getTileset());
            layer.addToWorld(this);
        }

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @NotNull public TextureAtlas getAtlas() {
        if (atlas == null) {
            FileHandle fileHandle = Gdx.files.internal("img/game/" + def.imgSrc + ".atlas");
            atlas = new TextureAtlas(fileHandle);
        }
        return atlas;
    }
    @NotNull public Tileset getTileset() {
        if (tileset == null) {
            FileHandle fileHandle = Gdx.files.internal("img/game/" + def.imgSrc + ".tileset.json");
            TilesetDef tilesetDef = new Json().fromJson(TilesetDef.class, fileHandle);
            tileset = new Tileset(getAtlas(), tilesetDef);
        }
        return tileset;
    }

    public int getWidth() { return def.width; }
    public int getHeight() { return def.height; }
    public Color getBackgroundColor() { return def.backgroundColor; }

    public void resize(int width, int height) {
        camera.updateToSize(width, height);
    }

    public void render(@NotNull SpriteBatch batch) {

        int hW = Gdx.graphics.getWidth() / 2;
        int hH = Gdx.graphics.getHeight() / 2;
        batch.getTransformMatrix().translate(hW, hH, 0);
        batch.begin();

        Collections.sort(actors, comparator);
        for (Actor actor : actors) {
            actor.render(batch, camera);
        }

        batch.end();
        batch.getTransformMatrix().translate(-hW, -hH, 0);

        debugRenderer.render(box2D, camera.underlyingCamera().combined);
    }

    public void update(float delta) {

        // The delta is in millis and box2d expects seconds
        box2D.step(delta / 1000f, Const.VELOCITY_ITERATIONS, Const.POSITION_ITERATIONS);

        for (Iterator<Actor> iterator = actors.iterator(); iterator.hasNext(); ) {
            Actor actor = iterator.next();

            if (actor.shouldBeRemovedFromActors()) {
                actor.removeFromWorld();
                iterator.remove();
            } else {
                actor.update(delta);
            }
        }

        camera.update(delta);
    }

    public void dispose() {
        actors.forEach(Actor::dispose);
    }
}
