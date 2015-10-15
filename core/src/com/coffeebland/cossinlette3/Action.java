package com.coffeebland.cossinlette3;

import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.state.GameState;
import com.coffeebland.cossinlette3.utils.NtN;

/**
 * Created by Guillaume on 2015-10-12.
 */
public interface Action {
    void execute(@NtN GameState state, @NtN GameWorld world, @NtN Context ctx);
}
