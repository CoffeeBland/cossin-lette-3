package com.coffeebland.cossinlette3.game.entity;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.game.file.PolygonDef;
import com.coffeebland.cossinlette3.utils.N;
import com.coffeebland.cossinlette3.utils.NtN;

import java.util.Iterator;

public class PolygonActor extends Actor {

    @N protected Body body;

    @NtN BodyDef bodyDef;
    @NtN FixtureDef fixtureDef;

    public PolygonActor(@NtN PolygonDef def) {
        super(def);

        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(def.x, def.y);

        ChainShape shape = new ChainShape();
        shape.createChain(def.points);

        fixtureDef = new FixtureDef();
        fixtureDef.density = 0;
        fixtureDef.shape = shape;
    }

    @Override public void addToWorld(@NtN GameWorld world) {
        super.addToWorld(world);

        body = world.getBox2D().createBody(bodyDef);
        body.createFixture(fixtureDef);
    }

    @Override public void removeFromWorld(Iterator<Actor> iterator) {
        assert world != null && body != null;

        world.getBox2D().destroyBody(body);
        body = null;

        super.removeFromWorld(iterator);
    }
}
