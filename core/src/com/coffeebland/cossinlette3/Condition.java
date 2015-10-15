package com.coffeebland.cossinlette3;

import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.state.GameState;
import com.coffeebland.cossinlette3.utils.N;
import com.coffeebland.cossinlette3.utils.NtN;

/**
 * Created by Guillaume on 2015-10-15.
 */
public class Condition implements Action {
    @NtN BooleanSource evalutation;
    @N Action onTrue, onFalse;

    public Condition(@NtN BooleanSource evaluation, @N Action onTrue, @N Action onFalse) {
        this.evalutation = evaluation;
        this.onTrue = onTrue;
        this.onFalse = onFalse;
    }

    @Override
    public void execute(@NtN GameState state, @NtN GameWorld world, @NtN Context ctx) {
        if (evalutation.eval(state, world, ctx)) if (onTrue != null) onTrue.execute(state, world, ctx);
        else if (onFalse != null) onFalse.execute(state, world, ctx);
    }
}
