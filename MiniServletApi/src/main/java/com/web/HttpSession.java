package com.web;

/**
 * Server-side session attribute store.
 */
public interface HttpSession {

    String getId();

    Object getAttribute(String name);

    void setAttribute(String name, Object value);
}
