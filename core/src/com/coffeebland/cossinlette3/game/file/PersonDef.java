package com.coffeebland.cossinlette3.game.file;

import com.coffeebland.cossinlette3.utils.N;

public class PersonDef extends ActorDef {
    public float radius, x, y, speed, density, orientation;
    @N public String charset;

    public PersonDef() {}

    public boolean hasCharset() {
        return charset != null && !charset.isEmpty();
    }
}