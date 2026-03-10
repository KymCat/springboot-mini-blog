package com.example.blogStudy.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponseDto {
    LocalDateTime timestamp;
    int status;
    private String code;
    private String message;
    String path;


    public static ErrorResponseDto of(ErrorCode errorCode, String path) {
        return new ErrorResponseDto(
                LocalDateTime.now(),
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getMessage(),
                path
        );
    }
}
