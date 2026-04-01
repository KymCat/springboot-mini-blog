package com.example.blogStudy.auth;

import com.example.blogStudy.dto.request.LoginRequest;
import com.example.blogStudy.jwt.JwtTokenResult;
import com.example.blogStudy.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE, // 지정된 타입과 해당 타입을 상속받는 하위 클래스까지
                classes = JwtAuthenticationFilter.class // Jwt 필터를 제외
        )
)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;


    @Test
    @DisplayName("로그인 성공 : access token 반환, refresh token cookie 설정")
    void login_success() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest("user1234", "password1234@");
        JwtTokenResult jwtTokenResult = new JwtTokenResult(
                "access-token-value",
                "refresh-token-value",
                600000L
        );

        given(authService.login(any(LoginRequest.class))).willReturn(jwtTokenResult);

        // when
        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(loginRequest)));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string("access-token-value"))
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        org.hamcrest.Matchers.containsString("refreshToken=refresh-token-value")));
    }

}
