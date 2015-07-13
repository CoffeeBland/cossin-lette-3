package com.coffeebland.cossinlette3.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Actor {

    @Nullable protected GameWorld world;
    @Nullable protected String reference;

    public void addToWorld(@NotNull GameWorld world) {
        if (this.world != null) throw new RuntimeException("Actors was already attached to a world");
        this.world = world;
        this.world.actors.add(this);
    }
    public void removeFromWorld() {
        this.world = null;
    }

    public boolean shouldBeRemovedFromActors() {
        return world == null;
    }

    public void render(@NotNull SpriteBatch batch, @NotNull GameCamera camera) { }
    public void update(float delta) { }
}
