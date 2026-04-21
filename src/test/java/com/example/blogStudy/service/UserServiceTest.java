package com.example.blogStudy.service;

import com.example.blogStudy.dto.create.UserCreate;
import com.example.blogStudy.dto.response.UserResponse;
import com.example.blogStudy.dto.update.NameUpdate;
import com.example.blogStudy.dto.update.PasswordUpdate;
import com.example.blogStudy.entity.User;
import com.example.blogStudy.exception.CustomException;
import com.example.blogStudy.exception.ErrorCode;
import com.example.blogStudy.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private final String USER_ID = "test1user";
    private final String PASSWORD = "password1";
    private final String NICKNAME = "name1";

    private final String USER_ID2 = "test2user";
    private final String PASSWORD2 = "password2";
    private final String NICKNAME2 = "name2";

    private final String WRONG_USER_ID = "wrong1user";
    private final String NEW_PASSWORD = "newPassword1";
    private final String NEW_NICKNAME = "newName1";
    private final String ENCODED_PASSWORD = "encodedPassword1";

    @Test
    @DisplayName("유저 전체 조회 성공")
    void get_users_success() {
        // given
        User user1 = User.create(USER_ID, PASSWORD, NICKNAME);
        User user2 = User.create(USER_ID2, PASSWORD2, NICKNAME2);
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
        User user = User.create(USER_ID, PASSWORD, NICKNAME);
        UserResponse expected = UserResponse.from(user);

        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));

        // when
        UserResponse result = userService.getUserById(USER_ID);

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("해당 id 유저 조회 실패 - 존재하지 않는 id")
    void get_user_by_id_fail_id_not_found() {
        // given
        given(userRepository.findById(WRONG_USER_ID)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserById(WRONG_USER_ID))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("유저 계정 생성 성공")
    void create_user_success() {
        // given
        UserCreate dto = new UserCreate(USER_ID, PASSWORD, NICKNAME);
        User saved = User.create(USER_ID, ENCODED_PASSWORD, NICKNAME);
        UserResponse expected = UserResponse.from(User.create(USER_ID, PASSWORD, NICKNAME));

        given(userRepository.existsById(USER_ID)).willReturn(false);
        given(passwordEncoder.encode(PASSWORD)).willReturn(ENCODED_PASSWORD);
        given(userRepository.save(any(User.class))).willReturn(saved);

        // when
        UserResponse result = userService.createUser(dto);

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expected);

        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("유저 계정 생성 실패 - 중복 계정")
    void create_user_fail_duplicate_user_id() {
        // given
        UserCreate dto = new UserCreate(USER_ID, PASSWORD, NICKNAME);

        given(userRepository.existsById(USER_ID)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.DUPLICATE_USER_ID.getMessage());

        then(passwordEncoder).should(never()).encode(PASSWORD);
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("현재 유저 비밀번호 수정 성공")
    void update_password_success() {
        // given
        PasswordUpdate dto = new PasswordUpdate(PASSWORD, NEW_PASSWORD);
        User user = User.create(USER_ID, PASSWORD, NICKNAME);

        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(PASSWORD, user.getPassword())).willReturn(true);
        given(passwordEncoder.encode(NEW_PASSWORD)).willReturn(ENCODED_PASSWORD);

        // when
        userService.updatePassword(USER_ID, dto);

        // then
        then(passwordEncoder).should().matches(PASSWORD, PASSWORD);
        then(passwordEncoder).should().encode(NEW_PASSWORD);
        assertThat(user.getPassword()).isEqualTo(ENCODED_PASSWORD);
    }

    @Test
    @DisplayName("현재 유저 비밀번호 수정 실패 - 기존, 새 패스워드 동일함")
    void update_password_fail_current_new_password_same() {
        // given
        PasswordUpdate dto = new PasswordUpdate(PASSWORD, PASSWORD); // 패스워드가 같음
        User user = User.create(USER_ID, PASSWORD, NICKNAME);

        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> userService.updatePassword(USER_ID, dto))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.SAME_AS_CURRENT_VALUE.getMessage());

        then(passwordEncoder).should(never()).matches(any(), any());
        then(passwordEncoder).should(never()).encode(any());
    }

    @Test
    @DisplayName("현재 유저 비밀번호 수정 실패 - 기존 패스워드 불일치")
    void update_password_fail_invalid_password() {
        // given
        PasswordUpdate dto = new PasswordUpdate(PASSWORD, NEW_PASSWORD);
        User user = User.create(USER_ID, PASSWORD2, NICKNAME);  // 패스워드 다름 PASSWORD != PASSWORD2

        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(PASSWORD, PASSWORD2)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.updatePassword(USER_ID, dto))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_PASSWORD.getMessage());

        then(passwordEncoder).should(never()).encode(any());
    }


    @Test
    @DisplayName("현재 유저 닉네임 수정 성공")
    void update_name_success() {
        // given
        NameUpdate dto = new NameUpdate(NEW_NICKNAME);
        User user = User.create(USER_ID, PASSWORD, NICKNAME);

        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));

        // when
        userService.updateName(USER_ID, dto);

        // then
        assertThat(user.getName()).isEqualTo(NEW_NICKNAME);
    }

    @Test
    @DisplayName("현재 유저 닉네임 수정 실패 - 존재하지 않는 id")
    void update_name_fail_id_not_found() {
        // given
        NameUpdate dto = new NameUpdate(NEW_NICKNAME);

        given(userRepository.findById(USER_ID)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateName(USER_ID, dto))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("현재 유저 닉네임 수정 실패 - 기존, 새 닉네임 동일함")
    void update_name_fail_duplicate_name() {
        // given
        NameUpdate dto = new NameUpdate(NICKNAME);
        User user = User.create(USER_ID, PASSWORD, NICKNAME);

        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> userService.updateName(USER_ID, dto))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.SAME_AS_CURRENT_VALUE.getMessage());
    }


    @Test
    @DisplayName("유저 계정 삭제 성공")
    void delete_user_success() {
        // given
        User user = User.create(USER_ID, PASSWORD, NICKNAME);

        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));

        // when
        userService.deleteUser(USER_ID);

        // then
        then(userRepository).should().delete(user);
    }

    @Test
    @DisplayName("유저 계정 삭제 실패 - 존재하지 않는 id")
    void delete_user_fail_id_not_found() {
        // given
        given(userRepository.findById(USER_ID)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.deleteUser(USER_ID))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());

        then(userRepository).should(never()).delete(any());
    }
}