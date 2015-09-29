package com.coffeebland.cossinlette3.editor;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.coffeebland.cossinlette3.CossinLette3;
import com.coffeebland.cossinlette3.state.StateManager.TransitionArgs;

public class EditorLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Cossin Lette 3: R\u00E9demption - \u00C9diteur";
        new LwjglApplication(new CossinLette3(
                new TransitionArgs<>(EditorState.class).setLength(0, 0)
        ), config);
    }
}