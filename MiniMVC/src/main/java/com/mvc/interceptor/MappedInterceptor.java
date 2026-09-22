package com.mvc.interceptor;

import com.mvc.handler.HandlerMethod;
import com.web.HttpRequest;
import com.web.HttpResponse;

/**
 * Wraps an interceptor with include path patterns ({@code /**}, {@code /api/**}, exact path).
 */
public class MappedInterceptor implements HandlerInterceptor {

    private final String[] includePatterns;
    private final HandlerInterceptor delegate;

    public MappedInterceptor(HandlerInterceptor delegate, String... includePatterns) {
        this.delegate = delegate;
        if (includePatterns == null || includePatterns.length == 0) {
            this.includePatterns = new String[]{"/**"};
        } else {
            this.includePatterns = includePatterns.clone();
        }
    }

    public boolean matches(String path) {
        for (String pattern : includePatterns) {
            if (match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    public HandlerInterceptor getInterceptor() {
        return delegate;
    }

    @Override
    public boolean preHandle(HttpRequest request, HttpResponse response, HandlerMethod handler)
            throws Exception {
        return delegate.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpRequest request, HttpResponse response, HandlerMethod handler)
            throws Exception {
        delegate.postHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpRequest request,
                                HttpResponse response,
                                HandlerMethod handler,
                                Exception ex) throws Exception {
        delegate.afterCompletion(request, response, handler, ex);
    }

    static boolean match(String pattern, String path) {
        if (pattern == null || path == null) {
            return false;
        }
        if ("/**".equals(pattern) || "/*".equals(pattern)) {
            return true;
        }
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            if (prefix.isEmpty()) {
                return true;
            }
            return path.equals(prefix) || path.startsWith(prefix + "/");
        }
        if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            return path.equals(prefix) || path.startsWith(prefix + "/");
        }
        return path.equals(pattern);
    }
}
