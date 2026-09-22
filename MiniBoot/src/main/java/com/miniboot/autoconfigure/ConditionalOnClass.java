package com.miniboot.autoconfigure;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Skip this auto-configuration unless every listed class is present on the classpath.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConditionalOnClass {

    /**
     * Fully-qualified class names to require (string form avoids hard compile deps when teaching).
     */
    String[] value();
}
