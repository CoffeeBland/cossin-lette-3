package com.coffeebland.cossinlette3;

import com.coffeebland.cossinlette3.utils.N;

/**
 * Created by Guillaume on 2015-10-15.
 */
public class Context {
    @N protected Context parent;

    public Context(@N Context parent) {
        this.parent = parent;
    }
    public Context() {
        this(null);
    }

    @N public Context getParent() { return parent; }
}
