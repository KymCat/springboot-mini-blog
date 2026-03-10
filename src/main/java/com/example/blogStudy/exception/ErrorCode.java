package com.example.blogStudy.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND      (HttpStatus.NOT_FOUND, "USER-001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_USER_ID   (HttpStatus.BAD_REQUEST, "USER-002","이미 존재하는 ID 입니다.");

    // 상수화를 위한 final
    private final HttpStatus status;
    private final String code;
    private final String message;
}



