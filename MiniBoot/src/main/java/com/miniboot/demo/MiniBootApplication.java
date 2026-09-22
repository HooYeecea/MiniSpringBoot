package com.miniboot.demo;

import com.miniboot.MiniSpringApplication;
import com.miniboot.annotation.MiniSpringBootApplication;
import com.miniboot.server.ServerType;

/**
 * Boot demo on BIO MiniTomcat (default).
 * <p>
 * Run: {@code mvn -pl MiniBoot -am exec:java}
 */
@MiniSpringBootApplication(scanBasePackages = "com.mvc.demo", server = ServerType.BIO, port = 8080)
public class MiniBootApplication {

    public static void main(String[] args) {
        MiniSpringApplication.run(MiniBootApplication.class, args);
    }
}
