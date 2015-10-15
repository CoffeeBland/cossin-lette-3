package com.coffeebland.cossinlette3.game.file;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.coffeebland.cossinlette3.utils.CharsetAtlas;

/**
 * Created by Guillaume on 2015-10-11.
 */
public class CharsetAtlasLoader extends SynchronousAssetLoader<CharsetAtlas, CharsetAtlasLoader.CharsetAtlasParameter> {
    public CharsetAtlasLoader (FileHandleResolver resolver) {
        super(resolver);
    }

    CharsetAtlas.TextureAtlasData data;

    @Override
    public CharsetAtlas load (AssetManager assetManager, String fileName, FileHandle file, CharsetAtlasParameter parameter) {
        for (CharsetAtlas.TextureAtlasData.Page page : data.getPages()) {
            page.texture = assetManager.get(page.textureFile.path().replaceAll("\\\\", "/"), Texture.class);
        }

        return new CharsetAtlas(data);
    }

    @Override
    public Array<AssetDescriptor> getDependencies (String fileName, FileHandle atlasFile, CharsetAtlasParameter parameter) {
        FileHandle imgDir = atlasFile.parent();

        if (parameter != null)
            data = new CharsetAtlas.TextureAtlasData(atlasFile, imgDir, parameter.flip);
        else {
            data = new CharsetAtlas.TextureAtlasData(atlasFile, imgDir, false);
        }

        Array<AssetDescriptor> dependencies = new Array<>();
        for (CharsetAtlas.TextureAtlasData.Page page : data.getPages()) {
            TextureLoader.TextureParameter params = new TextureLoader.TextureParameter();
            params.format = page.format;
            params.genMipMaps = page.useMipMaps;
            params.minFilter = page.minFilter;
            params.magFilter = page.magFilter;
            dependencies.add(new AssetDescriptor<>(page.textureFile, Texture.class, params));
        }
        return dependencies;
    }

    static public class CharsetAtlasParameter extends AssetLoaderParameters<CharsetAtlas> {
        /** whether to flip the texture atlas vertically **/
        public boolean flip = false;

        public CharsetAtlasParameter () {}
        public CharsetAtlasParameter (boolean flip) {
            this.flip = flip;
        }
    }
}
