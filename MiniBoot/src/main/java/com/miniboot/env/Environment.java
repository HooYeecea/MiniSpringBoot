package com.miniboot.env;

/**
 * Read-only view of MiniBoot configuration sources (properties files + command-line args).
 */
public interface Environment {

    boolean containsProperty(String key);

    String getProperty(String key);

    String getProperty(String key, String defaultValue);

    <T> T getProperty(String key, Class<T> targetType);

    <T> T getProperty(String key, Class<T> targetType, T defaultValue);
}
