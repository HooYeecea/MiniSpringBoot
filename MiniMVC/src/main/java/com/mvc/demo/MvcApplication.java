package com.mvc.demo;

import com.miniioccontainer.context.MiniApplicationContext;
import com.minitomcat.HandleRequest;
import com.minitomcat.HttpServer;
import com.mvc.handler.HandlerMethod;
import com.mvc.servlet.DispatcherServlet;
import com.web.HttpRequest;
import com.web.HttpResponse;

/**
 * Boots MiniIOC + MiniMVC {@link DispatcherServlet} on MiniTomcat (BIO).
 * <p>
 * Run from the parent reactor so dependencies resolve:
 * {@code mvn -pl MiniMVC -am exec:java -Dexec.mainClass=com.mvc.demo.MvcApplication}
 */
public class MvcApplication {

    public static void main(String[] args) throws Exception {
        MiniApplicationContext context = new MiniApplicationContext("com.mvc.demo");
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        dispatcherServlet.addInterceptor(new LoggingInterceptor(), "/**");
        // API-only interceptor path demo
        dispatcherServlet.addInterceptor(new LoggingInterceptor() {
            @Override
            public boolean preHandle(HttpRequest request, HttpResponse response, HandlerMethod handler) {
                System.out.println("[Interceptor:/api] " + request.getPath());
                return true;
            }
        }, "/api/**");
        dispatcherServlet.init();

        HandleRequest.resetMappings();
        HandleRequest.registerServlet("/*", dispatcherServlet);

        System.out.println("[MiniMVC] Demo routes:");
        System.out.println("  GET http://localhost:8080/mvc/hello");
        System.out.println("  GET http://localhost:8080/mvc/echo?name=MiniSpring");
        System.out.println("  GET http://localhost:8080/mvc/add?a=1&b=2");
        System.out.println("  GET http://localhost:8080/api/ping");
        System.out.println("  GET http://localhost:8080/api/user?id=7");
        System.out.println("  GET http://localhost:8080/api/users/7");
        System.out.println("  POST http://localhost:8080/api/users  (JSON body)");
        HttpServer.main(args);
    }
}
