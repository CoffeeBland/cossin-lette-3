package com.coffeebland.cossinlette3.state;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.coffeebland.cossinlette3.game.file.*;
import com.coffeebland.cossinlette3.utils.CharsetAtlas;
import com.coffeebland.cossinlette3.utils.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class StateImpl<StateArgs> implements State<StateArgs> {
    public static final float TRANSITION_SHORT = 250;
    public static final float TRANSITION_MEDIUM = 500;
    public static final float TRANSITION_LONG = 1000;

    public StateImpl() {}

    protected Color backgroundColor;
    protected StateManager stateManager;
    protected AssetManager assetManager;
    protected EventManager eventManager;

    @Override public void setStateManager(@Nullable StateManager stateManager) {
        this.stateManager = stateManager;
    }
    @Override @NotNull public Color getBackgroundColor() {
        if (backgroundColor == null) {
            backgroundColor = Color.BLACK.cpy();
        }
        return backgroundColor;
    }
    public void setBackgroundColor(@NotNull Color color) {
        this.backgroundColor = color;
    }

    @Override public void onPrepare(@Nullable StateArgs args, StateManager.Notifier notifier) {
        FileHandleResolver fhr = new InternalFileHandleResolver();
        assetManager = new AssetManager(fhr);
        assetManager.setLoader(TilesetDef.class, new TilesetDefLoader(fhr));
        assetManager.setLoader(CharsetAtlas.class, new CharsetAtlasLoader(fhr));
        assetManager.setLoader(WorldDef.class, new WorldDefLoader(fhr));
        eventManager = new EventManager();
    }
    @Override public void onPrepareUpdate(float delta) {
        eventManager.setTagTo(Tag.ASSETS, assetManager.update());
        eventManager.update(delta);
    }

    @Override public void update(float delta) {
        eventManager.setTagTo(Tag.ASSETS, assetManager.update());
        eventManager.update(delta);
    }
    @Override public void onDispose() {
        assetManager.dispose();
        assetManager = null;
    }

    @Override
    public void onTransitionInStart() {
        eventManager.runTag(Tag.TRANSITION_IN);
    }

    @Override
    public void onTransitionInFinish() {
        eventManager.cancelTag(Tag.TRANSITION_IN);

    }

    @Override
    public void onTransitionOutStart() {
        eventManager.runTag(Tag.TRANSITION_OUT);
    }

    @Override
    public void onTransitionOutFinish() {
        eventManager.cancelTag(Tag.TRANSITION_OUT);
    }

    public <T> void load(
            @NotNull String path,
            @NotNull Class<T> type,
            @NotNull LoadListener<T> listener) {
        assetManager.load(path, type);
        eventManager.post(Tag.ASSETS, () -> listener.onLoaded(assetManager.get(path, type)));
    }

    public interface LoadListener<T> {
        void onLoaded(@NotNull T value);
    }
}
