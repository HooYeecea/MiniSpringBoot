package com.mvc.demo;

import com.miniioccontainer.annotation.MyComponent;
import com.mvc.annotation.ControllerAdvice;
import com.mvc.annotation.ExceptionHandler;
import com.mvc.annotation.ResponseBody;
import com.mvc.exception.HttpMessageNotReadableException;
import com.mvc.validation.MethodArgumentNotValidException;
import com.web.HttpResponse;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handlers for MiniMVC demos.
 */
@MyComponent
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex, HttpResponse response) {
        response.setStatus(400, "Bad Request");
        List<Map<String, String>> details = ex.getViolations().stream()
                .map(v -> {
                    Map<String, String> item = new LinkedHashMap<>();
                    item.put("field", v.field());
                    item.put("message", v.message());
                    return item;
                })
                .collect(Collectors.toList());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "validation_failed");
        body.put("details", details);
        return body;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public Map<String, Object> handleBadJson(HttpMessageNotReadableException ex, HttpResponse response) {
        response.setStatus(400, "Bad Request");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "bad_json");
        body.put("message", ex.getMessage());
        return body;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public Map<String, Object> handleIllegalArgument(IllegalArgumentException ex, HttpResponse response) {
        response.setStatus(400, "Bad Request");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "bad_request");
        body.put("message", ex.getMessage());
        return body;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Map<String, Object> handleOther(Exception ex, HttpResponse response) {
        response.setStatus(500, "Internal Server Error");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "internal_error");
        body.put("message", ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage());
        return body;
    }
}
