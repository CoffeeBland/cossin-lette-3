package com.coffeebland.cossinlette3.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.coffeebland.cossinlette3.CossinLette3;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Cossin Lette 3";
        new LwjglApplication(new CossinLette3(), config);
    }
}