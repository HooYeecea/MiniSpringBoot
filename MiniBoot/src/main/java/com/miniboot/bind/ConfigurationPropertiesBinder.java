package com.miniboot.bind;

import com.miniboot.annotation.ConfigurationProperties;
import com.miniboot.env.Environment;
import com.miniboot.env.StandardEnvironment;

import java.lang.reflect.Field;

/**
 * Binds {@link Environment} values onto an object annotated with {@link ConfigurationProperties}.
 * Only sets a field when the corresponding property key is present (keeps annotation/defaults otherwise).
 */
public final class ConfigurationPropertiesBinder {

    private ConfigurationPropertiesBinder() {
    }

    public static <T> T bind(Environment environment, Class<T> type) {
        if (environment == null || type == null) {
            throw new IllegalArgumentException("environment and type must not be null");
        }
        ConfigurationProperties annotation = type.getAnnotation(ConfigurationProperties.class);
        if (annotation == null) {
            throw new IllegalArgumentException(type.getName() + " lacks @ConfigurationProperties");
        }
        try {
            T instance = type.getDeclaredConstructor().newInstance();
            bind(environment, instance, annotation.prefix());
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to bind " + type.getName(), e);
        }
    }

    public static void bind(Environment environment, Object target) {
        if (target == null) {
            throw new IllegalArgumentException("target must not be null");
        }
        ConfigurationProperties annotation = target.getClass().getAnnotation(ConfigurationProperties.class);
        if (annotation == null) {
            throw new IllegalArgumentException(target.getClass().getName() + " lacks @ConfigurationProperties");
        }
        bind(environment, target, annotation.prefix());
    }

    public static void bind(Environment environment, Object target, String prefix) {
        if (environment == null || target == null) {
            throw new IllegalArgumentException("environment and target must not be null");
        }
        if (prefix == null || prefix.isBlank()) {
            throw new IllegalArgumentException("prefix must not be blank");
        }
        String normalized = prefix.endsWith(".") ? prefix.substring(0, prefix.length() - 1) : prefix.trim();

        for (Field field : target.getClass().getDeclaredFields()) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            String key = normalized + "." + field.getName();
            if (!environment.containsProperty(key)) {
                continue;
            }
            String raw = environment.getProperty(key);
            Object value = StandardEnvironment.convert(raw, field.getType());
            field.setAccessible(true);
            try {
                field.set(target, value);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Cannot set field " + field.getName() + " on "
                        + target.getClass().getSimpleName(), e);
            }
        }
    }
}
