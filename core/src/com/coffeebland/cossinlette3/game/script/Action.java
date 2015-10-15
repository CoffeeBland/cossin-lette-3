package com.coffeebland.cossinlette3.game.script;

import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.state.GameState;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Guillaume on 2015-10-12.
 */
public interface Action {
    void execute(@NotNull GameState gameState, @NotNull GameWorld gameWorld);
}
