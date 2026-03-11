package com.example.blogStudy.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    LocalDateTime timestamp;
    int status;
    private String code;
    private String message;
    String path;


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
