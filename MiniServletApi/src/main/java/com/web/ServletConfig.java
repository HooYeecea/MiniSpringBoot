package com.web;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Per-Servlet init configuration (currently init-param only).
 */
public class ServletConfig {

    private final Map<String, String> initParameters;

    public ServletConfig(Map<String, String> initParameters) {
        if (initParameters == null || initParameters.isEmpty()) {
            this.initParameters = Map.of();
        } else {
            this.initParameters = Collections.unmodifiableMap(new LinkedHashMap<>(initParameters));
        }
    }

    public String getInitParameter(String name) {
        return initParameters.get(name);
    }
}
