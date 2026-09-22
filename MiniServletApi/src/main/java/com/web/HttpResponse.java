package com.web;

/**
 * Application-facing HTTP response contract.
 * Encoding/writing to the socket or channel stays in each container.
 */
public interface HttpResponse {

    void setStatus(int status);

    default void setStatus(int status, String reason) {
        setStatus(status);
    }

    int getStatus();

    default String getReason() {
        return "";
    }

    default void setHeader(String name, String value) {
    }

    void setBody(String text);

    default void setBody(byte[] body) {
        if (body == null || body.length == 0) {
            setBody("");
            return;
        }
        setBody(new String(body, java.nio.charset.StandardCharsets.UTF_8));
    }

    default byte[] getBody() {
        return new byte[0];
    }

    default void appendBody(byte[] extra) {
    }

    default void addCookie(String name, String value) {
    }

    default void sendError(int status, String reason) {
        setStatus(status, reason);
        setBody(status + " " + (reason == null ? "Error" : reason));
    }

    default boolean isError() {
        return false;
    }

    default void reset() {
    }
}
