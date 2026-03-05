package com.example.blogStudy.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {
    @Id
    @Column(length = 30, name = "user_id")
    private String id;

    @Column(nullable = false, length = 255, name = "user_pw")
    private String password;

    @Column(nullable = false, length = 30, name = "user_name")
    private String name;

    public static User create(String id, String password, String name) {
        User user = new User();
        user.id = id;
        user.password = password;
        user.name = name;
        return user;
    }
}
