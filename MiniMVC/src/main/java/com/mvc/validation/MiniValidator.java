package com.mvc.validation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Tiny bean validator for {@link NotNull} / {@link NotBlank} / {@link Min} / {@link Max}.
 */
public final class MiniValidator {

    private MiniValidator() {
    }

    public static void validate(Object target) {
        if (target == null) {
            throw new MethodArgumentNotValidException(
                    List.of(new ConstraintViolation("", "target must not be null")));
        }
        List<ConstraintViolation> violations = new ArrayList<>();
        Class<?> type = target.getClass();
        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            Object value;
            try {
                value = field.get(target);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
            NotNull notNull = field.getAnnotation(NotNull.class);
            if (notNull != null && value == null) {
                violations.add(new ConstraintViolation(field.getName(), notNull.message()));
            }
            NotBlank notBlank = field.getAnnotation(NotBlank.class);
            if (notBlank != null) {
                if (value == null || value.toString().isBlank()) {
                    violations.add(new ConstraintViolation(field.getName(), notBlank.message()));
                }
            }
            Min min = field.getAnnotation(Min.class);
            if (min != null && value instanceof Number number) {
                if (number.longValue() < min.value()) {
                    violations.add(new ConstraintViolation(
                            field.getName(),
                            min.message().replace("{value}", String.valueOf(min.value()))));
                }
            }
            Max max = field.getAnnotation(Max.class);
            if (max != null && value instanceof Number number) {
                if (number.longValue() > max.value()) {
                    violations.add(new ConstraintViolation(
                            field.getName(),
                            max.message().replace("{value}", String.valueOf(max.value()))));
                }
            }
        }
        if (!violations.isEmpty()) {
            throw new MethodArgumentNotValidException(violations);
        }
    }
}
