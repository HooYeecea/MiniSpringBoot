package com.miniboot.demo;

import com.miniboot.MiniSpringApplication;
import com.miniboot.annotation.MiniSpringBootApplication;

/**
 * Step-1 demo: one-line Boot-style start that reuses MiniMVC sample controllers.
 * <p>
 * Run: {@code mvn -pl MiniBoot -am exec:java}
 */
@MiniSpringBootApplication(scanBasePackages = "com.mvc.demo")
public class MiniBootApplication {

    public static void main(String[] args) {
        MiniSpringApplication.run(MiniBootApplication.class, args);
    }
}
