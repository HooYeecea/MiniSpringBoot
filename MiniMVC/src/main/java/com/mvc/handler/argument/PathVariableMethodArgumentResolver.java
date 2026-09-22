package com.mvc.handler.argument;

import com.mvc.annotation.PathVariable;
import com.mvc.handler.HandlerMethod;
import com.web.HttpRequest;
import com.web.HttpResponse;

import java.lang.reflect.Parameter;

public class PathVariableMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(Parameter parameter) {
        return parameter.isAnnotationPresent(PathVariable.class);
    }

    @Override
    public Object resolveArgument(Parameter parameter,
                                  HandlerMethod handler,
                                  HttpRequest request,
                                  HttpResponse response) {
        PathVariable annotation = parameter.getAnnotation(PathVariable.class);
        String name = annotation.value();
        if (name == null || name.isEmpty()) {
            if (!parameter.isNamePresent()) {
                throw new IllegalStateException(
                        "Compile with -parameters, or set @PathVariable(\"name\") on " + parameter);
            }
            name = parameter.getName();
        }
        String raw = handler.getUriVariables().get(name);
        if (raw == null) {
            throw new IllegalArgumentException("Missing path variable: " + name);
        }
        return SimpleTypeConverter.convert(raw, parameter.getType(), name);
    }
}
