package com.miniboot;

import com.miniboot.annotation.MiniSpringBootApplication;
import com.miniioccontainer.context.MiniApplicationContext;
import com.minitomcat.HandleRequest;
import com.minitomcat.HttpServer;
import com.mvc.servlet.DispatcherServlet;

/**
 * Boot-style entry: create IoC context, mount {@link DispatcherServlet}, start BIO MiniTomcat.
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
     * Boots the application and blocks on the embedded BIO HTTP server (port 8080).
     *
     * @param primarySource class annotated with {@link MiniSpringBootApplication}
     * @param args          forwarded to the server (currently unused by MiniTomcat)
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

        String scanPackage = resolveScanPackage(primarySource, boot);
        System.out.println("[MiniBoot] Starting " + primarySource.getSimpleName());
        System.out.println("[MiniBoot] Scanning package: " + scanPackage);

        MiniApplicationContext context = new MiniApplicationContext(scanPackage);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        dispatcherServlet.init();

        HandleRequest.resetMappings();
        HandleRequest.registerServlet("/*", dispatcherServlet);

        System.out.println("[MiniBoot] DispatcherServlet mapped to /*");
        System.out.println("[MiniBoot] Try: http://localhost:8080/mvc/hello");
        System.out.println("[MiniBoot] Try: http://localhost:8080/api/ping");

        try {
            HttpServer.main(args != null ? args : new String[0]);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to start embedded MiniTomcat", e);
        }
        return context;
    }

    private static String resolveScanPackage(Class<?> primarySource, MiniSpringBootApplication boot) {
        String[] packages = boot.scanBasePackages();
        if (packages != null && packages.length > 0 && packages[0] != null && !packages[0].isBlank()) {
            if (packages.length > 1) {
                System.out.println("[MiniBoot] Multiple scanBasePackages listed; Step 1 uses only the first: "
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
