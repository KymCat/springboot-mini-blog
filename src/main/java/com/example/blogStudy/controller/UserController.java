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

    @GetMapping("/users")
    public List<UserResponseDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/users/{id}")
    public UserResponseDto getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserCreateDto dto) {
        UserResponseDto created = userService.createUser(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable String id, @RequestBody UserUpdateDto dto) {
        UserResponseDto updated = userService.updateUser(id, dto);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}
