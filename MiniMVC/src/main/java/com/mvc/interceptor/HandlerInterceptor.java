package com.mvc.interceptor;

import com.mvc.handler.HandlerMethod;
import com.web.HttpRequest;
import com.web.HttpResponse;

/**
 * Spring-style handler interceptor: preHandle → postHandle → afterCompletion.
 */
public interface HandlerInterceptor {

    /** @return {@code false} to abort the request */
    default boolean preHandle(HttpRequest request, HttpResponse response, HandlerMethod handler)
            throws Exception {
        return true;
    }

    default void postHandle(HttpRequest request, HttpResponse response, HandlerMethod handler)
            throws Exception {
    }

    /** Always called after the request finishes (success or error), for interceptors that ran preHandle. */
    default void afterCompletion(HttpRequest request,
                                 HttpResponse response,
                                 HandlerMethod handler,
                                 Exception ex) throws Exception {
    }
}
