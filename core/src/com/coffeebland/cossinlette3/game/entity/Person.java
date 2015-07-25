package com.coffeebland.cossinlette3.game.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.coffeebland.cossinlette3.game.GameCamera;
import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.game.file.PersonDef;
import com.coffeebland.cossinlette3.game.visual.ImageStrips;
import com.coffeebland.cossinlette3.utils.Const;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;

public class Person extends Actor implements GameCamera.PositionSource {

    public static final int FLAG_WALKING = 0;

    @Nullable protected Body body;
    @NotNull protected final BitSet flags = new BitSet();
    @NotNull public final PersonDef def;
    @NotNull public final ImageStrips imageStrips = new ImageStrips();

    public float speed;
    public float orientation;

    public Person(@NotNull PersonDef def) {
        super(def);
        this.def = def;
        this.speed = def.speed;
    }

    @Nullable @Override public Vector2 getPosition() {
        return body != null ? body.getPosition() : null;
    }

    @Override public void addToWorld(@NotNull GameWorld world) {
        super.addToWorld(world);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.position.set(def.x, def.y);

        body = world.box2D.createBody(bodyDef);
        assert body != null;

        CircleShape shape = new CircleShape();
        shape.setRadius(def.radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = def.density;
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef);
    }
    @Override public void removeFromWorld() {
        assert world != null && body != null;

        world.box2D.destroyBody(body);

        super.removeFromWorld();
    }

    public void animFlag(int flag) {
        if (!flags.get(flag)) {
            flags.set(flag);
            imageStrips.resolve(flags);
        }
    }
    public void animUnflag(int flag) {
        if (flags.get(flag)) {
            flags.clear(flag);
            imageStrips.resolve(flags);
        }
    }
    public void resolveImageStrips() {
        imageStrips.resolve(flags);
    }

    /**
     * Applies a force of length equal to the person's speed using the orientation vector as a base;
     * this actually modifies the orientation vector (the length, not the orientation), also,
     * whoever called this should take care of claiming the vector
     */
    public void move(Vector2 orientationVector) {
        if (body != null) {
            body.setLinearVelocity(orientationVector.nor().scl(speed));
        }
    }
    public void stop() {
        if (body != null) {
            body.setLinearVelocity(Vector2.Zero);
        }
    }

    @Override public void render(@NotNull SpriteBatch batch, @NotNull GameCamera camera) {
        Vector2 pos = getPosition();
        if (pos != null) {
            imageStrips.render(batch,
                    (pos.x - camera.getPos().x) / Const.METERS_PER_PIXEL,
                    (pos.y - camera.getPos().y) / Const.METERS_PER_PIXEL,
                    orientation, 1);
        }
    }

    @Override public void update(float delta) {
        imageStrips.update(delta);
    }

}
