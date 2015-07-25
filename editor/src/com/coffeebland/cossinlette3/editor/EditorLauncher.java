package com.coffeebland.cossinlette3.editor;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.coffeebland.cossinlette3.CossinLette3;

public class EditorLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Cossin Lette 3 - Éditeur";
        new LwjglApplication(new CossinLette3Editor(), config);
    }
}