package com.coffeebland.cossinlette3.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.coffeebland.cossinlette3.game.entity.Actor;
import com.coffeebland.cossinlette3.utils.Const;
import com.coffeebland.cossinlette3.utils.VPool;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GameWorld {
    @NotNull public final World box2D;
    @NotNull public final List<Actor> actors;
    @NotNull protected Comparator<Actor> comparator;
    @NotNull public final GameCamera camera;
    @NotNull public final Color backgroundColor;
    @NotNull public final Box2DDebugRenderer debugRenderer;

    public GameWorld() {
        box2D = new World(VPool.V2(), false);
        actors = new ArrayList<>();
        comparator = (lhs, rhs) -> Float.compare(lhs.getPriority(), rhs.getPriority());
        camera = new GameCamera();
        backgroundColor = Color.BLACK.cpy();
        debugRenderer = new Box2DDebugRenderer();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

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
        for (Actor actor : actors) {
            actor.dispose();
        }
    }
}
