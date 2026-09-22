package com.miniboot.autoconfigure;

import com.miniioccontainer.context.MiniApplicationContext;
import com.mvc.servlet.DispatcherServlet;

/**
 * Creates and initializes {@link DispatcherServlet} when MiniMVC is on the classpath.
 */
@ConditionalOnClass("com.mvc.servlet.DispatcherServlet")
public class DispatcherServletAutoConfiguration implements AutoConfiguration {

    @Override
    public void configure(AutoConfigurationContext context) {
        MiniApplicationContext applicationContext = context.getApplicationContext();
        DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);
        dispatcherServlet.init();
        context.setDispatcherServlet(dispatcherServlet);
        System.out.println("[MiniBoot] AutoConfig applied: DispatcherServlet");
    }
}
