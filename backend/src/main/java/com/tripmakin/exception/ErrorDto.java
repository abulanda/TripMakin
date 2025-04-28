package com.tripmakin.exception;

import java.time.Instant;

public record ErrorDto(String error,
                       int status,
                       String path,
                       Instant timestamp) {

    public static ErrorDto of(String message, int status, String path) {
        return new ErrorDto(message, status, path, Instant.now());
    }
}
