package com.coffeebland.cossinlette3.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.utils.V2;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Guillaume on 2015-10-12.
 */
public abstract class DirectionalInput extends ActiveableInput implements UpdateableInput {

    @Override public boolean update(float delta) {
        @NotNull Vector2 vec = V2.get();
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) vec.add(-1, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) vec.add(0, 1);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) vec.add(1, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) vec.add(0, -1);
        boolean handled = handleOrientation(vec);
        V2.claim(vec);
        return handled;
    }
    public abstract boolean handleOrientation(@NotNull Vector2 direction);
}
