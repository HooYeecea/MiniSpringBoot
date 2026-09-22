package com.miniboot;

import com.miniboot.annotation.EnableAutoConfiguration;
import com.miniboot.annotation.MiniSpringBootApplication;
import com.miniboot.autoconfigure.AutoConfiguration;
import com.miniboot.autoconfigure.AutoConfigurationContext;
import com.miniboot.autoconfigure.AutoConfigurationLoader;
import com.miniboot.autoconfigure.ServerProperties;
import com.miniboot.env.Environment;
import com.miniboot.env.StandardEnvironment;
import com.miniboot.server.EmbeddedServer;
import com.miniioccontainer.context.MiniApplicationContext;

import java.util.List;

/**
 * Boot-style entry: create Environment + IoC context, apply auto-configurations, start server.
 * <p>
 * Usage:
 * <pre>{@code
 * @MiniSpringBootApplication
 * public class App {
 *     public static void main(String[] args) {
 *         MiniSpringApplication.run(App.class, args);
 *     }
 * }
 * }</pre>
 */
public final class MiniSpringApplication {

    private MiniSpringApplication() {
    }

    /**
     * Boots the application and blocks on the embedded HTTP server.
     *
     * @param primarySource class annotated with {@link MiniSpringBootApplication}
     * @param args          {@code --key=value} overlays (e.g. {@code --server.port=9090})
     * @return the IoC context (unreachable while the accept-loop runs; useful for tests later)
     */
    public static MiniApplicationContext run(Class<?> primarySource, String... args) {
        if (primarySource == null) {
            throw new IllegalArgumentException("primarySource must not be null");
        }
        MiniSpringBootApplication boot = primarySource.getAnnotation(MiniSpringBootApplication.class);
        if (boot == null) {
            throw new IllegalArgumentException(
                    primarySource.getName() + " must be annotated with @MiniSpringBootApplication");
        }

        Environment environment = StandardEnvironment.create(args);

        String scanPackage = resolveScanPackage(primarySource, boot);
        System.out.println("[MiniBoot] Starting " + primarySource.getSimpleName());
        System.out.println("[MiniBoot] Scanning package: " + scanPackage);

        MiniApplicationContext applicationContext = new MiniApplicationContext(scanPackage);
        AutoConfigurationContext autoContext =
                new AutoConfigurationContext(applicationContext, boot, primarySource, environment);

        if (isAutoConfigurationEnabled(primarySource)) {
            List<AutoConfiguration> configurations = AutoConfigurationLoader.load();
            for (AutoConfiguration configuration : configurations) {
                configuration.configure(autoContext);
            }
        } else {
            System.out.println("[MiniBoot] @EnableAutoConfiguration not present; skip auto-config");
        }

        EmbeddedServer server = autoContext.getEmbeddedServer();
        if (server == null) {
            throw new IllegalStateException(
                    "No EmbeddedServer was auto-configured; check META-INF/miniboot.factories "
                            + "and classpath (mini-mvc / MiniTomcat)");
        }

        ServerProperties serverProperties = autoContext.getServerProperties();
        int port = serverProperties != null ? serverProperties.getPort() : boot.port();
        System.out.println("[MiniBoot] Try: http://localhost:" + port + "/mvc/hello");
        System.out.println("[MiniBoot] Try: http://localhost:" + port + "/api/ping");

        try {
            server.start();
        } catch (Exception e) {
            server.stop();
            String type = serverProperties != null ? String.valueOf(serverProperties.getType())
                    : String.valueOf(boot.server());
            throw new IllegalStateException("Failed to start embedded server (" + type + ")", e);
        }
        return applicationContext;
    }

    private static boolean isAutoConfigurationEnabled(Class<?> primarySource) {
        if (primarySource.isAnnotationPresent(EnableAutoConfiguration.class)) {
            return true;
        }
        return primarySource.isAnnotationPresent(MiniSpringBootApplication.class)
                && MiniSpringBootApplication.class.isAnnotationPresent(EnableAutoConfiguration.class);
    }

    private static String resolveScanPackage(Class<?> primarySource, MiniSpringBootApplication boot) {
        String[] packages = boot.scanBasePackages();
        if (packages != null && packages.length > 0 && packages[0] != null && !packages[0].isBlank()) {
            if (packages.length > 1) {
                System.out.println("[MiniBoot] Multiple scanBasePackages listed; using only the first: "
                        + packages[0]);
            }
            return packages[0].trim();
        }
        Package pkg = primarySource.getPackage();
        if (pkg == null || pkg.getName() == null || pkg.getName().isEmpty()) {
            throw new IllegalStateException(
                    "Cannot infer scan package from " + primarySource.getName()
                            + "; set @MiniSpringBootApplication(scanBasePackages = \"...\")");
        }
        return pkg.getName();
    }
}
