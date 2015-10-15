package com.coffeebland.cossinlette3.game.file;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.coffeebland.cossinlette3.game.entity.Tileset;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldDef {

    public int width, height;

    @NotNull public String imgSrc;

    @NotNull public List<PersonDef> people = new ArrayList<>();
    @NotNull public List<PolygonDef> staticPolygons = new ArrayList<>();
    @NotNull public List<TileLayerDef> tileLayers = new ArrayList<>();
    @NotNull public Color backgroundColor = Color.BLACK.cpy();

    public WorldDef() {}

    public void resize(@NotNull Tileset tileset, int newWidth, int newHeight, int tileLayerSize) {
        width = newWidth;
        height = newHeight;
        int tWidth = (int)tileset.metersToTile(newWidth);
        int tHeight = (int)tileset.metersToTile(newHeight);
        while (tileLayers.size() > tileLayerSize) tileLayers.remove(tileLayers.size() - 1);
        while (tileLayers.size() < tileLayerSize) tileLayers.add(new TileLayerDef(this, tileset, tileLayers.size()));
        for (TileLayerDef tileLayer: tileLayers) {
            tileLayer.tiles = Arrays.copyOf(tileLayer.tiles, tHeight);
            for (int y = 0; y < tHeight; y++) {
                if (tileLayer.tiles[y] == null) tileLayer.tiles[y] = new long[tWidth][];
                else tileLayer.tiles[y] = Arrays.copyOf(tileLayer.tiles[y], tWidth);
                for (int x = 0; x < tWidth; x++) {
                    if (tileLayer.tiles[y][x] == null) tileLayer.tiles[y][x] = new long[0];
                }
            }
        }
    }

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
