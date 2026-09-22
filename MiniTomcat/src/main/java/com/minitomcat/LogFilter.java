package com.minitomcat;

import com.web.Filter;
import com.web.FilterChain;
import com.web.HttpRequest;
import com.web.HttpResponse;

/**
 * 演示「请求进入 / 离开」日志。映射 {@code /*}，所有请求都会经过。
 */
public class LogFilter implements Filter {
    @Override
    public void doFilter(HttpRequest request, HttpResponse response, FilterChain chain) throws Exception {
        System.out.println("[LogFilter] 进入: " + request.getMethod() + " " + request.getPath());
        chain.doFilter(request, response);
        System.out.println("[LogFilter] 离开: " + request.getPath());
    }
}
