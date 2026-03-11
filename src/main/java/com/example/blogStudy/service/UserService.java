package com.example.blogStudy.service;

import com.example.blogStudy.dto.create.UserCreate;
import com.example.blogStudy.dto.response.UserResponse;
import com.example.blogStudy.dto.update.UserUpdate;
import com.example.blogStudy.entity.User;
import com.example.blogStudy.exception.CustomException;
import com.example.blogStudy.exception.ErrorCode;
import com.example.blogStudy.repository.UserRepository;
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
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    // 해당 id 유저 조회
    @Transactional(readOnly = true)
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserResponse.from(user);
    }

    // 유저 계정 생성
    @Transactional
    public UserResponse createUser(UserCreate dto) {
        if(userRepository.existsById(dto.getId()))
            throw new CustomException(ErrorCode.DUPLICATE_USER_ID);

        User saved = userRepository.save(dto.toEntity());
        return UserResponse.from(saved);
    }

    // 유저 계정 수정
    @Transactional
    public UserResponse updateUser(String id, UserUpdate dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.update(dto);
        return UserResponse.from(user);
    }

    // 유저 계정 삭제
    @Transactional
    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);
    }
}
