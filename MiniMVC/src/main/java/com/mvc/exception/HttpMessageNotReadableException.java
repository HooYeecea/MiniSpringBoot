package com.mvc.exception;

/**
 * Thrown when the request body cannot be read / parsed.
 */
public class HttpMessageNotReadableException extends RuntimeException {

    public HttpMessageNotReadableException(String message, Throwable cause) {
        super(message, cause);
    }
}
