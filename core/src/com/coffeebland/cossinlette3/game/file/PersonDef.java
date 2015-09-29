package com.coffeebland.cossinlette3.game.file;

import org.jetbrains.annotations.Nullable;

public class PersonDef extends ActorDef {
    public float radius, x, y, speed, density, orientation;
    @Nullable public String charset;

    public PersonDef() {}

    public boolean hasCharset() {
        return charset != null && !charset.isEmpty();
    }
}