package com.example.blogStudy.controller;

import com.example.blogStudy.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/redis-test")
public class RedisTestController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/save")
    public String save() {
        refreshTokenService.save("kim123", "sample-refresh-token", 60000);
        return "saved";
    }

    @GetMapping("/get")
    public String get() {
        return refreshTokenService.findByUserId("kim123");
    }

    @GetMapping("/exists")
    public boolean exists() {
        return refreshTokenService.exists("kim123");
    }

    @DeleteMapping("/delete")
    public String delete() {
        refreshTokenService.delete("kim123");
        return "deleted";
    }
}
