package com.example.blogStudy.controller;

import com.example.blogStudy.config.TestWebConfig;
import com.example.blogStudy.dto.response.PostDetailResponse;
import com.example.blogStudy.dto.response.PostResponse;
import com.example.blogStudy.entity.Post;
import com.example.blogStudy.entity.User;
import com.example.blogStudy.exception.CustomException;
import com.example.blogStudy.exception.ErrorCode;
import com.example.blogStudy.security.JwtAuthenticationFilter;
import com.example.blogStudy.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = PostController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@Import(TestWebConfig.class)
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    private static final String USER_ID = "test1user";

    private static final Long ID = 1L;
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final int LIKE_CNT = 1;

    // 디폴트 유저 객체
    private User defaultUser() {
        return User.create(USER_ID, "password1", "name1");
    }

    // 디폴트 게시글 객체
    private Post defaultPost() {
        return Post.create(TITLE, CONTENT, defaultUser());
    }

    // 디폴트 게시글 응답용 객체
    private PostDetailResponse defaultPostDetailResponse() {
        return PostDetailResponse.from(defaultPost(), LIKE_CNT);
    }

    @Test
    @DisplayName("게시글 전체 조회 성공")
    void get_posts_success() throws Exception {
        // given
        PagedModel<PostResponse> posts = mock(PagedModel.class);
        given(postService.getPosts(any(Pageable.class))).willReturn(posts);

        // when
        ResultActions result = mockMvc.perform(get("/posts")
                .param("page", "0")
                .param("size", "5")
                .param("sort","createdAt,desc"));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 단일 조회 성공")
    void get_post_success() throws Exception {
        // given
        PostDetailResponse post = defaultPostDetailResponse();
        given(postService.getPost(ID)).willReturn(post);

        // when
        ResultActions result = mockMvc.perform(get("/posts/{id}", ID));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(TITLE))
                .andExpect(jsonPath("$.content").value(CONTENT))
                .andExpect(jsonPath("$.userId").value(USER_ID));
    }

    @Test
    @DisplayName("게시글 단일 조회 실패 - 존재하지 않는 게시글 id")
    void get_post_fail_id_not_found() throws Exception {
        // given
        given(postService.getPost(ID))
                .willThrow(new CustomException(ErrorCode.POST_NOT_FOUND));

        // when
        ResultActions result = mockMvc.perform(get("/posts/{id}", ID));

        // then
        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value(ErrorCode.POST_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.POST_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.path").value("/posts/" + ID));
    }


//
//    @Test
//    void createPost() {
//    }
//
//    @Test
//    void updatePost() {
//    }
//
//    @Test
//    void deletePost() {
//    }
}