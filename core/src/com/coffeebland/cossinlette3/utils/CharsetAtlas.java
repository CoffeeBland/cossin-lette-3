package com.coffeebland.cossinlette3.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Json;
import com.coffeebland.cossinlette3.game.file.CharsetDef;
import com.coffeebland.cossinlette3.game.visual.Charset;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guillaume on 2015-09-22.
 */
public class CharsetAtlas extends TextureAtlas {

    @NotNull protected Map<String, CharsetDef[]> charsetsDefs = new HashMap<>();

    public CharsetAtlas() {}
    public CharsetAtlas(String internalPackFile) {
        super(internalPackFile);
    }
    public CharsetAtlas(FileHandle packFile) {
        super(packFile);
    }
    public CharsetAtlas(FileHandle packFile, boolean flip) {
        super(packFile, flip);
    }
    public CharsetAtlas(FileHandle packFile, FileHandle imagesDir) {
        super(packFile, imagesDir);
    }
    public CharsetAtlas(FileHandle packFile, FileHandle imagesDir, boolean flip) {
        super(packFile, imagesDir, flip);
    }
    public CharsetAtlas(TextureAtlasData data) {
        super(data);
    }

    @NotNull public Charset getCharset(@NotNull String name) {
        CharsetDef[] defs;
        if (charsetsDefs.containsKey(name)) {
            defs = charsetsDefs.get(name);
        } else {
            defs = new Json().fromJson(
                    CharsetDef[].class,
                    Gdx.files.internal("img/game/" +name + ".charset.json")
            );
            charsetsDefs.put(name, defs);
        }
        return new Charset(this, defs);
    }
}
