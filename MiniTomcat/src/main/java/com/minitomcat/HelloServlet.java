package com.minitomcat;

import com.web.HttpRequest;
import com.web.HttpResponse;
import com.web.Servlet;

public class HelloServlet implements Servlet {
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        response.setBody("<h1>Hello, MiniTomcat!</h1>");
    }
}
