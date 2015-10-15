package com.coffeebland.cossinlette3.game.file;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Guillaume on 2015-10-10.
 */
public class WorldDefLoader extends AsynchronousAssetLoader<WorldDef, WorldDefLoader.WorldDefParameter> {

    WorldDef worldDef;

    public WorldDefLoader(FileHandleResolver resolver) { super(resolver); }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, WorldDefParameter parameter) {
        worldDef = WorldDef.read(file);
    }

    @Override
    public WorldDef loadSync(AssetManager manager, String fileName, FileHandle file, WorldDefParameter parameter) {
        WorldDef worldDef = this.worldDef;
        this.worldDef = null;
        return worldDef;
    }

    @Nullable
    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, WorldDefParameter parameter) {
        return null;
    }

    static class WorldDefParameter extends AssetLoaderParameters<WorldDef> {}
}
