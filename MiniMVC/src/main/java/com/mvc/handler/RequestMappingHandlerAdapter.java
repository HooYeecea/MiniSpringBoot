package com.mvc.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvc.handler.argument.HandlerMethodArgumentResolver;
import com.mvc.handler.argument.HandlerMethodArgumentResolverComposite;
import com.mvc.handler.argument.HttpRequestMethodArgumentResolver;
import com.mvc.handler.argument.HttpResponseMethodArgumentResolver;
import com.mvc.handler.argument.PathVariableMethodArgumentResolver;
import com.mvc.handler.argument.RequestBodyMethodArgumentResolver;
import com.mvc.handler.argument.RequestParamMethodArgumentResolver;
import com.mvc.handler.returnvalue.HandlerMethodReturnValueHandler;
import com.mvc.handler.returnvalue.HandlerMethodReturnValueHandlerComposite;
import com.mvc.handler.returnvalue.ObjectReturnValueHandler;
import com.mvc.handler.returnvalue.ResponseBodyReturnValueHandler;
import com.mvc.handler.returnvalue.StringReturnValueHandler;
import com.web.HttpRequest;
import com.web.HttpResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * Invokes handlers using pluggable argument resolvers and return-value handlers.
 */
public class RequestMappingHandlerAdapter implements HandlerAdapter {

    private final HandlerMethodArgumentResolverComposite argumentResolvers =
            new HandlerMethodArgumentResolverComposite();
    private final HandlerMethodReturnValueHandlerComposite returnValueHandlers =
            new HandlerMethodReturnValueHandlerComposite();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RequestMappingHandlerAdapter() {
        argumentResolvers
                .addResolver(new HttpRequestMethodArgumentResolver())
                .addResolver(new HttpResponseMethodArgumentResolver())
                .addResolver(new RequestParamMethodArgumentResolver())
                .addResolver(new PathVariableMethodArgumentResolver())
                .addResolver(new RequestBodyMethodArgumentResolver(objectMapper));

        returnValueHandlers
                .addHandler(new ResponseBodyReturnValueHandler(objectMapper))
                .addHandler(new StringReturnValueHandler())
                .addHandler(new ObjectReturnValueHandler());
    }

    public void addArgumentResolver(HandlerMethodArgumentResolver resolver) {
        argumentResolvers.addResolver(resolver);
    }

    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        argumentResolvers.addResolvers(resolvers);
    }

    public void addReturnValueHandler(HandlerMethodReturnValueHandler handler) {
        returnValueHandlers.addHandler(handler);
    }

    public HandlerMethodReturnValueHandlerComposite getReturnValueHandlers() {
        return returnValueHandlers;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public boolean supports(HandlerMethod handler) {
        return handler != null;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HandlerMethod handler) throws Exception {
        Object[] args = resolveArguments(handler, request, response);
        Object returnValue;
        try {
            returnValue = handler.getMethod().invoke(handler.getBean(), args);
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause() == null ? ex : ex.getCause();
            if (cause instanceof Exception exception) {
                throw exception;
            }
            if (cause instanceof Error error) {
                throw error;
            }
            throw new IllegalStateException(cause);
        }
        returnValueHandlers.handleReturnValue(returnValue, handler, response);
    }

    private Object[] resolveArguments(HandlerMethod handler, HttpRequest request, HttpResponse response)
            throws Exception {
        Method method = handler.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            args[i] = argumentResolvers.resolveArgument(parameters[i], handler, request, response);
        }
        return args;
    }
}
