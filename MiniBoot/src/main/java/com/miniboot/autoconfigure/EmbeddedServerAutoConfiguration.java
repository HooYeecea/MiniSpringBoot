package com.miniboot.autoconfigure;

import com.miniboot.annotation.MiniSpringBootApplication;
import com.miniboot.bind.ConfigurationPropertiesBinder;
import com.miniboot.env.Environment;
import com.miniboot.server.EmbeddedServer;
import com.miniboot.server.EmbeddedServerFactory;
import com.mvc.servlet.DispatcherServlet;

/**
 * Creates the embedded server from Environment / annotation settings and mounts
 * {@link DispatcherServlet} at {@code /*}.
 * <p>
 * Precedence for port/type: Environment ({@code server.*}) &gt; {@link MiniSpringBootApplication}.
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

        ServerProperties serverProperties = resolveServerProperties(context);
        context.setServerProperties(serverProperties);

        EmbeddedServer server = EmbeddedServerFactory.create(
                serverProperties.getType(), serverProperties.getPort());
        server.registerServlet("/*", dispatcherServlet);
        context.setEmbeddedServer(server);

        System.out.println("[MiniBoot] AutoConfig applied: EmbeddedServer ("
                + serverProperties.getType() + ", port=" + serverProperties.getPort() + ")");
        System.out.println("[MiniBoot] DispatcherServlet mapped to /*");
    }

    private static ServerProperties resolveServerProperties(AutoConfigurationContext context) {
        MiniSpringBootApplication boot = context.getBootAnnotation();
        ServerProperties properties = new ServerProperties();
        properties.setPort(boot.port());
        properties.setType(boot.server());

        Environment environment = context.getEnvironment();
        if (environment != null) {
            ConfigurationPropertiesBinder.bind(environment, properties, "server");
        }
        return properties;
    }
}
