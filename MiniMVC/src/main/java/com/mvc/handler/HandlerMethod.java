package com.mvc.handler;

import com.mvc.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * A mapped controller method plus optional URI variables for the current request.
 */
public final class HandlerMethod {

    private final Object bean;
    private final Method method;
    private final String pathPattern;
    private final Set<RequestMethod> httpMethods;
    private final Map<String, String> uriVariables;

    public HandlerMethod(Object bean, Method method, String pathPattern, Set<RequestMethod> httpMethods) {
        this(bean, method, pathPattern, httpMethods, Map.of());
    }

    public HandlerMethod(Object bean,
                         Method method,
                         String pathPattern,
                         Set<RequestMethod> httpMethods,
                         Map<String, String> uriVariables) {
        this.bean = bean;
        this.method = method;
        this.pathPattern = pathPattern;
        if (httpMethods == null || httpMethods.isEmpty()) {
            this.httpMethods = Collections.emptySet();
        } else {
            this.httpMethods = Collections.unmodifiableSet(EnumSet.copyOf(httpMethods));
        }
        this.uriVariables = uriVariables == null || uriVariables.isEmpty()
                ? Map.of()
                : Map.copyOf(uriVariables);
        this.method.setAccessible(true);
    }

    public HandlerMethod withUriVariables(Map<String, String> variables) {
        return new HandlerMethod(bean, method, pathPattern, httpMethods, variables);
    }

    public Object getBean() {
        return bean;
    }

    public Method getMethod() {
        return method;
    }

    public String getPath() {
        return pathPattern;
    }

    public Map<String, String> getUriVariables() {
        return uriVariables;
    }

    /** Empty set means all HTTP methods are accepted. */
    public Set<RequestMethod> getHttpMethods() {
        return httpMethods;
    }

    public boolean supportsHttpMethod(String requestMethod) {
        if (httpMethods.isEmpty()) {
            return true;
        }
        if (requestMethod == null || requestMethod.isEmpty()) {
            return false;
        }
        try {
            return httpMethods.contains(RequestMethod.valueOf(requestMethod.toUpperCase()));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public String getDescription() {
        return bean.getClass().getSimpleName() + "#" + method.getName()
                + " [" + String.join(",", httpMethodLabels()) + "] " + pathPattern;
    }

    private Iterable<String> httpMethodLabels() {
        if (httpMethods.isEmpty()) {
            return Set.of("*");
        }
        return httpMethods.stream().map(Enum::name).toList();
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
