package com.minitomcat;

import com.web.Filter;
import com.web.FilterChain;
import com.web.HttpRequest;
import com.web.HttpResponse;
import com.web.Servlet;

import java.util.List;

/**
 * 模仿 Tomcat 的 ApplicationFilterChain。
 * 每次请求单独创建一份，内部用 pos 记录走到第几个 Filter。
 */
public class ApplicationFilterChain implements FilterChain {
    private final List<Filter> filters;
    private final Servlet servlet;
    private int pos = 0;

    public ApplicationFilterChain(List<Filter> filters, Servlet servlet) {
        this.filters = filters;
        this.servlet = servlet;
    }

    @Override
    public void doFilter(HttpRequest request, HttpResponse response) throws Exception {
        if (pos < filters.size()) {
            Filter next = filters.get(pos++);
            next.doFilter(request, response, this);
            return;
        }
        if (servlet != null) {
            servlet.service(request, response);
        } else {
            response.setStatus(404);
            response.setBody("<h1>404 Not Found</h1>");
        }
    }
}
