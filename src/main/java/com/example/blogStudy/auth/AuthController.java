package com.example.blogStudy.auth;

import com.example.blogStudy.dto.request.ReissueRequest;
import com.example.blogStudy.dto.request.UserRequest;
import com.example.blogStudy.jwt.JwtTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/auth/login")
    public ResponseEntity<JwtTokenResponse> login(@RequestBody UserRequest dto) {
        JwtTokenResponse result = authService.login(dto);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/auth/reissue")
    public ResponseEntity<JwtTokenResponse> reissue(@RequestBody ReissueRequest dto) {
        JwtTokenResponse result = authService.reissue(dto.getRefreshToken());

        return ResponseEntity.ok(result);
    }
}
