package com.example.blogStudy.dto.response;

import com.example.blogStudy.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponseDto {
    private String id;
    private String name;

    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName()
        );
    }
}
