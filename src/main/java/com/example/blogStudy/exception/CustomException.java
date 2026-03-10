package com.example.blogStudy.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());  // RuntimeException 생성자 호출
        this.errorCode = errorCode;
    }
}

