package com.coffeebland.cossinlette3.game;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.coffeebland.cossinlette3.input.UpdateableInput;

/**
 * Created by Guillaume on 2015-10-12.
 */
public class RenderInputMultiplexer
        extends InputMultiplexer
        implements UpdateableInput {

    @Override public boolean update(float delta) {
        for (InputProcessor p : getProcessors()) {
            if (p instanceof UpdateableInput && ((UpdateableInput) p).update(delta)) {
                return true;
            }
        }
        return false;
    }
}
