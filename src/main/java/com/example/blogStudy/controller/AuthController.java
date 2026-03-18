package com.example.blogStudy.controller;

import com.example.blogStudy.dto.request.ReissueRequest;
import com.example.blogStudy.dto.request.UserRequest;
import com.example.blogStudy.jwt.JwtTokenResponse;
import com.example.blogStudy.service.AuthService;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponse> login(@RequestBody UserRequest dto) {
        JwtTokenResponse result = authService.login(dto);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/reissue")
    public ResponseEntity<JwtTokenResponse> reissue(@RequestBody ReissueRequest dto) {
        JwtTokenResponse result = authService.reissue(dto.getRefreshToken());

        return ResponseEntity.ok(result);
    }
}
