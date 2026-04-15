package com.example.blogStudy.controller;

import com.example.blogStudy.config.TestWebConfig;
import com.example.blogStudy.dto.create.UserCreate;
import com.example.blogStudy.dto.response.UserResponse;
import com.example.blogStudy.dto.update.NameUpdate;
import com.example.blogStudy.dto.update.PasswordUpdate;
import com.example.blogStudy.exception.CustomException;
import com.example.blogStudy.exception.ErrorCode;
import com.example.blogStudy.security.JwtAuthenticationFilter;
import com.example.blogStudy.service.UserService;
import com.example.blogStudy.support.security.WithCustomMockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@Import(TestWebConfig.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;


    @Test
    @DisplayName("유저 전체 조회")
    void get_user_success() throws Exception {
        // given
        List<UserResponse> users = List.of(
                new UserResponse("user1234", "유저1"),
                new UserResponse("user2345", "유저2")
        );

        given(userService.getUsers()).willReturn(users);

        // when
        ResultActions result = mockMvc.perform(get("/users"));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(users.size()))
                .andExpect(jsonPath("$[0].id").value("user1234"))
                .andExpect(jsonPath("$[0].name").value("유저1"));
    }

    @Test
    @DisplayName("해당 id 유저 조회")
    void get_user_by_id_success() throws Exception {
        // given
        String id = "user1234";
        String name = "닉네임";
        UserResponse user = new UserResponse(id, name);

        given(userService.getUserById(id)).willReturn(user);

        // when
        ResultActions result = mockMvc.perform(get("/users/{id}", id));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name));


    }

    @Test
    @DisplayName("존재하지 않는 id 유저 조회")
    void get_user_by_id_fail_not_found() throws Exception {
        // given
        String id = "user12345";
        given(userService.getUserById(id))
                .willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

        // when
        ResultActions result = mockMvc.perform(get("/users/{id}", id));

        // then
        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.path").value("/users/user12345"));

        verify(userService).getUserById(argThat(arg -> arg.equals(id)));
    }

    @Test
    @DisplayName("유저 계정 생성")
    void create_user_success() throws Exception {
        // given
        String id = "user1234";
        String password = "testPassword1";
        String name = "닉네임";

        UserCreate dto = new UserCreate(id, password, name);
        UserResponse created = new UserResponse(id, name);

        given(userService.createUser(any(UserCreate.class))).willReturn(created);

        // when
        ResultActions result = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)));

        // then
        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name));

        verify(userService).createUser(argThat(user ->
                user.getId().equals(id) &&
                user.getPassword().equals(password) &&
                user.getName().equals(name)));
    }

    @Test
    @DisplayName("유저 계정 생성 실패 - id 검증 실패")
    void create_user_fail_not_valid_id() throws Exception {
        // given
        String id = "user";
        String password = "testPassword1";
        String name = "유저1";

        UserCreate dto = new UserCreate(id, password, name);

        // when
        ResultActions result = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)));

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/users"));

    }

    @Test
    @DisplayName("유저 계정 생성 실패 - password 검증 실패")
    void create_user_fail_not_valid_password() throws Exception {
        // given
        String id = "user12234";
        String password = null;
        String name = "닉네임";

        UserCreate dto = new UserCreate(id, password, name);

        // when
        ResultActions result = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)));

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/users"));

    }

    @Test
    @WithCustomMockUser
    @DisplayName("유저 비밀번호 수정")
    void update_password_success() throws Exception {
        // given
        String id = "user1234";
        String currentPassword = "testPassword1";
        String newPassword = "newPassword1";
        PasswordUpdate dto = new PasswordUpdate(currentPassword, newPassword);

        // when
        ResultActions result = mockMvc.perform(patch("/users/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)));

        // then
        result
                .andExpect(status().isNoContent());

        verify(userService).updatePassword(eq(id), argThat(arg ->
                arg.getCurrentPassword().equals(currentPassword) &&
                arg.getNewPassword().equals(newPassword)));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("유저 비밀번호 수정 실패 - 비밀번호 검증 실패")
    void update_password_fail_not_valid_password() throws Exception {
        // given
        String currentPassword = "current"; // 최소 문자 갯수 만족 X
        String newPassword = "n ew";         // 최소 문자 갯수 + 공백 문자 만족 X
        PasswordUpdate dto = new PasswordUpdate(currentPassword, newPassword);

        // when
        ResultActions result = mockMvc.perform(patch("/users/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)));

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/users/me/password"));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("유저 비밀번호 수정 실패 - 현재 비밀번호와 새 비밀번호가 동일")
    void update_password_fail_equal_current_new_password() throws Exception {
        // given
        String id = "user1234";
        String currentPassword = "testPassword1";
        String newPassword = "testPassword1";
        PasswordUpdate dto = new PasswordUpdate(currentPassword, newPassword);

        // void method 에 CustomException 발생시키기 => doThrow()
        doThrow(new CustomException(ErrorCode.SAME_AS_CURRENT_VALUE))
                .when(userService).updatePassword(eq(id), any(PasswordUpdate.class));

        // when
        ResultActions result = mockMvc.perform(patch("/users/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)));

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value(ErrorCode.SAME_AS_CURRENT_VALUE.getCode()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/users/me/password"));

        verify(userService).updatePassword(eq(id), argThat(arg ->
                arg.getCurrentPassword().equals(currentPassword) &&
                arg.getNewPassword().equals(newPassword)));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("유저 비밀번호 수정 실패 - 현재 비밀번호 틀림")
    void update_password_fail_not_valid_current_password() throws Exception {
        // given
        String id = "user1234";
        String currentPassword = "failPassword1";
        String newPassword = "newPassword1";
        PasswordUpdate dto = new PasswordUpdate(currentPassword, newPassword);

        // void method 에 CustomException 발생시키기 => doThrow()
        doThrow(new CustomException(ErrorCode.INVALID_PASSWORD))
                .when(userService).updatePassword(eq(id), any(PasswordUpdate.class));

        // when
        ResultActions result = mockMvc.perform(patch("/users/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)));

        // then
        result
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_PASSWORD.getCode()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/users/me/password"));

        verify(userService).updatePassword(eq(id), argThat(arg ->
                arg.getCurrentPassword().equals(currentPassword) &&
                        arg.getNewPassword().equals(newPassword)));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("유저 닉네임 수정")
    void update_name_success() throws Exception {
        // given
        String id = "user1234";
        String newName = "새닉네임";
        NameUpdate dto = new NameUpdate(newName);

        // when
        ResultActions result = mockMvc.perform(patch("/users/me/name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)));

        // then
        result
                .andExpect(status().isNoContent());

        verify(userService).updateName(eq(id), argThat(arg ->
                arg.getName().equals(newName)));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("유저 닉네임 수정 실패 - 기존 닉네임과 새 닉네임 동일")
    void update_name_fail_equal_current_new_name() throws Exception {
        // given
        String id = "user1234";
        String newName = "닉네임";
        NameUpdate dto = new NameUpdate(newName);

        // void method 에 CustomException 발생시키기 => doThrow()
        doThrow(new CustomException(ErrorCode.SAME_AS_CURRENT_VALUE))
                .when(userService).updateName(eq(id), any(NameUpdate.class));

        // when
        ResultActions result = mockMvc.perform(patch("/users/me/name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)));

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value(ErrorCode.SAME_AS_CURRENT_VALUE.getCode()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/users/me/name"));

        verify(userService).updateName(eq(id), argThat(arg ->
                arg.getName().equals(newName)));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("유저 정보 삭제")
    void deleteUser() throws Exception {
        // given
        String id = "user1234";

        // when
        ResultActions result = mockMvc.perform(delete("/users/me"));
        result
                .andExpect(status().isNoContent());

        // then
        verify(userService).deleteUser(eq(id));
    }
}