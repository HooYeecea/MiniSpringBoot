package com.mvc.exception;

import com.miniioccontainer.aop.MiniAopInterceptor;
import com.miniioccontainer.context.MiniApplicationContext;
import com.mvc.annotation.ControllerAdvice;
import com.mvc.annotation.ExceptionHandler;
import com.mvc.handler.HandlerMethod;
import com.mvc.handler.returnvalue.HandlerMethodReturnValueHandlerComposite;
import com.web.HttpRequest;
import com.web.HttpResponse;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Scans {@link ControllerAdvice} beans and dispatches to {@link ExceptionHandler} methods.
 */
public class ExceptionHandlerExceptionResolver implements HandlerExceptionResolver {

    private final List<ExceptionHandlerMethod> handlers = new ArrayList<>();
    private final HandlerMethodReturnValueHandlerComposite returnValueHandlers;

    public ExceptionHandlerExceptionResolver(HandlerMethodReturnValueHandlerComposite returnValueHandlers) {
        this.returnValueHandlers = returnValueHandlers;
    }

    public void init(MiniApplicationContext applicationContext) {
        handlers.clear();
        Map<String, Object> beans = applicationContext.getBeansOfType(Object.class);
        for (Object bean : beans.values()) {
            Object target = MiniAopInterceptor.unwrap(bean);
            Class<?> clazz = target.getClass();
            if (!clazz.isAnnotationPresent(ControllerAdvice.class)) {
                continue;
            }
            for (Method method : clazz.getDeclaredMethods()) {
                ExceptionHandler annotation = method.getAnnotation(ExceptionHandler.class);
                if (annotation == null) {
                    continue;
                }
                Class<? extends Throwable>[] types = annotation.value();
                if (types.length == 0) {
                    continue;
                }
                method.setAccessible(true);
                handlers.add(new ExceptionHandlerMethod(target, method, types));
            }
        }
        System.out.println("[MiniMVC] ExceptionHandler methods: " + handlers.size());
    }

    @Override
    public boolean resolveException(HttpRequest request,
                                    HttpResponse response,
                                    HandlerMethod handler,
                                    Exception ex) throws Exception {
        ExceptionHandlerMethod matched = findHandler(ex);
        if (matched == null) {
            return false;
        }
        Object[] args = resolveArgs(matched.method(), request, response, ex);
        Object returnValue = matched.method().invoke(matched.bean(), args);
        // Reuse return-value handlers; wrap advice method as a synthetic HandlerMethod
        HandlerMethod adviceHandler = new HandlerMethod(
                matched.bean(), matched.method(), "/__exception__", null);
        returnValueHandlers.handleReturnValue(returnValue, adviceHandler, response);
        return true;
    }

    private ExceptionHandlerMethod findHandler(Throwable ex) {
        ExceptionHandlerMethod best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (ExceptionHandlerMethod candidate : handlers) {
            for (Class<? extends Throwable> type : candidate.exceptionTypes()) {
                if (!type.isInstance(ex)) {
                    continue;
                }
                int distance = distance(ex.getClass(), type);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = candidate;
                }
            }
        }
        return best;
    }

    private static int distance(Class<?> actual, Class<?> declared) {
        int distance = 0;
        Class<?> current = actual;
        while (current != null && !declared.equals(current)) {
            current = current.getSuperclass();
            distance++;
        }
        return current == null ? Integer.MAX_VALUE : distance;
    }

    private static Object[] resolveArgs(Method method,
                                        HttpRequest request,
                                        HttpResponse response,
                                        Exception ex) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> type = parameters[i].getType();
            if (HttpRequest.class.isAssignableFrom(type)) {
                args[i] = request;
            } else if (HttpResponse.class.isAssignableFrom(type)) {
                args[i] = response;
            } else if (Throwable.class.isAssignableFrom(type)) {
                Throwable matched = findAssignable(ex, type);
                args[i] = matched;
            } else {
                throw new IllegalStateException(
                        "Unsupported @ExceptionHandler parameter: " + type.getName());
            }
        }
        return args;
    }

    private static Throwable findAssignable(Throwable ex, Class<?> type) {
        Throwable current = ex;
        while (current != null) {
            if (type.isInstance(current)) {
                return current;
            }
            current = current.getCause();
        }
        return ex;
    }

    private record ExceptionHandlerMethod(
            Object bean,
            Method method,
            Class<? extends Throwable>[] exceptionTypes
    ) {
    }
}
