package com.example.blogStudy.dto.create;

import com.example.blogStudy.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserCreate {
    private String id;
    private String password;
    private String name;

    public User toEntity() {
        return User.create(this.id, this.password, this.name);
    }
}
