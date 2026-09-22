package com.miniboot.autoconfigure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Loads {@link AutoConfiguration} class names from {@code META-INF/miniboot.factories}
 * (Spring {@code spring.factories} style, teaching subset).
 */
public final class AutoConfigurationLoader {

    public static final String FACTORIES_RESOURCE = "META-INF/miniboot.factories";
    public static final String AUTO_CONFIGURATION_KEY = AutoConfiguration.class.getName();

    private AutoConfigurationLoader() {
    }

    public static List<AutoConfiguration> load() {
        Set<String> classNames = loadClassNames();
        List<AutoConfiguration> configurations = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = AutoConfigurationLoader.class.getClassLoader();
        }
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className, true, classLoader);
                if (!AutoConfiguration.class.isAssignableFrom(clazz)) {
                    throw new IllegalStateException(className + " does not implement AutoConfiguration");
                }
                if (!ConditionEvaluator.matches(clazz)) {
                    continue;
                }
                AutoConfiguration instance = (AutoConfiguration) clazz.getDeclaredConstructor().newInstance();
                configurations.add(instance);
                System.out.println("[MiniBoot] AutoConfig candidate: " + clazz.getSimpleName());
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Failed to load auto-configuration: " + className, e);
            }
        }
        return configurations;
    }

    private static Set<String> loadClassNames() {
        Set<String> names = new LinkedHashSet<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = AutoConfigurationLoader.class.getClassLoader();
        }
        try {
            Enumeration<URL> resources = classLoader.getResources(FACTORIES_RESOURCE);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                parseFactories(url, names);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + FACTORIES_RESOURCE, e);
        }
        return names;
    }

    private static void parseFactories(URL url, Set<String> names) throws IOException {
        try (InputStream in = url.openStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            String currentKey = null;
            StringBuilder value = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                line = stripComment(line).trim();
                if (line.isEmpty()) {
                    continue;
                }
                boolean continuation = line.endsWith("\\");
                if (continuation) {
                    line = line.substring(0, line.length() - 1).trim();
                }
                int eq = line.indexOf('=');
                if (eq >= 0 && currentKey == null) {
                    currentKey = line.substring(0, eq).trim();
                    value.append(line.substring(eq + 1).trim());
                } else if (currentKey != null) {
                    if (!value.isEmpty() && !value.toString().endsWith(",")) {
                        // keep comma-separated list intact across continuations
                    }
                    value.append(line);
                } else {
                    throw new IllegalStateException("Malformed factories line in " + url + ": " + line);
                }
                if (!continuation) {
                    if (AUTO_CONFIGURATION_KEY.equals(currentKey)) {
                        splitClassNames(value.toString(), names);
                    }
                    currentKey = null;
                    value.setLength(0);
                }
            }
            if (currentKey != null && AUTO_CONFIGURATION_KEY.equals(currentKey)) {
                splitClassNames(value.toString(), names);
            }
        }
    }

    private static String stripComment(String line) {
        int hash = line.indexOf('#');
        return hash >= 0 ? line.substring(0, hash) : line;
    }

    private static void splitClassNames(String csv, Set<String> names) {
        for (String part : csv.split(",")) {
            String name = part.trim();
            if (!name.isEmpty()) {
                names.add(name);
            }
        }
    }
}
