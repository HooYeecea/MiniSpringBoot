package com.mvc.handler.argument;

import com.mvc.handler.HandlerMethod;
import com.web.HttpRequest;
import com.web.HttpResponse;

import java.lang.reflect.Parameter;

/**
 * Resolves one handler method parameter.
 */
public interface HandlerMethodArgumentResolver {

    boolean supportsParameter(Parameter parameter);

    Object resolveArgument(Parameter parameter,
                           HandlerMethod handler,
                           HttpRequest request,
                           HttpResponse response) throws Exception;
}
