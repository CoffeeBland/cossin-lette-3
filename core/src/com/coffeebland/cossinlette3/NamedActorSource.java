package com.coffeebland.cossinlette3;

import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.game.entity.Actor;
import com.coffeebland.cossinlette3.state.GameState;
import com.coffeebland.cossinlette3.utils.N;
import com.coffeebland.cossinlette3.utils.NtN;

/**
 * Created by Guillaume on 2015-10-15.
 */
public class NamedActorSource implements Source<Actor> {

    @NtN protected String name;

    public NamedActorSource(@NtN String name) {
        this.name = name;
    }

    @Override @N
    public Actor eval(@NtN GameState gameState, @NtN GameWorld gameWorld, @NtN Context context) {
        return gameWorld.getNamed(name).stream().findFirst().orElse(null);
    }
}
