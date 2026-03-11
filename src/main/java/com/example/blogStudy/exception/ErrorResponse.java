package com.example.blogStudy.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    LocalDateTime timestamp;
    int status;                 // HttpStatus
    private String code;        // Enum Code
    private String message;     // Enum message
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
}
