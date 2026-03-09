package com.example.blogStudy.service;

import com.example.blogStudy.dto.create.UserCreateDto;
import com.example.blogStudy.dto.response.UserResponseDto;
import com.example.blogStudy.dto.update.UserUpdateDto;
import com.example.blogStudy.entity.User;
import com.example.blogStudy.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 유저 전체 조회
    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseDto::from)
                .toList();
    }

    // 해당 id 유저 조회
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found : " + id));

        return UserResponseDto.from(user);
    }

    // 유저 계정 생성
    @Transactional
    public UserResponseDto createUser(UserCreateDto dto) {
        if(userRepository.existsById(dto.getId()))
            throw new IllegalArgumentException("Exist ID");

        User saved = userRepository.save(dto.toEntity());
        return UserResponseDto.from(saved);
    }

    // 유저 계정 수정
    @Transactional
    public UserResponseDto updateUser(String id, UserUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found : " + id));

        user.update(dto);
        return UserResponseDto.from(user);
    }

    // 유저 계정 삭제
    @Transactional
    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found : " + id));

        userRepository.delete(user);
    }
}
