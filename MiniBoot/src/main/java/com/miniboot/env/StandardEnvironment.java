package com.miniboot.env;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Loads {@code application.properties}, optional {@code application-{profile}.properties},
 * then overlays {@code --key=value} command-line arguments (highest precedence).
 */
public final class StandardEnvironment implements Environment {

    private final Map<String, String> properties = new LinkedHashMap<>();

    private StandardEnvironment() {
    }

    public static StandardEnvironment create(String... args) {
        StandardEnvironment environment = new StandardEnvironment();
        Map<String, String> cli = parseArgs(args);

        String profileFromCli = firstNonBlank(
                cli.get("miniboot.profiles.active"),
                cli.get("spring.profiles.active"));

        environment.loadClasspathProperties("application.properties");

        String profile = profileFromCli;
        if (profile == null || profile.isBlank()) {
            profile = environment.properties.get("miniboot.profiles.active");
        }
        if (profile == null || profile.isBlank()) {
            profile = environment.properties.get("spring.profiles.active");
        }
        if (profile != null && !profile.isBlank()) {
            for (String p : profile.split(",")) {
                String trimmed = p.trim();
                if (!trimmed.isEmpty()) {
                    environment.loadClasspathProperties("application-" + trimmed + ".properties");
                    System.out.println("[MiniBoot] Active profile: " + trimmed);
                }
            }
        }

        environment.properties.putAll(cli);
        System.out.println("[MiniBoot] Environment ready (" + environment.properties.size() + " properties)");
        return environment;
    }

    private void loadClasspathProperties(String location) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = StandardEnvironment.class.getClassLoader();
        }
        try {
            Enumeration<URL> resources = classLoader.getResources(location);
            List<URL> urls = new ArrayList<>();
            while (resources.hasMoreElements()) {
                urls.add(resources.nextElement());
            }
            // Later entries override earlier ones (app module typically last on Maven classpath → first here;
            // load in reverse so the first classpath hit wins, matching ClassLoader.getResource).
            for (int i = urls.size() - 1; i >= 0; i--) {
                merge(urls.get(i), location);
            }
            if (!urls.isEmpty()) {
                System.out.println("[MiniBoot] Loaded " + location + " (" + urls.size() + " resource(s))");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + location, e);
        }
    }

    private void merge(URL url, String location) throws IOException {
        Properties props = new Properties();
        try (InputStream in = url.openStream();
             InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            props.load(reader);
        }
        for (String name : props.stringPropertyNames()) {
            properties.put(name, props.getProperty(name));
        }
    }

    private static Map<String, String> parseArgs(String... args) {
        Map<String, String> result = new LinkedHashMap<>();
        if (args == null) {
            return result;
        }
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg == null || !arg.startsWith("--")) {
                continue;
            }
            String body = arg.substring(2);
            int eq = body.indexOf('=');
            if (eq >= 0) {
                result.put(body.substring(0, eq).trim(), body.substring(eq + 1).trim());
            } else if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                result.put(body.trim(), args[++i]);
            } else {
                result.put(body.trim(), "true");
            }
        }
        return result;
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a;
        }
        if (b != null && !b.isBlank()) {
            return b;
        }
        return null;
    }

    @Override
    public boolean containsProperty(String key) {
        return properties.containsKey(key);
    }

    @Override
    public String getProperty(String key) {
        return properties.get(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        String value = properties.get(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType) {
        String value = properties.get(key);
        if (value == null) {
            return null;
        }
        return convert(value, targetType);
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        if (!containsProperty(key)) {
            return defaultValue;
        }
        return getProperty(key, targetType);
    }

    @SuppressWarnings("unchecked")
    public static <T> T convert(String raw, Class<T> targetType) {
        if (targetType == String.class) {
            return (T) raw;
        }
        if (targetType == int.class || targetType == Integer.class) {
            return (T) Integer.valueOf(raw.trim());
        }
        if (targetType == long.class || targetType == Long.class) {
            return (T) Long.valueOf(raw.trim());
        }
        if (targetType == boolean.class || targetType == Boolean.class) {
            return (T) Boolean.valueOf(raw.trim());
        }
        if (targetType.isEnum()) {
            return (T) Enum.valueOf((Class<? extends Enum>) targetType.asSubclass(Enum.class), raw.trim());
        }
        throw new IllegalArgumentException("Unsupported property type: " + targetType.getName());
    }
}
