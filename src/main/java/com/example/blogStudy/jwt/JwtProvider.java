package com.example.blogStudy.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component  // Bean 등록 : 다른 클래스에서 @RequiredArgsConstructor 주입해서 사용가능
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private SecretKey key;

    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }


    // JwtProvider 객체 생성 -> JwtProvider 생성자 -> init() 자동실행 어노테이션
    @PostConstruct
    public void init() {

        // Base64 SecretKey Decode
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);   // HMAC SHA 키 생성(Jwt 서명 키)
    }


    // 사용자 Id를 받아서 Access Token 문자열을 만들어 반환
    public String createAccessToken(String userId, String userName) {
        Date now = new Date();  // 현재 시간
        Date expiration = new Date( // 현재 시간 + Access Token 만료 시간
                now.getTime() + jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .subject(userId)
                .claim("nickname", userName)
                .issuedAt(now)              // iat, 발급일자
                .expiration(expiration)     // exp, 만료일자
                .signWith(key)              // key 를 비밀키로 서명
                .compact();                 // 위 옵션 포함해서 JWT 문자열로 압축
    }


    // 사용자 Id를 받아서 Refresh Token 문자열을 만들어 반환
    public String createRefreshToken(String userId) {
        Date now = new Date();  // 현재 시간
        Date expiration = new Date( // 현재 시간 + Access Token 만료 시간
                now.getTime() + jwtProperties.getRefreshTokenExpiration());

        return Jwts.builder()
                .subject(userId)            // token 주인 표시
                .issuedAt(now)              // iat, 발급일자
                .expiration(expiration)     // exp, 만료일자
                .signWith(key)              // key 를 비밀키로 서명
                .compact();                 // 위 옵션 포함해서 JWT 문자열로 압축
    }

    // token 안에서 payload 반환
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // token 안에서 userId 반환
    public String getUserId(String token) {
        return getClaims(token).getSubject();
    }

    // token 안에서 nickname 반환
    public String getNickName(String token) {
        return getClaims(token).get("nickname", String.class);
    }

    // 토큰 검증
    public Boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
