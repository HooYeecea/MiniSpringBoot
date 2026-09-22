package com.minitomcat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * BIO HTTP server. {@link #start()} blocks in the accept loop; {@link #stop()} closes the socket.
 */
public class HttpServer {

    private final int port;
    private volatile boolean running;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;

    public HttpServer(int port) {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("invalid port: " + port);
        }
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        threadPool = Executors.newFixedThreadPool(10);
        running = true;
        System.out.println("Server is running on port " + port);
        try {
            while (running) {
                Socket socket = serverSocket.accept();
                System.out.println("连接进来了！");
                threadPool.submit(() -> HandleRequest.handleRequest(socket));
            }
        } catch (SocketException e) {
            if (running) {
                throw e;
            }
            // stop() closed the ServerSocket — expected
        }
    }

    public void stop() {
        running = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        }
        if (threadPool != null) {
            threadPool.shutdownNow();
            try {
                threadPool.awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new HttpServer(8080).start();
    }
}
