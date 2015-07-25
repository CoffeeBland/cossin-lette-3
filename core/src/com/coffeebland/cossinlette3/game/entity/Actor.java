package com.coffeebland.cossinlette3.game.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.coffeebland.cossinlette3.game.GameCamera;
import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.game.file.WorldFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Actor {

    @Nullable protected GameWorld world;
    protected final float priority;

    public Actor(float priority) {
        this.priority = priority;
    }
    public Actor(WorldFile.ActorDef def) {
        this(def.priority);
    }

    public float getPriority() { return priority; }

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
    public void dispose() {
        removeFromWorld();
    }
}
