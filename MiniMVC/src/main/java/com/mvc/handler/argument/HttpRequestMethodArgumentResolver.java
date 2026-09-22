package com.mvc.handler.argument;

import com.mvc.handler.HandlerMethod;
import com.web.HttpRequest;
import com.web.HttpResponse;

import java.lang.reflect.Parameter;

public class HttpRequestMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(Parameter parameter) {
        return HttpRequest.class.isAssignableFrom(parameter.getType());
    }

    @Override
    public Object resolveArgument(Parameter parameter,
                                  HandlerMethod handler,
                                  HttpRequest request,
                                  HttpResponse response) {
        return request;
    }
}
