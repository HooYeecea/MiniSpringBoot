package com.mvc.handler.returnvalue;

import com.mvc.handler.HandlerMethod;
import com.web.HttpResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Tries registered {@link HandlerMethodReturnValueHandler}s in order.
 */
public class HandlerMethodReturnValueHandlerComposite implements HandlerMethodReturnValueHandler {

    private final List<HandlerMethodReturnValueHandler> handlers = new ArrayList<>();

    public HandlerMethodReturnValueHandlerComposite addHandler(HandlerMethodReturnValueHandler handler) {
        handlers.add(handler);
        return this;
    }

    @Override
    public boolean supportsReturnType(HandlerMethod handler, Class<?> returnType) {
        return findHandler(handler, returnType) != null;
    }

    @Override
    public void handleReturnValue(Object returnValue,
                                  HandlerMethod handler,
                                  HttpResponse response) throws Exception {
        if (returnValue == null) {
            return;
        }
        byte[] existing = response.getBody();
        if (existing != null && existing.length > 0) {
            return;
        }
        Class<?> returnType = returnValue.getClass();
        HandlerMethodReturnValueHandler selected = findHandler(handler, returnType);
        if (selected == null) {
            throw new IllegalStateException(
                    "No ReturnValueHandler for " + returnType.getName()
                            + " on " + handler.getMethod());
        }
        selected.handleReturnValue(returnValue, handler, response);
    }

    private HandlerMethodReturnValueHandler findHandler(HandlerMethod handler, Class<?> returnType) {
        for (HandlerMethodReturnValueHandler candidate : handlers) {
            if (candidate.supportsReturnType(handler, returnType)) {
                return candidate;
            }
        }
        return null;
    }
}
