package com.example.blogStudy.auth;

import com.example.blogStudy.dto.request.UserRequest;
import com.example.blogStudy.entity.User;
import com.example.blogStudy.exception.CustomException;
import com.example.blogStudy.exception.ErrorCode;
import com.example.blogStudy.jwt.JwtProperties;
import com.example.blogStudy.jwt.JwtProvider;
import com.example.blogStudy.jwt.JwtTokenResponse;
import com.example.blogStudy.repository.UserRepository;
import com.example.blogStudy.jwt.RefreshTokenService;
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

        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getName());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        refreshTokenService.save(
                user.getId(),
                refreshToken,
                jwtProperties.getRefreshTokenExpiration()
        );

        return new JwtTokenResponse(accessToken, refreshToken);
    }


    // refresh token 재발행
    public JwtTokenResponse reissue(String token) {
        // refresh token 검증
        if(Boolean.FALSE.equals(jwtProvider.validateToken(token)))
            throw new CustomException(ErrorCode.INVALID_TOKEN);

        // dto 데이터에서 user id 추출
        String userId = jwtProvider.getUserId(token);
        String nickname = jwtProvider.getNickName(token);

        // redis refresh token 과 비교
        if(Boolean.FALSE.equals(refreshTokenService.isValid(userId, token)))
            throw new CustomException(ErrorCode.INVALID_TOKEN);

        // access, refresh 재발행
        String accessToken = jwtProvider.createAccessToken(userId, nickname);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        // redis 에 저장
        refreshTokenService.save(
                userId,
                refreshToken,
                jwtProperties.getRefreshTokenExpiration()
        );

        return new JwtTokenResponse(accessToken, refreshToken);
    }
}
