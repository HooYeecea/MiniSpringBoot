package com.miniboot.autoconfigure;

import com.miniboot.annotation.ConfigurationProperties;
import com.miniboot.server.ServerType;

/**
 * Bound from {@code server.*} Environment keys (with annotation defaults as fallback).
 */
@ConfigurationProperties(prefix = "server")
public class ServerProperties {

    private int port = 8080;
    private ServerType type = ServerType.BIO;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ServerType getType() {
        return type;
    }

    public void setType(ServerType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ServerProperties{port=" + port + ", type=" + type + '}';
    }
}
