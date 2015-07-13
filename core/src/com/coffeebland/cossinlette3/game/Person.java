package com.coffeebland.cossinlette3.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.coffeebland.cossinlette3.utils.Const;
import com.coffeebland.cossinlette3.utils.Textures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Person extends Actor implements GameCamera.PositionSource {

    public static final int FLAG_WALKING = 0;

    @Nullable protected Body body;
    @NotNull protected WorldFile.PersonDef def;
    @NotNull protected final BitSet flags = new BitSet();
    @NotNull public final ImageStrips imageStrips = new ImageStrips();

    protected float speed;
    protected float orientation;

    public Person(@NotNull WorldFile.PersonDef def) {
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

        CircleShape shape = new CircleShape();
        shape.setRadius(def.radius);

        body = world.box2D.createBody(bodyDef);
        body.createFixture(shape, 60);
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
        if (body != null) body.applyForceToCenter(orientationVector.nor().scl(speed), true);
    }

    @Override public void render(@NotNull SpriteBatch batch, @NotNull GameCamera camera) {
        Vector2 pos = getPosition();
        if (pos != null) {
            imageStrips.render(batch,
                    (pos.x - camera.pos.x) / Const.METERS_PER_PIXEL,
                    (pos.y - camera.pos.y) / Const.METERS_PER_PIXEL,
                    orientation, 1, false);
        }
    }

    @Override public void update(float delta) {
        imageStrips.update(delta);
    }

    public static class ImageStrips {

        public enum NewStripFlags {
            KEEP_FRAME, KEEP_REMAINING;
        }

        @NotNull public final SortedSet<ImageStripResolver> resolvers = new TreeSet<>();
        @Nullable protected ImageStrip currentStrip;
        protected float fps, frameLength, durationRemaining;
        protected int frameX;

        public ImageStrips() {

        }

        public void resolve(@NotNull BitSet flags, EnumSet<NewStripFlags> newStripFlags) {
            resolveStrip:
            {
                for (ImageStripResolver resolver : resolvers) {
                    if (resolver.conditionsMet(flags)) {
                        currentStrip = resolver.imageStrip;
                        break resolveStrip;
                    }
                }

                // Could not resolve strip
                currentStrip = null;
                return;
            }

            this.fps = currentStrip.fps;
            this.frameLength = 1000 / fps;
            if (!newStripFlags.contains(NewStripFlags.KEEP_FRAME)) frameX = 0;
            else frameX %= currentStrip.framesX;
            if (!newStripFlags.contains(NewStripFlags.KEEP_REMAINING)) durationRemaining = frameLength;
        }
        public void resolve(@NotNull BitSet flags) {
            resolve(flags, EnumSet.noneOf(NewStripFlags.class));
        }

        public void render(@NotNull SpriteBatch batch, float x, float y, float orientation, float scale, boolean flip) {
            if (currentStrip != null) {
                currentStrip.render(batch, x, y, frameX, orientation, scale, flip);
            }
        }
        public void render(@NotNull SpriteBatch batch, float x, float y, float orientation, float scale, boolean flip, @NotNull Color color) {
            if (currentStrip != null) {
                currentStrip.render(batch, x, y, frameX, orientation, scale, flip, color);
            }
        }

        public void update(float delta) {
            if (currentStrip != null) {
                durationRemaining -= delta;
                while (durationRemaining < 0) {
                    durationRemaining += frameLength;
                    frameX = (frameX + 1) % currentStrip.framesX;
                }
            }
        }
    }

    public static abstract class ImageStripResolver implements Comparable<ImageStripResolver> {
        public final int priority;
        public final ImageStrip imageStrip;

        public ImageStripResolver(int priority, ImageStrip imageStrip) {
            this.priority = priority;
            this.imageStrip = imageStrip;
        }

        @Override public int compareTo(@NotNull ImageStripResolver compared) {
            return Integer.compare(compared.priority, priority);
        }

        public abstract boolean conditionsMet(@NotNull BitSet flags);
    }

    public static class ImageStrip extends ImageSheet {
        protected final float fps;
        protected final List<OrientationFrames> frames;

        public ImageStrip(@NotNull String src, int frameWidth, int frameHeight, int decalX, int decalY, float fps, List<OrientationFrames> frames) {
            super(src, frameWidth, frameHeight, decalX, decalY);
            this.frames = frames;
            this.fps = fps;
        }

        public void render(@NotNull SpriteBatch batch, float x, float y, int frameX, float orientation, float scale, boolean flip) {
            for (OrientationFrames frame : frames) {
                if (orientation >= frame.startAngle && orientation < frame.endAngle) {
                    render(batch, x, y, frameX, frame.frameY, scale, flip);
                    return;
                }
            }
        }
        public void render(@NotNull SpriteBatch batch, float x, float y, int frameX, float orientation, float scale, boolean flip, @NotNull Color tint) {
            for (OrientationFrames frame : frames) {
                if (orientation >= frame.startAngle && orientation < frame.endAngle) {
                    render(batch, x, y, frameX, frame.frameY, scale, flip, tint);
                    return;
                }
            }
        }
    }
    public static class OrientationFrames {
        public final int frameY;
        public final float startAngle, endAngle;

        public OrientationFrames(int frameY, float startAngle, float endAngle) {
            this.frameY = frameY;
            this.startAngle = startAngle;
            this.endAngle = endAngle;
        }
        public OrientationFrames(int frameY, double startAngle, double endAngle) {
            this(frameY, (float)startAngle, (float)endAngle);
        }
    }

    public static class ImageSheet {
        @NotNull public final String src;
        @NotNull public final Texture texture;
        public final int frameWidth, frameHeight, framesX, framesY, decalX, decalY;

        public ImageSheet(@NotNull String src, int frameWidth, int frameHeight, int decalX, int decalY) {
            this.src = src;
            this.texture = Textures.get(src);
            this.frameWidth = frameWidth;
            this.frameHeight = frameHeight;
            this.framesX = texture.getWidth() / frameWidth;
            this.framesY = texture.getHeight() / frameHeight;
            this.decalX = decalX;
            this.decalY = decalY;
        }

        public void render(@NotNull SpriteBatch batch, float x, float y, int imageX, int imageY, float scale, boolean flip) {
            batch.draw(texture,
                    x - decalX, y - decalY,
                    frameWidth * scale, frameHeight * scale,
                    frameWidth * imageX,
                    frameHeight * imageY,
                    frameWidth, frameHeight,
                    flip, false
            );
        }
        public void render(@NotNull SpriteBatch batch, float x, float y, int imageX, int imageY, float scale, boolean flip, @NotNull Color tint) {
            batch.setColor(tint);
            render(batch, x, y, imageX, imageY, scale, flip);
            batch.setColor(Color.WHITE);
        }
    }
}
