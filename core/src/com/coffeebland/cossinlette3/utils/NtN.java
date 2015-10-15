package com.coffeebland.cossinlette3.utils;

import java.lang.annotation.*;

/**
 * Created by Guillaume on 2015-10-15.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface NtN {
    String value() default "";

    Class<? extends Exception> exception() default Exception.class;
}
