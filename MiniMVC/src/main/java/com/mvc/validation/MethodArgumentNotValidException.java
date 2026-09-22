package com.mvc.validation;

import java.util.Collections;
import java.util.List;

/**
 * Thrown when {@code @Valid} validation fails.
 */
public class MethodArgumentNotValidException extends RuntimeException {

    private final List<ConstraintViolation> violations;

    public MethodArgumentNotValidException(List<ConstraintViolation> violations) {
        super("Validation failed: " + violations);
        this.violations = List.copyOf(violations);
    }

    public List<ConstraintViolation> getViolations() {
        return Collections.unmodifiableList(violations);
    }
}
