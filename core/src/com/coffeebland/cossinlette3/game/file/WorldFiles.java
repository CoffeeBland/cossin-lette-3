package com.coffeebland.cossinlette3.game.file;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public enum WorldFiles {
    NEW("test"),
    TEST2("test2");

    WorldFiles(String fileName) {
        this.fileName = fileName;
    }

    public final String fileName;

    public FileHandle getHandle() { return Gdx.files.internal(getPath()); }
    public String getPath() { return String.format("worlds/%s.world.json", fileName); }
}
