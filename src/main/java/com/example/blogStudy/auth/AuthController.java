package com.example.blogStudy.auth;

import com.example.blogStudy.dto.request.ReissueRequest;
import com.example.blogStudy.dto.request.LoginRequest;
import com.example.blogStudy.jwt.JwtTokenResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // 로그인
    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest dto) {
        JwtTokenResult result = authService.login(dto);

        // Refresh Token set Cookie
        ResponseCookie cookie = refreshTokenCookie(result);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(result.getAccessToken());
    }

    // 로그아웃
    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(Authentication auth, HttpServletRequest request) {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String accessToken = authHeader.substring(7);

        authService.logout(auth, accessToken);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    // 토큰 재발행
    @PostMapping("/auth/reissue")
    public ResponseEntity<String> reissue(@RequestBody ReissueRequest dto) {
        JwtTokenResult result = authService.reissue(dto.getRefreshToken());

        // Refresh Token set Cookie
        ResponseCookie cookie = refreshTokenCookie(result);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(result.getAccessToken());
    }

    // Refresh Token Cookie 설정
    private ResponseCookie refreshTokenCookie(JwtTokenResult result) {
        boolean isProduction = false;   // 로컬 개발, 운영 환경에서는 true 로 변경

        return ResponseCookie.from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(isProduction)
                .path("/")
                .maxAge(result.getRefreshTokenExpiration())
                .sameSite("Lax")
                .build();
    }
}
