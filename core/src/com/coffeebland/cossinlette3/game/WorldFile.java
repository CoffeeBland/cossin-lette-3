package com.coffeebland.cossinlette3.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.List;

public class WorldFile {

    public List<PolygonDef> staticPolygons;
    public Color backgroundColor;

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

    public GameWorld createGameWorld(SaveFile saveFile) {
        GameWorld gameWorld = new GameWorld();

        gameWorld.getBackgroundColor().set(backgroundColor);

        for (PolygonDef def : staticPolygons) {
            PolygonActor poly = new PolygonActor(def);
            poly.addToWorld(gameWorld);
        }

        return gameWorld;
    }

    public static class PolygonDef {
        public float x, y;
        public float[] points;

        public PolygonDef() {}
        public PolygonDef(float x, float y, float[] points) {
            this.x = x;
            this.y = y;
            this.points = points;
        }
    }
    public static class PersonDef {
        public float radius, x, y, headHeight, speed;

        public PersonDef() {}
        public PersonDef(float radius, float headHeight, float speed, float x, float y) {
            this.radius = radius;
            this.headHeight = headHeight;
            this.speed = speed;
            this.x = x;
            this.y = y;
        }
    }
}
