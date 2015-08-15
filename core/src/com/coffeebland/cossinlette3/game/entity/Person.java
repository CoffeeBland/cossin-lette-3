package com.coffeebland.cossinlette3.game.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.coffeebland.cossinlette3.game.GameCamera;
import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.game.file.CharsetDef;
import com.coffeebland.cossinlette3.game.file.PersonDef;
import com.coffeebland.cossinlette3.game.visual.FlagResolver;
import com.coffeebland.cossinlette3.game.visual.ImageStrips;
import com.coffeebland.cossinlette3.utils.Const;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;

public class Person extends Actor implements GameCamera.PositionSource {

    public static final int FLAG_WALKING = 0;

    @Nullable protected Body body;
    @NotNull protected final BitSet flags = new BitSet();
    @Nullable protected ImageStrips imageStrips;
    protected float speed, speedSquared, orientation;

    @NotNull BodyDef bodyDef;
    @NotNull FixtureDef fixtureDef;

    public Person(@NotNull PersonDef def) {
        super(def);
        this.speed = def.speed;
        this.speedSquared = speed * speed;

        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.position.set(def.x, def.y);

        CircleShape shape = new CircleShape();
        shape.setRadius(def.radius);

        fixtureDef = new FixtureDef();
        fixtureDef.density = def.density;
        fixtureDef.shape = shape;
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

        body = world.box2D.createBody(bodyDef);
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
            if (imageStrips != null) imageStrips.resolve(flags);
        }
    }
    public void animUnflag(int flag) {
        if (flags.get(flag)) {
            flags.clear(flag);
            if (imageStrips != null) imageStrips.resolve(flags);
        }
    }
    public void resolveImageStrips() {
        if (imageStrips != null) imageStrips.resolve(flags);
    }
    public void setCharset(TextureAtlas atlas, CharsetDef[] defs) {
        for (int i = 0; i < defs.length; i++) {
            CharsetDef def = defs[i];
            FlagResolver resolver = new FlagResolver(atlas, i, def);
            if (imageStrips != null) imageStrips.resolvers.add(resolver);
        }
        resolveImageStrips();
    }

    /**
     * Applies a force of length equal to the person's speed using the orientation vector as a base;
     * this actually modifies the orientation vector (the length, not the orientation), also,
     * whoever called this should take care of claiming the vector
     *
     * note that this *will not* actually apply a force if the body has not been created
     */
    public void move(@NotNull Vector2 orientationVector) {
        if (body != null) body.setLinearVelocity(orientationVector.nor().scl(speed));
        orientation = orientationVector.angleRad();
    }
    public void stop() {
        if (body != null) body.setLinearVelocity(Vector2.Zero);
    }

    @Override public void render(@NotNull SpriteBatch batch, @NotNull GameCamera camera) {
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
        if (pos != null) priority = -pos.y + 1;
    }

}
