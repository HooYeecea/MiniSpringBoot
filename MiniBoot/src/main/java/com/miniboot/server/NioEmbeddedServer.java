package com.miniboot.server;

import cn.minitomcatnio.connector.Connector;
import cn.minitomcatnio.container.Context;
import cn.minitomcatnio.container.Engine;
import cn.minitomcatnio.container.Host;
import com.web.Servlet;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Embedded NIO MiniTomcat: Engine → Host → Context + Connector accept loop.
 */
public final class NioEmbeddedServer implements EmbeddedServer {

    private final int port;
    private Context context;
    private Engine engine;
    private Connector connector;

    public NioEmbeddedServer(int port) {
        this.port = port;
    }

    @Override
    public void registerServlet(String pathPattern, Servlet servlet) {
        try {
            Path docBase = Files.createTempDirectory("miniboot-nio-root");
            context = new Context(docBase);
            context.addServlet(pathPattern, servlet);

            Host localhost = new Host("localhost");
            localhost.addContext("", context);

            engine = new Engine("Catalina");
            engine.setDefaultHost("localhost");
            engine.addHost(localhost);

            connector = new Connector(port, engine);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to prepare NIO embedded server", e);
        }
    }

    @Override
    public void start() throws Exception {
        if (connector == null) {
            throw new IllegalStateException("Call registerServlet before start");
        }
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop, "miniboot-nio-shutdown"));
        connector.start();
    }

    @Override
    public void stop() {
        if (connector != null) {
            connector.stop();
        }
        if (engine != null) {
            engine.stop();
        }
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public ServerType getType() {
        return ServerType.NIO;
    }
}
