package com.example.blogStudy.service;

import com.example.blogStudy.dto.create.UserCreate;
import com.example.blogStudy.dto.request.UserRequest;
import com.example.blogStudy.entity.User;
import com.example.blogStudy.exception.CustomException;
import com.example.blogStudy.exception.ErrorCode;
import com.example.blogStudy.jwt.JwtProperties;
import com.example.blogStudy.jwt.JwtProvider;
import com.example.blogStudy.jwt.JwtTokenResponse;
import com.example.blogStudy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    public JwtTokenResponse login(UserRequest dto) {
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!user.getPassword().equals(dto.getPassword()))
            throw new CustomException(ErrorCode.INVALID_PASSWORD);

        String accessToken = jwtProvider.createAccessToken(user.getId());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        refreshTokenService.save(
                user.getId(),
                refreshToken,
                jwtProperties.getRefreshTokenExpiration()
        );

        return new JwtTokenResponse(accessToken, refreshToken);
    }
}
