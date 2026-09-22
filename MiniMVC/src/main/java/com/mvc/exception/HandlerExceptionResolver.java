package com.mvc.exception;

import com.mvc.handler.HandlerMethod;
import com.web.HttpRequest;
import com.web.HttpResponse;

/**
 * Resolves an exception thrown during handler execution into an HTTP response.
 *
 * @return {@code true} if the exception was handled
 */
public interface HandlerExceptionResolver {

    boolean resolveException(HttpRequest request,
                             HttpResponse response,
                             HandlerMethod handler,
                             Exception ex) throws Exception;
}
