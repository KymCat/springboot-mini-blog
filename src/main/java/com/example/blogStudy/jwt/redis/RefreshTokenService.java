package com.example.blogStudy.jwt.redis;

import com.example.blogStudy.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final String PREFIX = "refresh:";
    private final StringRedisTemplate stringRedisTemplate;

    private String createKey(String userId) {
        return PREFIX + userId;
    }

    // refresh 토큰 저장
    public void save(String userId, String refreshToken, long expireTime) {
        stringRedisTemplate.opsForValue().set(
                createKey(userId),  // key
                refreshToken,       // value
                expireTime,         // TTL
                TimeUnit.MILLISECONDS
                );
    }

    // 해당 유저의 refresh 토큰(value) 조회
    public String getRefreshToken(String userId) {
        return stringRedisTemplate.opsForValue().get(createKey(userId));
    }

    // 해당 유저의 refresh 토큰 존재 여부 확인
    public boolean exists(String userId) {
        Boolean hasKey = stringRedisTemplate.hasKey(createKey(userId));
        return Boolean.TRUE.equals(hasKey);
    }

    // 로그아웃할 때 삭제
    public void delete(String userId) {
        stringRedisTemplate.delete(createKey(userId));
    }

    // refresh 토큰 유효 검증
    public Boolean isValid(String userId, String refreshToken) {
        String savedToken = getRefreshToken(userId);
        return savedToken != null && refreshToken.equals(savedToken);
    }

}
