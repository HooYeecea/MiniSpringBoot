package com.miniboot.autoconfigure;

import com.miniboot.annotation.MiniSpringBootApplication;
import com.miniboot.server.EmbeddedServer;
import com.miniboot.env.Environment;
import com.miniioccontainer.context.MiniApplicationContext;
import com.mvc.servlet.DispatcherServlet;

/**
 * Mutable bag shared across auto-configurations during {@code MiniSpringApplication.run}.
 */
public final class AutoConfigurationContext {

    private final MiniApplicationContext applicationContext;
    private final MiniSpringBootApplication bootAnnotation;
    private final Class<?> primarySource;
    private final Environment environment;

    private DispatcherServlet dispatcherServlet;
    private EmbeddedServer embeddedServer;
    private ServerProperties serverProperties;

    public AutoConfigurationContext(MiniApplicationContext applicationContext,
                                    MiniSpringBootApplication bootAnnotation,
                                    Class<?> primarySource,
                                    Environment environment) {
        this.applicationContext = applicationContext;
        this.bootAnnotation = bootAnnotation;
        this.primarySource = primarySource;
        this.environment = environment;
    }

    public MiniApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public MiniSpringBootApplication getBootAnnotation() {
        return bootAnnotation;
    }

    public Class<?> getPrimarySource() {
        return primarySource;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public DispatcherServlet getDispatcherServlet() {
        return dispatcherServlet;
    }

    public void setDispatcherServlet(DispatcherServlet dispatcherServlet) {
        this.dispatcherServlet = dispatcherServlet;
    }

    public EmbeddedServer getEmbeddedServer() {
        return embeddedServer;
    }

    public void setEmbeddedServer(EmbeddedServer embeddedServer) {
        this.embeddedServer = embeddedServer;
    }

    public ServerProperties getServerProperties() {
        return serverProperties;
    }

    public void setServerProperties(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }
}
