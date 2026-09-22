package com.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds a request parameter to a handler method argument.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {

    /** Parameter name. Empty → use the Java parameter name if available. */
    String value() default "";

    boolean required() default true;

    /** Used when the parameter is missing (only if {@link #required()} is false). */
    String defaultValue() default "";
}
