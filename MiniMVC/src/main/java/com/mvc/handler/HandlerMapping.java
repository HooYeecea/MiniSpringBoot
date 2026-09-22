package com.mvc.handler;

import com.web.HttpRequest;

/**
 * Resolves an HTTP request to a {@link HandlerMethod}.
 */
public interface HandlerMapping {

    /**
     * @return matched handler, or {@code null} if no mapping
     */
    HandlerMethod getHandler(HttpRequest request);
}
