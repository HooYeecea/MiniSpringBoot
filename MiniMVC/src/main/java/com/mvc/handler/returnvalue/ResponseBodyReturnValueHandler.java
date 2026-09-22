package com.mvc.handler.returnvalue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvc.annotation.ResponseBody;
import com.mvc.annotation.RestController;
import com.mvc.handler.HandlerMethod;
import com.web.HttpResponse;

import java.lang.reflect.Method;

/**
 * Serializes {@link ResponseBody} / {@link RestController} returns as JSON.
 */
public class ResponseBodyReturnValueHandler implements HandlerMethodReturnValueHandler {

    private final ObjectMapper objectMapper;

    public ResponseBodyReturnValueHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supportsReturnType(HandlerMethod handler, Class<?> returnType) {
        Method method = handler.getMethod();
        Class<?> beanClass = handler.getBean().getClass();
        return method.isAnnotationPresent(ResponseBody.class)
                || beanClass.isAnnotationPresent(ResponseBody.class)
                || beanClass.isAnnotationPresent(RestController.class);
    }

    @Override
    public void handleReturnValue(Object returnValue,
                                  HandlerMethod handler,
                                  HttpResponse response) throws Exception {
        response.setHeader("Content-Type", "application/json; charset=UTF-8");
        response.setBody(objectMapper.writeValueAsString(returnValue));
    }
}
