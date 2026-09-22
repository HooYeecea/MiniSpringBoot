package com.minitomcat;

import com.web.Filter;
import com.web.FilterChain;
import com.web.Servlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandleRequest {

    private static final Map<String, Servlet> SERVLET_MAP = new HashMap<>();
    private static final List<FilterMapping> FILTER_MAPPINGS = new ArrayList<>();

    static {
        SERVLET_MAP.put("/", (req, resp) -> resp.setBody("<h1>Welcome to MiniTomcat Home Page</h1>"));
        SERVLET_MAP.put("/hello", new HelloServlet());
        SERVLET_MAP.put("/time", new TimeServlet());

        FILTER_MAPPINGS.add(new FilterMapping("/*", new LogFilter()));
        FILTER_MAPPINGS.add(new FilterMapping("/*", new TimerFilter()));
        FILTER_MAPPINGS.add(new FilterMapping("/hello", new HelloFilter()));
    }

    public static void resetMappings() {
        SERVLET_MAP.clear();
        FILTER_MAPPINGS.clear();
    }

    public static void registerServlet(String path, Servlet servlet) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path must not be empty");
        }
        if (servlet == null) {
            throw new IllegalArgumentException("servlet must not be null");
        }
        SERVLET_MAP.put(path, servlet);
    }

    public static void registerFilter(String urlPattern, Filter filter) {
        FILTER_MAPPINGS.add(new FilterMapping(urlPattern, filter));
    }

    private static String pathOnly(String url) {
        if (url == null || url.isEmpty()) {
            return "/";
        }
        int query = url.indexOf('?');
        String path = query >= 0 ? url.substring(0, query) : url;
        return path.isEmpty() ? "/" : path;
    }

    private static Servlet findServlet(String path) {
        Servlet exact = SERVLET_MAP.get(path);
        if (exact != null) {
            return exact;
        }
        return SERVLET_MAP.get("/*");
    }

    private static List<Filter> matchFilters(String path) {
        List<Filter> matched = new ArrayList<>();
        for (FilterMapping mapping : FILTER_MAPPINGS) {
            if (mapping.matches(path)) {
                matched.add(mapping.getFilter());
            }
        }
        return matched;
    }

    public static void handleRequest(Socket socket) {

        try (socket) {
            System.out.println("处理请求");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.ISO_8859_1)
            );
            String requestLine = reader.readLine();
            System.out.println("接收到请求，请求内容为:" + requestLine);
            if (requestLine == null || requestLine.isEmpty()) {
                System.out.println("请求为空为空");
                return;
            }
            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String url = parts[1];
            String version = parts[2];

            BioHttpRequest request = new BioHttpRequest();
            request.setMethod(method);
            request.setUrl(url);
            request.setVersion(version);

            String headerLine;
            while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
                int colon = headerLine.indexOf(':');
                if (colon <= 0) {
                    continue;
                }
                String name = headerLine.substring(0, colon).trim();
                String value = headerLine.substring(colon + 1).trim();
                request.setHeader(name, value);
            }

            int contentLength = 0;
            String lengthHeader = request.getHeader("content-length");
            if (lengthHeader != null && !lengthHeader.isEmpty()) {
                contentLength = Integer.parseInt(lengthHeader);
            }
            if (contentLength > 0) {
                char[] buf = new char[contentLength];
                int offset = 0;
                while (offset < contentLength) {
                    int read = reader.read(buf, offset, contentLength - offset);
                    if (read < 0) {
                        break;
                    }
                    offset += read;
                }
                request.setBody(new String(buf, 0, offset).getBytes(StandardCharsets.UTF_8));
            }

            String path = pathOnly(url);
            BioHttpResponse response = new BioHttpResponse(socket);

            Servlet servlet = findServlet(path);
            FilterChain chain = new ApplicationFilterChain(matchFilters(path), servlet);
            chain.doFilter(request, response);

            response.write();

        } catch (Exception e) {
            System.err.println("处理请求时发生异常: " + e.getMessage());
        }
    }
}
