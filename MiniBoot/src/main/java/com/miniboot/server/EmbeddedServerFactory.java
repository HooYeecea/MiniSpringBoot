package com.miniboot.server;

/**
 * Creates an {@link EmbeddedServer} for the requested {@link ServerType}.
 */
public final class EmbeddedServerFactory {

    private EmbeddedServerFactory() {
    }

    public static EmbeddedServer create(ServerType type, int port) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        return switch (type) {
            case BIO -> new BioEmbeddedServer(port);
            case NIO -> new NioEmbeddedServer(port);
        };
    }
}
