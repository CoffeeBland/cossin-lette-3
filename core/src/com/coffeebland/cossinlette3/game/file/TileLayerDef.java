package com.coffeebland.cossinlette3.game.file;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import org.jetbrains.annotations.NotNull;

public class TileLayerDef extends ActorDef {
    private TilesetDef tilesetDef;
    public String tilesetDefSrc;
    public int[][][] tiles;
    public float x, y;
    public int width, height;

    @NotNull
    public TilesetDef getTilesetDef() {
        if (tilesetDef == null) {
            FileHandle fileHandle = Gdx.files.internal(tilesetDefSrc);
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            tilesetDef = json.fromJson(TilesetDef.class, fileHandle);
        }
        return tilesetDef;
    }

    public TileLayerDef() {}
}
