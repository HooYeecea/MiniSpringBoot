package com.minitomcat;

import com.web.Filter;
import com.web.FilterChain;
import com.web.HttpRequest;
import com.web.HttpResponse;

/**
 * 演示 URL 匹配：只拦截 {@code /hello}，其他路径不会进这个 Filter。
 */
public class HelloFilter implements Filter {
    @Override
    public void doFilter(HttpRequest request, HttpResponse response, FilterChain chain) throws Exception {
        System.out.println("[HelloFilter] 命中 /hello，准备进入 HelloServlet");
        chain.doFilter(request, response);
    }
}
