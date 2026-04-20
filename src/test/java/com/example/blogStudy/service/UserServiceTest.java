package com.example.blogStudy.service;

import com.example.blogStudy.dto.response.UserResponse;
import com.example.blogStudy.entity.User;
import com.example.blogStudy.exception.CustomException;
import com.example.blogStudy.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("유저 전체 조회 성공")
    void get_users_success() {
        // given
        User user1 = User.create("test1user", "password1", "유저1");
        User user2 = User.create("test2user", "password2", "유저2");
        List<UserResponse> expected = Stream.of(user1,user2)
                .map(UserResponse::from)
                .toList();

        given(userRepository.findAll()).willReturn(List.of(user1,user2));

        // when
        List<UserResponse> result = userService.getUsers();

        // then
        assertThat(result)
                .hasSize(2)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("해당 id 유저 조회 성공")
    void get_user_by_id_success() {
        // given
        String id = "test1user";
        User user = User.create(id, "password1", "유저1");
        UserResponse expected = UserResponse.from(user);

        given(userRepository.findById(id)).willReturn(Optional.of(user));

        // when
        UserResponse result = userService.getUserById(id);

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("해당 id 유저 조회 실패 - 존재하지 않는 id")
    void get_user_by_id_fail_id_not_found() {
        // given
        String id = "wrong1user";
        given(userRepository.findById(id)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserById(id))
                .isInstanceOf(CustomException.class);
    }
//
//    @Test
//    void createUser() {
//    }
//
//    @Test
//    void updatePassword() {
//    }
//
//    @Test
//    void updateName() {
//    }
//
//    @Test
//    void deleteUser() {
//    }
}