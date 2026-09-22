package com.mvc.handler;

import com.miniioccontainer.aop.MiniAopInterceptor;
import com.miniioccontainer.context.MiniApplicationContext;
import com.mvc.annotation.Controller;
import com.mvc.annotation.RequestMapping;
import com.mvc.annotation.RequestMethod;
import com.mvc.annotation.RestController;
import com.web.HttpRequest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scans IoC {@link Controller} / {@link RestController} beans and matches request paths
 * (exact or {@code {var}} templates).
 */
public class RequestMappingHandlerMapping implements HandlerMapping {

    private final List<MappingRegistration> registrations = new ArrayList<>();

    public void init(MiniApplicationContext applicationContext) {
        registrations.clear();
        Map<String, Object> beans = applicationContext.getBeansOfType(Object.class);
        for (Object bean : beans.values()) {
            Object target = MiniAopInterceptor.unwrap(bean);
            Class<?> clazz = target.getClass();
            if (!isControllerType(clazz)) {
                continue;
            }
            registerController(target, clazz);
        }
        System.out.println("[MiniMVC] HandlerMapping registered " + registrations.size() + " handler(s)");
        for (MappingRegistration registration : registrations) {
            System.out.println("  -> " + registration.handlerMethod().getDescription());
        }
    }

    private static boolean isControllerType(Class<?> clazz) {
        return clazz.isAnnotationPresent(Controller.class)
                || clazz.isAnnotationPresent(RestController.class);
    }

    private void registerController(Object bean, Class<?> clazz) {
        RequestMapping typeMapping = clazz.getAnnotation(RequestMapping.class);
        String typePath = typeMapping == null ? "" : typeMapping.value();

        for (Method method : clazz.getDeclaredMethods()) {
            RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
            if (methodMapping == null) {
                continue;
            }
            String path = combinePaths(typePath, methodMapping.value());
            Set<RequestMethod> httpMethods = toHttpMethods(typeMapping, methodMapping);
            HandlerMethod handlerMethod = new HandlerMethod(bean, method, path, httpMethods);
            register(handlerMethod);
        }
    }

    private void register(HandlerMethod handlerMethod) {
        for (MappingRegistration existing : registrations) {
            if (existing.pathPattern().equals(handlerMethod.getPath())
                    && overlaps(existing.handlerMethod(), handlerMethod)) {
                throw new IllegalStateException(
                        "Ambiguous mapping: " + handlerMethod.getDescription()
                                + " conflicts with " + existing.handlerMethod().getDescription());
            }
        }
        registrations.add(MappingRegistration.compile(handlerMethod));
    }

    private static boolean overlaps(HandlerMethod a, HandlerMethod b) {
        if (a.getHttpMethods().isEmpty() || b.getHttpMethods().isEmpty()) {
            return true;
        }
        for (RequestMethod method : a.getHttpMethods()) {
            if (b.getHttpMethods().contains(method)) {
                return true;
            }
        }
        return false;
    }

    private static Set<RequestMethod> toHttpMethods(RequestMapping typeMapping, RequestMapping methodMapping) {
        EnumSet<RequestMethod> methods = EnumSet.noneOf(RequestMethod.class);
        if (methodMapping.method().length > 0) {
            for (RequestMethod method : methodMapping.method()) {
                methods.add(method);
            }
            return methods;
        }
        if (typeMapping != null && typeMapping.method().length > 0) {
            for (RequestMethod method : typeMapping.method()) {
                methods.add(method);
            }
        }
        return methods;
    }

    static String combinePaths(String typePath, String methodPath) {
        String left = normalize(typePath);
        String right = normalize(methodPath);
        if ("/".equals(left)) {
            return right;
        }
        if ("/".equals(right)) {
            return left;
        }
        return left + right;
    }

    static String normalize(String path) {
        if (path == null || path.isEmpty()) {
            return "/";
        }
        String normalized = path.startsWith("/") ? path : "/" + path;
        while (normalized.length() > 1 && normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    @Override
    public HandlerMethod getHandler(HttpRequest request) {
        String path = normalize(request.getPath());
        String httpMethod = request.getMethod();
        for (MappingRegistration registration : registrations) {
            Map<String, String> variables = registration.match(path);
            if (variables == null) {
                continue;
            }
            HandlerMethod candidate = registration.handlerMethod();
            if (candidate.supportsHttpMethod(httpMethod)) {
                return candidate.withUriVariables(variables);
            }
        }
        return null;
    }

    public int totalMappings() {
        return registrations.size();
    }

    private record MappingRegistration(
            HandlerMethod handlerMethod,
            String pathPattern,
            Pattern regex,
            List<String> variableNames
    ) {
        static MappingRegistration compile(HandlerMethod handlerMethod) {
            String pattern = handlerMethod.getPath();
            List<String> names = new ArrayList<>();
            StringBuilder regex = new StringBuilder("^");
            int i = 0;
            while (i < pattern.length()) {
                char c = pattern.charAt(i);
                if (c == '{') {
                    int end = pattern.indexOf('}', i);
                    if (end < 0) {
                        throw new IllegalStateException("Unclosed path variable in " + pattern);
                    }
                    names.add(pattern.substring(i + 1, end));
                    regex.append("([^/]+)");
                    i = end + 1;
                } else {
                    if (".[]{}()*+-?^$|\\".indexOf(c) >= 0) {
                        regex.append('\\');
                    }
                    regex.append(c);
                    i++;
                }
            }
            regex.append('$');
            return new MappingRegistration(
                    handlerMethod,
                    pattern,
                    Pattern.compile(regex.toString()),
                    List.copyOf(names));
        }

        /** @return uri variables map (possibly empty), or null if no match */
        Map<String, String> match(String path) {
            Matcher matcher = regex.matcher(path);
            if (!matcher.matches()) {
                return null;
            }
            if (variableNames.isEmpty()) {
                return Map.of();
            }
            Map<String, String> variables = new LinkedHashMap<>();
            for (int i = 0; i < variableNames.size(); i++) {
                variables.put(variableNames.get(i), matcher.group(i + 1));
            }
            return variables;
        }
    }
}
