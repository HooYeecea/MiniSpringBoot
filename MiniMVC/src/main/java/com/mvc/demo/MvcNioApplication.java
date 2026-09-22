package com.mvc.demo;

import cn.minitomcatnio.connector.Connector;
import cn.minitomcatnio.container.Context;
import cn.minitomcatnio.container.Engine;
import cn.minitomcatnio.container.Host;
import com.miniioccontainer.context.MiniApplicationContext;
import com.mvc.servlet.DispatcherServlet;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Boots MiniMVC {@link DispatcherServlet} on MiniTomcatNIO.
 * <p>
 * {@code mvn -f MiniMVC/pom.xml exec:java -Dexec.mainClass=com.mvc.demo.MvcNioApplication}
 */
public class MvcNioApplication {

    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        MiniApplicationContext applicationContext = new MiniApplicationContext("com.mvc.demo");
        DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);
        dispatcherServlet.addInterceptor(new LoggingInterceptor());
        dispatcherServlet.init();

        Path docBase = Files.createTempDirectory("minimvc-nio-root");
        Context context = new Context(docBase);
        // /* → empty prefix in Mapper: matches every path before the default Servlet
        context.addServlet("/*", dispatcherServlet);

        Host localhost = new Host("localhost");
        localhost.addContext("", context);

        Engine engine = new Engine("Catalina");
        engine.setDefaultHost("localhost");
        engine.addHost(localhost);

        Connector connector = new Connector(PORT, engine);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            connector.stop();
            engine.stop();
        }, "minimvc-nio-shutdown"));

        System.out.println("[MiniMVC] NIO demo on port " + PORT);
        System.out.println("  GET http://localhost:" + PORT + "/mvc/hello");
        System.out.println("  GET http://localhost:" + PORT + "/api/ping");
        System.out.println("  GET http://localhost:" + PORT + "/api/users/7");
        connector.start();
    }
}
