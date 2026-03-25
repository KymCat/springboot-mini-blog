package com.example.blogStudy.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    LocalDateTime timestamp;
    private final int status;                 // HttpStatus
    private final String code;        // Enum Code
    private final String message;     // Enum message
    String path;                // request URI


    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return new ErrorResponse(
                LocalDateTime.now(),
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getMessage(),
                path
        );
    }

    public static ErrorResponse of(int status, String code, String message, String path) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status,
                code,
                message,
                path
        );
    }
}
