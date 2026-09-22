package com.miniboot.server;

import com.web.Servlet;

/**
 * Abstraction over BIO / NIO MiniTomcat so {@code MiniSpringApplication} does not hard-code a server.
 */
public interface EmbeddedServer {

    /**
     * Register a servlet mapping before {@link #start()}.
     */
    void registerServlet(String pathPattern, Servlet servlet);

    /**
     * Start the server and block the calling thread until stopped.
     */
    void start() throws Exception;

    /**
     * Request shutdown (may unblock {@link #start()}).
     */
    void stop();

    int getPort();

    ServerType getType();
}
