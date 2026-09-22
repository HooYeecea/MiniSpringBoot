package com.web;

/**
 * Server-side forward / include.
 */
public interface RequestDispatcher {

    void forward(HttpRequest request, HttpResponse response);

    void include(HttpRequest request, HttpResponse response);
}
