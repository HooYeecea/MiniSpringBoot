package com.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps a controller type or handler method to an HTTP path.
 * <p>
 * Class-level path is a prefix; method-level path is appended.
 * Empty {@link #method()} means all HTTP methods are accepted (for now).
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

    /** URL path pattern, e.g. {@code /hello} or {@code /users}. */
    String value() default "";

    /** Allowed HTTP methods. Empty = any method. */
    RequestMethod[] method() default {};
}
