package com.mvc.handler.returnvalue;

import com.mvc.annotation.ResponseBody;
import com.mvc.annotation.RestController;
import com.mvc.handler.HandlerMethod;
import com.web.HttpResponse;

/**
 * Fallback: {@code String.valueOf(returnValue)} as plain text.
 */
public class ObjectReturnValueHandler implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(HandlerMethod handler, Class<?> returnType) {
        return !isResponseBody(handler);
    }

    @Override
    public void handleReturnValue(Object returnValue,
                                  HandlerMethod handler,
                                  HttpResponse response) {
        response.setHeader("Content-Type", "text/plain; charset=UTF-8");
        response.setBody(String.valueOf(returnValue));
    }

    private static boolean isResponseBody(HandlerMethod handler) {
        Class<?> beanClass = handler.getBean().getClass();
        return handler.getMethod().isAnnotationPresent(ResponseBody.class)
                || beanClass.isAnnotationPresent(ResponseBody.class)
                || beanClass.isAnnotationPresent(RestController.class);
    }
}
