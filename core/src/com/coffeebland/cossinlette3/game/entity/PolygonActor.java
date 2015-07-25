package com.coffeebland.cossinlette3.game.entity;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.game.file.PolygonDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PolygonActor extends Actor {

    @NotNull protected PolygonDef def;
    @Nullable protected Body body;

    public PolygonActor(@NotNull PolygonDef def) {
        super(def);
        this.def = def;
    }

    @Override public void addToWorld(@NotNull GameWorld world) {
        super.addToWorld(world);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(def.x, def.y);

        body = world.box2D.createBody(bodyDef);
        assert body != null;

        PolygonShape shape = new PolygonShape();
        shape.set(def.points);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 0;
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef);
    }

    @Override public void removeFromWorld() {
        assert world != null && body != null;

        world.box2D.destroyBody(body);
        body = null;

        super.removeFromWorld();
    }

    @Override public void dispose() {}
}
