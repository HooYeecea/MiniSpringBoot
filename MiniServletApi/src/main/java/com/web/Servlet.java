package com.web;

/**
 * Minimal Servlet contract shared by MiniTomcat / MiniTomcatNIO / MiniMVC.
 */
public interface Servlet {

    default void init() {
    }

    /** Called by containers that support init-param. Default ignores config. */
    default void init(ServletConfig config) {
        init();
    }

    default String getInitParameter(String name) {
        return null;
    }

    default void destroy() {
    }

    void service(HttpRequest request, HttpResponse response) throws Exception;
}
