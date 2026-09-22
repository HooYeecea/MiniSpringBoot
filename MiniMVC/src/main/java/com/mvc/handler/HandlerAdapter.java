package com.mvc.handler;

import com.web.HttpRequest;
import com.web.HttpResponse;

/**
 * Invokes a mapped handler for the current request.
 */
public interface HandlerAdapter {

    boolean supports(HandlerMethod handler);

    void handle(HttpRequest request, HttpResponse response, HandlerMethod handler) throws Exception;
}
