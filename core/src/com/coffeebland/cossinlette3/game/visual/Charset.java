package com.coffeebland.cossinlette3.game.visual;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Json;
import com.coffeebland.cossinlette3.game.file.CharsetDef;
import org.jetbrains.annotations.NotNull;

public class Charset extends ImageStrips {
    public Charset(@NotNull TextureAtlas atlas, @NotNull CharsetDef[] defs) {
        for (int i = 0; i < defs.length; i++) {
            CharsetDef def = defs[i];
            FlagResolver resolver = new FlagResolver(atlas, i, def);
            resolvers.add(resolver);
        }
    }
    public Charset(@NotNull TextureAtlas atlas, @NotNull FileHandle fileHandle) {
        this(atlas, new Json().fromJson(CharsetDef[].class, fileHandle));
    }
}
