package com.mvc.handler.argument;

import com.mvc.annotation.RequestParam;
import com.mvc.handler.HandlerMethod;
import com.web.HttpRequest;
import com.web.HttpResponse;

import java.lang.reflect.Parameter;

public class RequestParamMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestParam.class);
    }

    @Override
    public Object resolveArgument(Parameter parameter,
                                  HandlerMethod handler,
                                  HttpRequest request,
                                  HttpResponse response) {
        RequestParam annotation = parameter.getAnnotation(RequestParam.class);
        String name = annotation.value();
        if (name == null || name.isEmpty()) {
            if (!parameter.isNamePresent()) {
                throw new IllegalStateException(
                        "Compile with -parameters, or set @RequestParam(\"name\") on " + parameter);
            }
            name = parameter.getName();
        }

        String raw = request.getParameter(name);
        if (raw == null || raw.isEmpty()) {
            if (!annotation.defaultValue().isEmpty()) {
                raw = annotation.defaultValue();
            } else if (annotation.required()) {
                throw new IllegalArgumentException("Missing required request parameter: " + name);
            } else {
                return SimpleTypeConverter.defaultForMissing(parameter.getType());
            }
        }
        return SimpleTypeConverter.convert(raw, parameter.getType(), name);
    }
}
