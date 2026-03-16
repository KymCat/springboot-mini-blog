package com.example.blogStudy.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtTokenResponse {
    private final String accessToken;
    private final String refreshToken;
}
