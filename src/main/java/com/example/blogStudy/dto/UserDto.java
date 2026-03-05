package com.example.blogStudy.dto;

import com.example.blogStudy.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {
    private String id;
    private String password;
    private String name;

    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getPassword(),
                user.getName()
        );
    }

    public User toEntity() {
        return User.create(this.id, this.password, this.name);
    }
}
