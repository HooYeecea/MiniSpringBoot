package com.miniboot.server;

import com.minitomcat.HandleRequest;
import com.minitomcat.HttpServer;
import com.web.Servlet;

/**
 * Embedded BIO MiniTomcat: resets servlet mappings, registers the app servlet, then accept-loops.
 */
public final class BioEmbeddedServer implements EmbeddedServer {

    private final int port;
    private HttpServer httpServer;

    public BioEmbeddedServer(int port) {
        this.port = port;
    }

    @Override
    public void registerServlet(String pathPattern, Servlet servlet) {
        HandleRequest.resetMappings();
        HandleRequest.registerServlet(pathPattern, servlet);
    }

    @Override
    public void start() throws Exception {
        httpServer = new HttpServer(port);
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop, "miniboot-bio-shutdown"));
        httpServer.start();
    }

    @Override
    public void stop() {
        if (httpServer != null) {
            httpServer.stop();
        }
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public ServerType getType() {
        return ServerType.BIO;
    }
}
