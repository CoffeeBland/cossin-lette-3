package com.coffeebland.cossinlette3.state;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.coffeebland.cossinlette3.game.file.*;
import com.coffeebland.cossinlette3.utils.CharsetAtlas;
import com.coffeebland.cossinlette3.utils.N;
import com.coffeebland.cossinlette3.utils.NtN;
import com.coffeebland.cossinlette3.utils.event.EventManager;
import com.coffeebland.cossinlette3.utils.event.Tag;

public abstract class StateImpl<StateArgs> implements State<StateArgs> {
    public static final float TRANSITION_SHORT = 250;
    public static final float TRANSITION_MEDIUM = 500;
    public static final float TRANSITION_LONG = 1000;

    public StateImpl() {}

    protected Color backgroundColor;
    protected StateManager stateManager;
    protected AssetManager assetManager;
    protected EventManager eventManager;

    @Override public void setStateManager(@N StateManager stateManager) {
        this.stateManager = stateManager;
    }
    @Override @NtN public Color getBackgroundColor() {
        if (backgroundColor == null) {
            backgroundColor = Color.BLACK.cpy();
        }
        return backgroundColor;
    }
    public void setBackgroundColor(@NtN Color color) {
        this.backgroundColor = color;
    }

    @Override public void onPrepare(@N StateArgs args, StateManager.Notifier notifier) {
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
            @NtN String path,
            @NtN Class<T> type,
            @NtN LoadListener<T> listener) {
        assetManager.load(path, type);
        eventManager.post(Tag.ASSETS, () -> listener.onLoaded(assetManager.get(path, type)));
    }

    public interface LoadListener<T> {
        void onLoaded(@NtN T value);
    }
}
