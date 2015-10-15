package com.coffeebland.cossinlette3.game.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.coffeebland.cossinlette3.game.GameCamera;
import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.game.file.CharsetDef;
import com.coffeebland.cossinlette3.game.file.PersonDef;
import com.coffeebland.cossinlette3.game.visual.ImageStrips;
import com.coffeebland.cossinlette3.utils.CharsetAtlas;
import com.coffeebland.cossinlette3.utils.Const;
import com.coffeebland.cossinlette3.utils.V2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.Iterator;

public class Person extends Actor implements GameCamera.PositionSource {

    @Nullable protected Body body;
    @NotNull protected final BitSet flags = new BitSet();
    @Nullable protected ImageStrips imageStrips;
    protected float speed, speedSquared, orientation, radius;
    @Nullable protected Vector2 walking;

    @NotNull BodyDef bodyDef;
    @NotNull FixtureDef fixtureDef;

    public Person(@NotNull PersonDef def, @NotNull CharsetAtlas atlas) {
        super(def);
        this.speed = def.speed;
        this.speedSquared = speed * speed;
        this.orientation = def.orientation;

        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.position.set(def.x, def.y);

        CircleShape shape = new CircleShape();
        shape.setRadius(this.radius = def.radius);

        fixtureDef = new FixtureDef();
        fixtureDef.density = def.density;
        fixtureDef.shape = shape;

        if (def.hasCharset()) {
            assert def.charset != null;
            setImageStrips(atlas.getCharset(def.charset));
        }
    }

    @Nullable @Override public Vector2 getPosition() {
        return body != null ? body.getPosition() : null;
    }

    @Nullable public ImageStrips getImageStrips() {
        return imageStrips;
    }
    public void setImageStrips(@Nullable ImageStrips imageStrips) {
        this.imageStrips = imageStrips;
        if (imageStrips != null) imageStrips.resolve(flags);
    }

    @Override public void addToWorld(@NotNull GameWorld world) {
        super.addToWorld(world);

        body = world.getBox2D().createBody(bodyDef);
        body.createFixture(fixtureDef);
    }
    @Override public void removeFromWorld(Iterator<Actor> iterator) {
        assert world != null && body != null;

        world.getBox2D().destroyBody(body);

        super.removeFromWorld(iterator);
    }

    public void animFlag(int flag) {
        if (!flags.get(flag)) {
            flags.set(flag);
            resolveImageStrips();
        }
    }
    public void animUnflag(int flag) {
        if (flags.get(flag)) {
            flags.clear(flag);
            resolveImageStrips();
        }
    }
    public void resolveImageStrips() {
        if (imageStrips != null) imageStrips.resolve(flags);
    }

    /**
     * Applies a force of length equal to the person's speed using the orientation vector as a base;
     * this actually modifies the orientation vector (the length, not the orientation), also,
     * whoever called this should take care of claiming the vector
     *
     * note that this *will not* actually apply a force if the body has not been created
     */
    public void move(@NotNull Vector2 orientationVector) {
        orientation = orientationVector.angleRad();
        walking = (walking == null ? V2.get() : walking).set(orientationVector).nor().scl(speed);
        animFlag(CharsetDef.FLAG_WALKING);
    }
    public void stop() {
        if (walking == null) return;
        V2.claim(walking);
        walking = null;
        animUnflag(CharsetDef.FLAG_WALKING);
    }

    @Override public void render(@NotNull Batch batch, @NotNull GameCamera camera) {
        while (orientation > Math.PI) orientation -= Math.PI * 2;
        while (orientation <= -Math.PI) orientation += Math.PI * 2;
        Vector2 pos = getPosition();
        if (pos != null) {
            if (imageStrips != null) {
                imageStrips.render(batch,
                        (pos.x - camera.getPos().x) / Const.METERS_PER_PIXEL,
                        (pos.y - camera.getPos().y) / Const.METERS_PER_PIXEL,
                        orientation, 1);
            }
        }
    }

    @Override public void update(float delta) {
        if (imageStrips != null) imageStrips.update(delta);
        Vector2 pos = getPosition();
        if (pos != null) priority = -pos.y + radius / 2;
        if (body != null) {
            if (walking == null) {
                body.setLinearVelocity(Vector2.Zero);
            } else {
                body.setLinearVelocity(walking);
            }
        }
    }

}
