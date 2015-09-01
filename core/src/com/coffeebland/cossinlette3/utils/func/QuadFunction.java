package com.coffeebland.cossinlette3.utils.func;

/**
 * Created by Guillaume on 2015-08-30.
 */
@FunctionalInterface
public interface QuadFunction<First, Second, Third, Fourth, Result> {
    Result apply(First first, Second second, Third third, Fourth fourth);
}
