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
        json.writeValue(this);
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

    public static class ActorDef {
        public float priority;

        public ActorDef() {}
    }
    public static class PolygonDef extends ActorDef {
        public float x, y;
        public float[] points;

        public PolygonDef() {}
    }
    public static class PersonDef extends ActorDef {
        public float radius, x, y, headHeight, speed, density;

        public PersonDef() {}
    }
    public static class ImageSheetDef {
        public String src;
        public int frameWidth, frameHeight, decalX, decalY;

        public ImageSheetDef() {}
    }
    public static class OrientationFrameDef {
        public int frameY;
        public boolean flip;
        public float startAngle, endAngle;

        public OrientationFrameDef() {}
    }
    public static class ImageStripDef extends ImageSheetDef {
        public float fps;
        public List<OrientationFrameDef> orientationFrameDefs;

        public ImageStripDef() {}
    }
    public static class FlagResolverDef {
        public List<Integer> flags;
        public int priority;
        public ImageStripDef imageStripDef;

        public FlagResolverDef() {}
    }
    public static class ImageStripsDef {
        public List<FlagResolverDef> resolverDefs;

        public ImageStripsDef() {}
    }
    public static class TileLayerDef extends ActorDef {
        public String src;
        public int tileSize;
        public int[][][] tiles;
        public int[][] animations;
        public float x, y;
        public int width, height;

        public TileLayerDef() {}
    }
}
