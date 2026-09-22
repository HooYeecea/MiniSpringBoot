package com.mvc.handler.argument;

import com.mvc.handler.HandlerMethod;
import com.web.HttpRequest;
import com.web.HttpResponse;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * Tries registered {@link HandlerMethodArgumentResolver}s in order.
 */
public class HandlerMethodArgumentResolverComposite implements HandlerMethodArgumentResolver {

    private final List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();

    public HandlerMethodArgumentResolverComposite addResolver(HandlerMethodArgumentResolver resolver) {
        resolvers.add(resolver);
        return this;
    }

    public HandlerMethodArgumentResolverComposite addResolvers(List<HandlerMethodArgumentResolver> toAdd) {
        resolvers.addAll(toAdd);
        return this;
    }

    @Override
    public boolean supportsParameter(Parameter parameter) {
        return findResolver(parameter) != null;
    }

    @Override
    public Object resolveArgument(Parameter parameter,
                                  HandlerMethod handler,
                                  HttpRequest request,
                                  HttpResponse response) throws Exception {
        HandlerMethodArgumentResolver resolver = findResolver(parameter);
        if (resolver == null) {
            throw new IllegalStateException(
                    "No ArgumentResolver for parameter " + parameter + " on " + handler.getMethod());
        }
        return resolver.resolveArgument(parameter, handler, request, response);
    }

    private HandlerMethodArgumentResolver findResolver(Parameter parameter) {
        for (HandlerMethodArgumentResolver resolver : resolvers) {
            if (resolver.supportsParameter(parameter)) {
                return resolver;
            }
        }
        return null;
    }
}
