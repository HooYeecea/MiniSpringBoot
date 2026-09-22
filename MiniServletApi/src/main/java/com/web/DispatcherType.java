package com.web;

/**
 * Dispatcher type used when matching filters.
 * When omitted in web.xml, the default is {@link #REQUEST}.
 */
public enum DispatcherType {
    REQUEST,
    FORWARD,
    INCLUDE,
    ERROR
}
