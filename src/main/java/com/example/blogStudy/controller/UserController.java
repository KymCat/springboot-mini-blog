package com.example.blogStudy.controller;

import com.example.blogStudy.dto.create.UserCreateDto;
import com.example.blogStudy.dto.response.UserResponseDto;
import com.example.blogStudy.dto.update.UserUpdateDto;
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
    public List<UserResponseDto> getUsers() {
        return userService.getUsers();
    }

    // 해당 id 유저 조회
    @GetMapping("/users/{id}")
    public UserResponseDto getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    // 유저 계정 생성
    @PostMapping("/users")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserCreateDto dto) {
        UserResponseDto created = userService.createUser(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    // 유저 정보 수정 (비밀번호, 닉네임)
    @PatchMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable String id, @RequestBody UserUpdateDto dto) {
        UserResponseDto updated = userService.updateUser(id, dto);

        return ResponseEntity.ok(updated);
    }

    // 유저 계정 삭제
    @DeleteMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}
