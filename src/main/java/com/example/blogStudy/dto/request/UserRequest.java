package com.example.blogStudy.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRequest {
    private final String id;
    private final String password;
}
