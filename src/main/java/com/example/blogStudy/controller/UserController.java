package com.example.blogStudy.controller;

import com.example.blogStudy.dto.create.UserCreate;
import com.example.blogStudy.dto.response.UserResponse;
import com.example.blogStudy.dto.update.UserUpdate;
import com.example.blogStudy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 유저 전체 조회
    @GetMapping("/users")
    public List<UserResponse> getUsers() {
        return userService.getUsers();
    }

    // 해당 id 유저 조회
    @GetMapping("/users/{id}")
    public UserResponse getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    // 유저 계정 생성
    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreate dto) {
        UserResponse created = userService.createUser(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    // 유저 정보 수정 (비밀번호, 닉네임)
    @PatchMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String id, @RequestBody UserUpdate dto) {
        UserResponse updated = userService.updateUser(id, dto);

        return ResponseEntity.ok(updated);
    }

    // 유저 계정 삭제
    @DeleteMapping("/users/{id}")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}
