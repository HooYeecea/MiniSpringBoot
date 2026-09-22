package com.miniboot.autoconfigure;

/**
 * Evaluates simple conditions on auto-configuration classes.
 */
public final class ConditionEvaluator {

    private ConditionEvaluator() {
    }

    public static boolean matches(Class<?> configurationClass) {
        ConditionalOnClass onClass = configurationClass.getAnnotation(ConditionalOnClass.class);
        if (onClass == null) {
            return true;
        }
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = configurationClass.getClassLoader();
        }
        for (String className : onClass.value()) {
            if (!isPresent(className, classLoader)) {
                System.out.println("[MiniBoot] Skip " + configurationClass.getSimpleName()
                        + " (missing class: " + className + ")");
                return false;
            }
        }
        return true;
    }

    private static boolean isPresent(String className, ClassLoader classLoader) {
        try {
            Class.forName(className, false, classLoader);
            return true;
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return false;
        }
    }
}
