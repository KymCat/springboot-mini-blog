package com.example.blogStudy.dto.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PasswordUpdate {
    private String currentPassword;
    private String newPassword;
}

// User 엔티티에 update() 메서드 작성 필요
