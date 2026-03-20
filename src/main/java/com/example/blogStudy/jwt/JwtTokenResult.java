package com.example.blogStudy.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtTokenResult {

    // AuthController Login Service Result
    private final String accessToken;
    private final String refreshToken;
    private final Long refreshTokenExpiration;
}
