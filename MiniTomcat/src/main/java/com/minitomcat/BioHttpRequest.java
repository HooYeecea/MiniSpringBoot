package com.minitomcat;

import com.web.HttpRequest;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * BIO connector request: method / URI / headers / query / body.
 */
public class BioHttpRequest implements HttpRequest {
    private String method;
    private String url;
    private String version;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private final Map<String, String> parameters = new LinkedHashMap<>();
    private byte[] body = new byte[0];

    public BioHttpRequest() {
    }

    @Override
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    /** @deprecated prefer {@link #getUri()} / {@link #getPath()} */
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        parameters.clear();
        parseQueryString();
    }

    @Override
    public String getUri() {
        return url;
    }

    @Override
    public String getPath() {
        if (url == null || url.isEmpty()) {
            return "/";
        }
        int query = url.indexOf('?');
        String path = query >= 0 ? url.substring(0, query) : url;
        return path.isEmpty() ? "/" : path;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setHeader(String name, String value) {
        if (name == null) {
            return;
        }
        headers.put(name.toLowerCase(), value);
    }

    @Override
    public String getHeader(String name) {
        if (name == null) {
            return null;
        }
        return headers.get(name.toLowerCase());
    }

    @Override
    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public void setBody(byte[] body) {
        this.body = body == null ? new byte[0] : body;
    }

    @Override
    public byte[] getBody() {
        return body;
    }

    @Override
    public String getBodyAsString() {
        return new String(body, StandardCharsets.UTF_8);
    }

    @Override
    public String getParameter(String name) {
        return parameters.get(name);
    }

    @Override
    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    private void parseQueryString() {
        if (url == null) {
            return;
        }
        int query = url.indexOf('?');
        if (query < 0 || query == url.length() - 1) {
            return;
        }
        for (String pair : url.substring(query + 1).split("&")) {
            if (pair.isEmpty()) {
                continue;
            }
            int eq = pair.indexOf('=');
            String rawName = eq >= 0 ? pair.substring(0, eq) : pair;
            String rawValue = eq >= 0 ? pair.substring(eq + 1) : "";
            parameters.putIfAbsent(decode(rawName), decode(rawValue));
        }
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return value;
        }
    }
}
