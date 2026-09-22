package com.miniboot.autoconfigure;

import com.miniboot.annotation.MiniSpringBootApplication;
import com.miniboot.server.EmbeddedServer;
import com.miniboot.server.EmbeddedServerFactory;
import com.mvc.servlet.DispatcherServlet;

/**
 * Creates the embedded server from {@link MiniSpringBootApplication} settings and mounts
 * {@link DispatcherServlet} at {@code /*}.
 */
@ConditionalOnClass({
        "com.miniboot.server.EmbeddedServer",
        "com.mvc.servlet.DispatcherServlet"
})
public class EmbeddedServerAutoConfiguration implements AutoConfiguration {

    @Override
    public void configure(AutoConfigurationContext context) {
        DispatcherServlet dispatcherServlet = context.getDispatcherServlet();
        if (dispatcherServlet == null) {
            throw new IllegalStateException(
                    "DispatcherServlet is missing; ensure DispatcherServletAutoConfiguration runs first");
        }

        MiniSpringBootApplication boot = context.getBootAnnotation();
        EmbeddedServer server = EmbeddedServerFactory.create(boot.server(), boot.port());
        server.registerServlet("/*", dispatcherServlet);
        context.setEmbeddedServer(server);

        System.out.println("[MiniBoot] AutoConfig applied: EmbeddedServer ("
                + boot.server() + ", port=" + boot.port() + ")");
        System.out.println("[MiniBoot] DispatcherServlet mapped to /*");
    }
}
