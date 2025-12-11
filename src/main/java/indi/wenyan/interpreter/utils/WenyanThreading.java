package indi.wenyan.interpreter.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for nothing but just a remainder of not in main thread.
 * Annotated with this need to be very careful with threading issues
 */
@SuppressWarnings("ALL")
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface WenyanThreading {
    boolean planning() default false;
}
