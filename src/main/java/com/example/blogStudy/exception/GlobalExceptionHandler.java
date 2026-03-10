package com.example.blogStudy.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice   // 프로젝트 전체 Controller 에서 발생하는 예외 처리 클래스
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)    // CustomException 발생 시 메서드 실행
    public ResponseEntity<ErrorResponseDto> handleCustomException(CustomException e) {

        ErrorCode errorCode = e.getErrorCode();

        ErrorResponseDto responseDto = new ErrorResponseDto(
                errorCode.getCode(),
                errorCode.getMessage()
        );

        return new ResponseEntity<>(responseDto, errorCode.getStatus());
    }
}
