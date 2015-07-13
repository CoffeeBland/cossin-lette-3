package com.coffeebland.cossinlette3.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PolygonActor extends Actor {

    @NotNull
    protected WorldFile.PolygonDef def;
    @Nullable
    protected Body body;

    public PolygonActor(@NotNull WorldFile.PolygonDef def) {
        this.def = def;
    }

    @Override
    public void addToWorld(@NotNull GameWorld world) {
        super.addToWorld(world);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(def.x, def.y);

        body = world.box2D.createBody(bodyDef);
        assert body != null;

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(def.points);

        body.createFixture(polygonShape, 0.0f);
    }

    @Override
    public void removeFromWorld() {
        assert world != null && body != null;

        world.box2D.destroyBody(body);
        body = null;

        super.removeFromWorld();
    }
}
