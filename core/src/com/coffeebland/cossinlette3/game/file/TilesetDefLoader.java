package com.coffeebland.cossinlette3.game.file;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.coffeebland.cossinlette3.utils.N;

/**
 * Created by Guillaume on 2015-10-10.
 */
public class TilesetDefLoader extends AsynchronousAssetLoader<TilesetDef, TilesetDefLoader.TilesetDefParameter> {

    TilesetDef tilesetDef;

    public TilesetDefLoader(FileHandleResolver resolver) { super(resolver); }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, TilesetDefParameter parameter) {
        tilesetDef = new Json().fromJson(TilesetDef.class, file);
    }

    @Override
    public TilesetDef loadSync(AssetManager manager, String fileName, FileHandle file, TilesetDefParameter parameter) {
        TilesetDef tilesetDef = this.tilesetDef;
        this.tilesetDef = null;
        return tilesetDef;
    }

    @N @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TilesetDefParameter parameter) {
        return null;
    }

    static class TilesetDefParameter extends AssetLoaderParameters<TilesetDef> {}
}
