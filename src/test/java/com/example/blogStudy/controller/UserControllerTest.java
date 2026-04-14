package com.example.blogStudy.controller;

import com.example.blogStudy.config.TestWebConfig;
import com.example.blogStudy.dto.create.UserCreate;
import com.example.blogStudy.dto.response.UserResponse;
import com.example.blogStudy.dto.update.PasswordUpdate;
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
    void getUsers() throws Exception {
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
    void getUserById() throws Exception {
        // given
        String id = "user1234";
        String name = "유저1";
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
    @DisplayName("유저 계정 생성")
    void createUser() throws Exception {
        // given
        String id = "user1234";
        String password = "testPassword1";
        String name = "유저1";

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
    }


    @Test
    @WithCustomMockUser
    @DisplayName("유저 비밀번호 수정")
    void updatePassword() throws Exception {
        String currentPassword = "testPassword1";
        String newPassword = "newPassword1";
        PasswordUpdate dto = new PasswordUpdate(currentPassword, newPassword);

        ResultActions result = mockMvc.perform(patch("/users/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)));

        result
                .andExpect(status().isNoContent());

        verify(userService).updatePassword(eq("user1234"), any(PasswordUpdate.class));
    }
//
//    @Test
//    void updateName() {
//    }
//
//    @Test
//    void deleteUser() {
//    }
}