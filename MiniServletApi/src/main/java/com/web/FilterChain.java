package com.web;

/**
 * Remaining filter chain, ending at the target Servlet.
 */
public interface FilterChain {

    void doFilter(HttpRequest request, HttpResponse response) throws Exception;
}
