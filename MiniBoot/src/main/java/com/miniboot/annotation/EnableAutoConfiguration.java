package com.miniboot.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables loading of {@link com.miniboot.autoconfigure.AutoConfiguration} classes from
 * {@code META-INF/miniboot.factories}.
 * <p>
 * Included by {@link MiniSpringBootApplication}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableAutoConfiguration {
}
