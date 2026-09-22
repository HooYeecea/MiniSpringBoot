package com.miniboot.autoconfigure;

import com.miniboot.annotation.MiniSpringBootApplication;
import com.miniboot.server.EmbeddedServer;
import com.miniioccontainer.context.MiniApplicationContext;
import com.mvc.servlet.DispatcherServlet;

/**
 * Mutable bag shared across auto-configurations during {@code MiniSpringApplication.run}.
 */
public final class AutoConfigurationContext {

    private final MiniApplicationContext applicationContext;
    private final MiniSpringBootApplication bootAnnotation;
    private final Class<?> primarySource;

    private DispatcherServlet dispatcherServlet;
    private EmbeddedServer embeddedServer;

    public AutoConfigurationContext(MiniApplicationContext applicationContext,
                                    MiniSpringBootApplication bootAnnotation,
                                    Class<?> primarySource) {
        this.applicationContext = applicationContext;
        this.bootAnnotation = bootAnnotation;
        this.primarySource = primarySource;
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
}
