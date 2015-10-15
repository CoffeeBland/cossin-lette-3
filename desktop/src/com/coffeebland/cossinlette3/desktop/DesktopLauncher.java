package com.coffeebland.cossinlette3.desktop;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.coffeebland.cossinlette3.CossinLette3;
import com.coffeebland.cossinlette3.state.SplashState;
import com.coffeebland.cossinlette3.state.StateImpl;
import com.coffeebland.cossinlette3.state.StateManager.TransitionArgs;

import java.io.File;

public class DesktopLauncher {

    public static boolean START_FULLSCREEN = false;

    public static void main(String[] arg) {
        System.out.println(new File(".").getAbsolutePath());
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Cossin Lette 3: R\u00E9demption";
        if (START_FULLSCREEN) {
            config.fullscreen = true;
            Graphics.DisplayMode mode = LwjglApplicationConfiguration.getDesktopDisplayMode();
            config.width = mode.width;
            config.height = mode.height;
        }
        new LwjglApplication(new CossinLette3(
                new TransitionArgs<>(SplashState.class)
                        .setArgs(null)
                        .setLength(StateImpl.TRANSITION_LONG, StateImpl.TRANSITION_LONG)
                        .setColor(Color.BLACK.cpy())
        ), config);
    }
}