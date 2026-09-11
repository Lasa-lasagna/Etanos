package com.lasa.gloria.common.exception;

import java.time.LocalDateTime;

public record ApiError(
        int status,
        String error,
        String message,
        String errorCode,
        LocalDateTime timestamp
) {
    public static ApiError of(int status, String error, String message, String errorCode) {
        return new ApiError(status, error, message, errorCode, LocalDateTime.now());
    }
}
