package com.miniboot.annotation;

import com.miniboot.server.ServerType;

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
 * <p>
 * Meta-annotated with {@link EnableAutoConfiguration} so Web / embedded-server
 * wiring is loaded from {@code META-INF/miniboot.factories}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableAutoConfiguration
public @interface MiniSpringBootApplication {

    /**
     * Base packages to scan for {@code @MyComponent} beans.
     * Empty means: use the package of the primary source class.
     * <p>
     * Currently supports a single package (first element if several are listed).
     */
    String[] scanBasePackages() default {};

    /**
     * Embedded server implementation. Default {@link ServerType#BIO}.
     */
    ServerType server() default ServerType.BIO;

    /**
     * HTTP listen port.
     */
    int port() default 8080;
}
