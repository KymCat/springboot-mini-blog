package com.example.blogStudy.controller;

import com.example.blogStudy.dto.response.UserResponse;
import com.example.blogStudy.security.JwtAuthenticationFilter;
import com.example.blogStudy.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import javax.xml.transform.Result;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
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
                new UserResponse("user1", "유저1"),
                new UserResponse("user2", "유저2")
        );

        given(userService.getUsers()).willReturn(users);

        // when
        ResultActions result = mockMvc.perform(get("/users"));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(users.size()))
                .andExpect(jsonPath("$[0].id").value("user1"))
                .andExpect(jsonPath("$[0].name").value("유저1"));
    }

    @Test
    @DisplayName("해당 id 유저 조회")
    void getUserById() throws Exception {
        // given
        String id = "user1";
        UserResponse user = new UserResponse(id, "유저1");

        given(userService.getUserById(id)).willReturn(user);

        // when
        ResultActions result = mockMvc.perform(get("/users/{id}", id));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("유저1"));


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