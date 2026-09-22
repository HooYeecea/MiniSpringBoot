package com.web;

import java.util.Collections;
import java.util.Map;

/**
 * Application-facing HTTP request contract.
 * Container-specific helpers stay on each server's concrete implementation.
 */
public interface HttpRequest {

    String getMethod();

    /** Raw request-target from the request line (may include query string). */
    String getUri();

    /** Path without query string. */
    String getPath();

    String getVersion();

    default String getHeader(String name) {
        return null;
    }

    default Map<String, String> getHeaders() {
        return Collections.emptyMap();
    }

    default String getParameter(String name) {
        return null;
    }

    default Map<String, String> getParameters() {
        return Collections.emptyMap();
    }

    default String getCookie(String name) {
        return null;
    }

    default String getContextPath() {
        return "";
    }

    default String getServletPath() {
        return null;
    }

    default String getPathInfo() {
        return null;
    }

    default String getPathWithinContext() {
        return getPath();
    }

    default byte[] getBody() {
        return new byte[0];
    }

    default String getBodyAsString() {
        return "";
    }

    default DispatcherType getDispatcherType() {
        return DispatcherType.REQUEST;
    }

    default HttpSession getSession() {
        throw new UnsupportedOperationException("Session is not supported by this container");
    }

    default RequestDispatcher getRequestDispatcher(String path) {
        throw new UnsupportedOperationException("RequestDispatcher is not supported by this container");
    }
}
