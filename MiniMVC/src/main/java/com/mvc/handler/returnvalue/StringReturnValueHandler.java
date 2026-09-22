package com.mvc.handler.returnvalue;

import com.mvc.annotation.ResponseBody;
import com.mvc.annotation.RestController;
import com.mvc.handler.HandlerMethod;
import com.web.HttpResponse;

/**
 * Writes a plain {@link String} return value (when not treated as JSON).
 */
public class StringReturnValueHandler implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(HandlerMethod handler, Class<?> returnType) {
        if (!String.class.equals(returnType)) {
            return false;
        }
        return !isResponseBody(handler);
    }

    @Override
    public void handleReturnValue(Object returnValue,
                                  HandlerMethod handler,
                                  HttpResponse response) {
        response.setHeader("Content-Type", "text/plain; charset=UTF-8");
        response.setBody((String) returnValue);
    }

    private static boolean isResponseBody(HandlerMethod handler) {
        Class<?> beanClass = handler.getBean().getClass();
        return handler.getMethod().isAnnotationPresent(ResponseBody.class)
                || beanClass.isAnnotationPresent(ResponseBody.class)
                || beanClass.isAnnotationPresent(RestController.class);
    }
}
