package com.miniboot.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the primary source for {@link com.miniboot.MiniSpringApplication#run}.
 * <p>
 * Component scan defaults to the package of the annotated class (and below).
 * Override with {@link #scanBasePackages()} when the main class lives outside
 * the application bean packages (e.g. boot demo scanning {@code com.mvc.demo}).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MiniSpringBootApplication {

    /**
     * Base packages to scan for {@code @MyComponent} beans.
     * Empty means: use the package of the primary source class.
     * <p>
     * Step 1 supports a single package (first element if several are listed).
     */
    String[] scanBasePackages() default {};
}
