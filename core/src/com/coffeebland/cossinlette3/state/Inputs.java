package com.coffeebland.cossinlette3.state;

import com.badlogic.gdx.Preferences;
import com.coffeebland.cossinlette3.utils.NtN;

/**
 * TODO create an abstraction over actually keys and gestures to allow parameterising of those (!)
 * Created by Guillaume on 2015-10-22.
 */
public class Inputs {
    @NtN protected Preferences prefs;

    public Inputs(@NtN Preferences prefs) {
        this.prefs = prefs;
    }
}
