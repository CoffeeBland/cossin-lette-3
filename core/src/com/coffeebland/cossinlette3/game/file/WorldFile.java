package com.coffeebland.cossinlette3.game.file;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.game.entity.PolygonActor;
import com.coffeebland.cossinlette3.game.entity.TileLayer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WorldFile {

    public List<PolygonDef> staticPolygons = new ArrayList<>();
    public List<TileLayerDef> tileLayers = new ArrayList<>();
    public Color backgroundColor = Color.BLACK.cpy();

    public WorldFile() {}

    public void write(FileHandle handle) {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        handle.writeString(json.prettyPrint(this), false);
    }

    public static WorldFile read(WorldFiles file) {
        return read(file.getHandle());
    }
    public static WorldFile read(FileHandle handle) {
        Json json = new Json();
        json.setIgnoreUnknownFields(true);
        return json.fromJson(WorldFile.class, handle);
    }

    public GameWorld createGameWorld(@Nullable SaveFile saveFile) {
        GameWorld gameWorld = new GameWorld();

        gameWorld.backgroundColor.set(backgroundColor);

        for (PolygonDef def : staticPolygons) {
            PolygonActor poly = new PolygonActor(def);
            poly.addToWorld(gameWorld);
        }

        for (TileLayerDef def : tileLayers) {
            TileLayer layer = new TileLayer(def);
            layer.addToWorld(gameWorld);
        }

        return gameWorld;
    }

}
