package com.coffeebland.cossinlette3.game.file;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import org.jetbrains.annotations.NotNull;

public class SaveFile {

    public static final String NEW_SAVE_FILE = "new";
    public static final String SAVE_FOLDER = "saves";
    public static FileHandle getSaveFileHandle(@NotNull String name) {
        return Gdx.files.local
                (String.format("%s/%s.save.json", SAVE_FOLDER, name));
    }
    public static SaveFile getNewSaveFile() {
        return read(Gdx.files.internal(String.format("%s/%s.save.json", "misc", NEW_SAVE_FILE)));
    }

    public WorldFiles worldFile;
    public float x, y;

    public SaveFile() {}

    public void write(FileHandle handle) {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        json.toJson(this, handle);
    }

    public static SaveFile read(FileHandle handle) {
        Json json = new Json();
        json.setIgnoreUnknownFields(true);
        return json.fromJson(SaveFile.class, handle);
    }
}
