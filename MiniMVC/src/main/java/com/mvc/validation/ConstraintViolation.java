package com.mvc.validation;

public record ConstraintViolation(String field, String message) {
}
