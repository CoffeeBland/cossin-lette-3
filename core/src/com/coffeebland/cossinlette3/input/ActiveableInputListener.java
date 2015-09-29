package com.coffeebland.cossinlette3.input;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputListener;

/**
 * Created by Guillaume on 2015-09-28.
 */
public class ActiveableInputListener extends InputListener {

    protected boolean enabled = true;

    public void enable() { enabled = true; }
    public void disable() { enabled = false; }
    public boolean isEnabled() { return enabled; }

    @Override
    public boolean handle(Event e) {
        return isEnabled() && super.handle(e);
    }
}
