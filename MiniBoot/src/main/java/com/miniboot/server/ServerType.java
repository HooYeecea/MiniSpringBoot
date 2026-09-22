package com.miniboot.server;

/**
 * Embedded HTTP server type for MiniBoot.
 */
public enum ServerType {
    /** Blocking MiniTomcat ({@code MiniTomcat}). */
    BIO,
    /** Non-blocking MiniTomcat ({@code MiniTomcatNIO}). */
    NIO
}
