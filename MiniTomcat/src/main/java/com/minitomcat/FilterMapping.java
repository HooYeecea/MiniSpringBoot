package com.minitomcat;

import com.web.Filter;

/**
 * Filter 与 URL 模式的绑定，对应 Tomcat 里的 FilterMap。
 * 支持三种常见写法：精确路径、前缀 {@code /xxx/*}、通配 {@code /*}。
 */
public class FilterMapping {
    private final String urlPattern;
    private final Filter filter;

    public FilterMapping(String urlPattern, Filter filter) {
        this.urlPattern = urlPattern;
        this.filter = filter;
    }

    public Filter getFilter() {
        return filter;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public boolean matches(String url) {
        if (urlPattern == null || url == null) {
            return false;
        }
        if ("/*".equals(urlPattern) || "*".equals(urlPattern)) {
            return true;
        }
        if (urlPattern.startsWith("*.")) {
            return url.endsWith(urlPattern.substring(1));
        }
        if (urlPattern.endsWith("/*")) {
            String prefix = urlPattern.substring(0, urlPattern.length() - 2);
            return url.equals(prefix) || url.startsWith(prefix + "/");
        }
        return url.equals(urlPattern);
    }
}
