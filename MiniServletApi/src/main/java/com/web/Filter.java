package com.web;

/**
 * Application filter. Must call {@code chain.doFilter} to continue.
 */
public interface Filter {

    default void init() {
    }

    default void destroy() {
    }

    void doFilter(HttpRequest request, HttpResponse response, FilterChain chain) throws Exception;
}
