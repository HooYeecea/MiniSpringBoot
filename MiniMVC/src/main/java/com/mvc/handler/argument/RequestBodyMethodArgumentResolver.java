package com.mvc.handler.argument;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvc.annotation.RequestBody;
import com.mvc.annotation.Valid;
import com.mvc.exception.HttpMessageNotReadableException;
import com.mvc.handler.HandlerMethod;
import com.mvc.validation.MiniValidator;
import com.web.HttpRequest;
import com.web.HttpResponse;

import java.lang.reflect.Parameter;

/**
 * Deserializes JSON request body into a method argument.
 */
public class RequestBodyMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper;

    public RequestBodyMethodArgumentResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supportsParameter(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestBody.class);
    }

    @Override
    public Object resolveArgument(Parameter parameter,
                                  HandlerMethod handler,
                                  HttpRequest request,
                                  HttpResponse response) throws Exception {
        RequestBody annotation = parameter.getAnnotation(RequestBody.class);
        byte[] body = request.getBody();
        boolean empty = body == null || body.length == 0;
        if (empty) {
            if (annotation.required()) {
                throw new IllegalArgumentException("Required request body is missing");
            }
            return null;
        }
        Object value;
        try {
            value = objectMapper.readValue(body, parameter.getType());
        } catch (JsonProcessingException ex) {
            throw new HttpMessageNotReadableException("Failed to read JSON request body", ex);
        }
        if (parameter.isAnnotationPresent(Valid.class)) {
            MiniValidator.validate(value);
        }
        return value;
    }
}
