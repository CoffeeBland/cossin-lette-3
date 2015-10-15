package com.coffeebland.cossinlette3.game;

import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.game.entity.Person;
import com.coffeebland.cossinlette3.input.DirectionalInput;
import com.coffeebland.cossinlette3.utils.N;
import com.coffeebland.cossinlette3.utils.NtN;

public class MovementInput extends DirectionalInput {

    @N protected Person person;

    public MovementInput(@N Person person) {
        this.person = person;
    }

    @Override
    public boolean handleOrientation(@NtN Vector2 direction) {
        if (person == null) return false;
        if (direction.len2() > 0) person.move(direction);
        else person.stop();
        return true;
    }
}
