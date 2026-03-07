package com.example.blogStudy.dto.update;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdateDto {
    private String password;
    private String name;
}

// User 엔티티에 update() 메서드 작성 필요
