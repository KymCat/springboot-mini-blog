package com.example.blogStudy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class RedisTestController {
    private final StringRedisTemplate stringRedisTemplate;

    @GetMapping("/redis-test")
    public String redisTest() {
        stringRedisTemplate.opsForValue().set("test:key", "hello world",15, TimeUnit.SECONDS); // 15초 뒤 만료
        return stringRedisTemplate.opsForValue().get("test:key");
    }
}
