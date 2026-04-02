package com.example.blogStudy.auth;

import com.example.blogStudy.dto.request.LoginRequest;
import com.example.blogStudy.jwt.JwtTokenResult;
import com.example.blogStudy.security.JwtAuthenticationFilter;
import jakarta.servlet.http.Cookie;
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
import static org.hamcrest.Matchers.containsString;

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
    @DisplayName("로그인 성공")
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
        ResultActions result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(loginRequest)));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(content().string("access-token-value"))
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        containsString("refreshToken=refresh-token-value")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        containsString("HttpOnly")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        containsString("SameSite=Lax")));

    }

    @Test
    @DisplayName("로그인 실패 : 아이디 검증 실패")
    void login_fail_valid_id() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest("", "password1234@");

        // when
        ResultActions result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(loginRequest)));

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("USER-004"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/auth/login"));
    }

    @Test
    @DisplayName("로그인 실패 : 비밀번호 검증 실패")
    void login_fail_valid_password() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest("user1234", "");

        // when
        ResultActions result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(loginRequest)));

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("USER-004"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/auth/login"));
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout_success() throws Exception {
        // given
        String refreshToken = "refresh-token-value";

        // when
        ResultActions result = mockMvc.perform(post("/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token-value")
                .cookie(new Cookie("refreshToken", refreshToken)));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        containsString("")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        containsString("Max-Age=0")));

        verify(authService).logout("access-token-value", refreshToken);
    }

    @Test
    @DisplayName("로그아웃 실패 : Authorization Header null")
    void logout_fail_header_null()  throws Exception {
        // given
        String refreshToken = "refresh-token-value";

        // when
        ResultActions result = mockMvc.perform(post("/auth/logout")
                .cookie(new Cookie("refreshToken", refreshToken)));

        // then
        result
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.code").value("AUTH-001"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/auth/logout"));
    }

    @Test
    @DisplayName("토큰 재발행 성공")
    void reissue_success() throws Exception {
        // given
        String accessToken = "access-token-value";
        String refreshToken = "refresh-token-value";
        JwtTokenResult jwtTokenResult = new JwtTokenResult(
                accessToken,
                refreshToken,
                600000L
        );

        given(authService.reissue(any(String.class))).willReturn(jwtTokenResult);

        // when
        ResultActions result = mockMvc.perform(post("/auth/reissue")
                .cookie(new Cookie("refreshToken", refreshToken)));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        containsString("refreshToken="+refreshToken)))
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        containsString("HttpOnly")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        containsString("SameSite=Lax")));
    }

    @Test
    @DisplayName("토큰 재발행 실패 : 쿠키 없음")
    void reissue_fail_refresh_token() throws Exception {
        //given
        // refresh-token 쿠키 없이 요청

        // when
        ResultActions result = mockMvc.perform(post("/auth/reissue"));

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("AUTH-003"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/auth/reissue"));
    }
}
