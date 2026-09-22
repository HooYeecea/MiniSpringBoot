package com.mvc.handler.argument;

final class SimpleTypeConverter {

    private SimpleTypeConverter() {
    }

    static Object defaultForMissing(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == boolean.class) {
            return false;
        }
        if (type == int.class || type == long.class || type == short.class || type == byte.class) {
            return 0;
        }
        if (type == double.class || type == float.class) {
            return 0.0;
        }
        if (type == char.class) {
            return '\0';
        }
        return null;
    }

    static Object convert(String raw, Class<?> type, String name) {
        if (type == String.class) {
            return raw;
        }
        if (type == int.class || type == Integer.class) {
            return Integer.valueOf(raw);
        }
        if (type == long.class || type == Long.class) {
            return Long.valueOf(raw);
        }
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.valueOf(raw);
        }
        if (type == double.class || type == Double.class) {
            return Double.valueOf(raw);
        }
        throw new IllegalArgumentException(
                "Unsupported parameter type " + type.getName() + " for '" + name + "'");
    }
}
