package com.coffeebland.cossinlette3;

import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.state.GameState;
import com.coffeebland.cossinlette3.utils.NtN;

/**
 * Created by Guillaume on 2015-10-15.
 */
public interface BooleanSource {
    default boolean eval(@NtN GameState gameState, @NtN GameWorld gameWorld, @NtN Context context) {
        return false;
    }
}
