package com.coffeebland.cossinlette3.desktop;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.coffeebland.cossinlette3.CossinLette3;
import com.coffeebland.cossinlette3.state.SplashState;
import com.coffeebland.cossinlette3.state.State;
import com.coffeebland.cossinlette3.state.StateManager.TransitionArgs;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Cossin Lette 3: R\u00E9demption";
        config.fullscreen = true;
        Graphics.DisplayMode mode = LwjglApplicationConfiguration.getDesktopDisplayMode();
        config.width = mode.width;
        config.height = mode.height;
        new LwjglApplication(new CossinLette3(
                new TransitionArgs<>(SplashState.class)
                        .setArgs(null)
                        .setLength(State.TRANSITION_LONG, State.TRANSITION_LONG)
                        .setColor(Color.BLACK.cpy())
        ), config);
    }
}