package com.mvc.handler.returnvalue;

import com.mvc.handler.HandlerMethod;
import com.web.HttpResponse;

/**
 * Writes a handler return value into the response.
 */
public interface HandlerMethodReturnValueHandler {

    boolean supportsReturnType(HandlerMethod handler, Class<?> returnType);

    void handleReturnValue(Object returnValue,
                           HandlerMethod handler,
                           HttpResponse response) throws Exception;
}
