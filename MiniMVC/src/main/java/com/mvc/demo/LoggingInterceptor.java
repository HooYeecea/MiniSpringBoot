package com.mvc.demo;

import com.mvc.handler.HandlerMethod;
import com.mvc.interceptor.HandlerInterceptor;
import com.web.HttpRequest;
import com.web.HttpResponse;

/**
 * Demo interceptor: logs pre / post / afterCompletion.
 */
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpRequest request, HttpResponse response, HandlerMethod handler) {
        System.out.println("[Interceptor] pre  " + request.getMethod() + " " + request.getPath()
                + " -> " + handler.getDescription());
        return true;
    }

    @Override
    public void postHandle(HttpRequest request, HttpResponse response, HandlerMethod handler) {
        System.out.println("[Interceptor] post " + request.getPath() + " status=" + response.getStatus());
    }

    @Override
    public void afterCompletion(HttpRequest request,
                                HttpResponse response,
                                HandlerMethod handler,
                                Exception ex) {
        if (ex == null) {
            System.out.println("[Interceptor] after " + request.getPath() + " ok");
        } else {
            System.out.println("[Interceptor] after " + request.getPath()
                    + " error=" + ex.getClass().getSimpleName());
        }
    }
}
