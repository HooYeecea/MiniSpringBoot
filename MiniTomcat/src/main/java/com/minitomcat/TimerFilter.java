package com.minitomcat;

import com.web.Filter;
import com.web.FilterChain;
import com.web.HttpRequest;
import com.web.HttpResponse;

/**
 * 演示 Filter 可以包住后续整条链：先计时，再放行，回来后打印耗时。
 */
public class TimerFilter implements Filter {
    @Override
    public void doFilter(HttpRequest request, HttpResponse response, FilterChain chain) throws Exception {
        long start = System.currentTimeMillis();
        chain.doFilter(request, response);
        long cost = System.currentTimeMillis() - start;
        System.out.println("[TimerFilter] " + request.getPath() + " 耗时 " + cost + " ms");
    }
}
