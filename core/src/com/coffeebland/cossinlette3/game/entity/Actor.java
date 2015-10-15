package com.coffeebland.cossinlette3.game.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.coffeebland.cossinlette3.game.GameCamera;
import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.game.file.ActorDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class Actor {

    protected boolean shouldBeRemoved;
    @Nullable protected GameWorld world;
    protected float priority;
    @Nullable protected String name;

    public Actor(float priority, @Nullable String name) {
        this.priority = priority;
        this.name = name;
    }
    public Actor(float priority) {
        this(priority, null);
    }
    public Actor(@NotNull ActorDef def) {
        this(def.priority, def.name);
    }

    public float getPriority() { return priority; }
    public void setPriority(float priority) {
        this.priority = priority;
    }

    public void addToWorld(@NotNull GameWorld world) {
        assert this.world == null;
        shouldBeRemoved = false;
        this.world = world;
        world.getActors().add(this);
        if (name != null) world.getNamed(name).add(this);
    }
    public void removeFromWorld(Iterator<Actor> iterator) {
        this.world = null;
        iterator.remove();
        if (name != null) world.getNamed(name).remove(this);
    }

    public void flagForRemoval() { shouldBeRemoved = true; }
    public boolean shouldBeRemovedFromActors() { return shouldBeRemoved; }

    public void render(@NotNull Batch batch, @NotNull GameCamera camera) { }
    public void update(float delta) {

    }
}
