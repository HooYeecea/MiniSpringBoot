package com.minitomcat;

import com.web.HttpRequest;
import com.web.HttpResponse;
import com.web.Servlet;

import java.util.Date;

public class TimeServlet implements Servlet {
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        response.setBody("<h1>" + new Date() + "</h1>");
    }
}
