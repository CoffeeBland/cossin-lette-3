package com.coffeebland.cossinlette3;

import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.state.GameState;
import com.coffeebland.cossinlette3.utils.N;
import com.coffeebland.cossinlette3.utils.NtN;

/**
 * Created by Guillaume on 2015-10-15.
 */
public class SimpleSource<T> implements Source<T> {

    @N T object;

    public SimpleSource(@N T object) {
        this.object = object;
    }

    @Override @N
    public T eval(@NtN GameState gameState, @NtN GameWorld gameWorld, @NtN Context context) {
        return object;
    }
}
