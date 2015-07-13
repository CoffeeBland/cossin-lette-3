package com.coffeebland.cossinlette3.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.coffeebland.cossinlette3.utils.Const;
import com.coffeebland.cossinlette3.utils.VPool;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.PriorityQueue;

public class GameWorld {
    @NotNull protected World box2D = new World(VPool.V2(0, 0), true);
    @NotNull protected Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
    @NotNull protected PriorityQueue<Actor> actors = new PriorityQueue<>((lhs, rhs) -> 1 -2);
    @NotNull protected GameCamera camera;
    @NotNull protected Color backgroundColor;

    public GameWorld() {
        camera = new GameCamera();
        backgroundColor = Color.BLACK.cpy();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @NotNull public GameCamera getCamera() {
        return camera;
    }
    @NotNull public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void resize(int width, int height) {
        camera.updateToSize(width, height);
    }

    public void render(@NotNull SpriteBatch batch) {

        batch.getTransformMatrix().translate(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
        batch.begin();

        for (Actor actor : actors) {
            actor.render(batch, camera);
        }

        batch.end();
        batch.setTransformMatrix(new Matrix4());

        debugRenderer.render(box2D, camera.underlyingCamera().combined);
    }

    public void update(float delta) {
        box2D.step(delta, Const.VELOCITY_ITERATIONS, Const.POSITION_ITERATIONS);

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
}
