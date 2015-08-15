package com.coffeebland.cossinlette3.game.file;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.ArrayList;
import java.util.List;

public class WorldDef {


    public int width, height;

    public String imgSrc;

    public List<PolygonDef> staticPolygons = new ArrayList<>();
    public List<TileLayerDef> tileLayers = new ArrayList<>();
    public Color backgroundColor = Color.BLACK.cpy();

    public WorldDef() {}

    public void write(FileHandle handle) {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        handle.writeString(json.prettyPrint(this), false);
    }

    public static WorldDef read(WorldFiles file) {
        return read(file.getHandle());
    }
    public static WorldDef read(FileHandle handle) {
        Json json = new Json();
        json.setIgnoreUnknownFields(true);
        return json.fromJson(WorldDef.class, handle);
    }
}
