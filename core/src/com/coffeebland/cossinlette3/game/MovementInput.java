package com.coffeebland.cossinlette3.game;

import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.game.entity.Person;
import com.coffeebland.cossinlette3.input.DirectionalInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MovementInput extends DirectionalInput {

    @Nullable protected Person person;

    public MovementInput(@Nullable Person person) {
        this.person = person;
    }

    @Override
    public boolean handleOrientation(@NotNull Vector2 direction) {
        if (person == null) return false;
        if (direction.len2() > 0) person.move(direction);
        else person.stop();
        return true;
    }
}
