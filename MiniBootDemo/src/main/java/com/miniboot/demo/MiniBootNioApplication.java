package com.miniboot.demo;

import com.miniboot.MiniSpringApplication;
import com.miniboot.annotation.MiniSpringBootApplication;
import com.miniboot.server.ServerType;

/**
 * Boot demo on NIO MiniTomcat.
 * <p>
 * Run: {@code mvn -f MiniBootDemo/pom.xml exec:java -Dexec.mainClass=com.miniboot.demo.MiniBootNioApplication}
 */
@MiniSpringBootApplication(scanBasePackages = "com.mvc.demo", server = ServerType.NIO, port = 8080)
public class MiniBootNioApplication {

    public static void main(String[] args) {
        MiniSpringApplication.run(MiniBootNioApplication.class, args);
    }
}
