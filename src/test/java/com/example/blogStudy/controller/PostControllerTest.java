package com.example.blogStudy.controller;

import com.example.blogStudy.config.TestWebConfig;
import com.example.blogStudy.dto.create.PostCreate;
import com.example.blogStudy.dto.response.PostDetailResponse;
import com.example.blogStudy.dto.response.PostResponse;
import com.example.blogStudy.dto.update.PostUpdate;
import com.example.blogStudy.entity.Post;
import com.example.blogStudy.entity.User;
import com.example.blogStudy.exception.CustomException;
import com.example.blogStudy.exception.ErrorCode;
import com.example.blogStudy.security.JwtAuthenticationFilter;
import com.example.blogStudy.service.PostService;
import com.example.blogStudy.support.security.WithCustomMockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.MediaType;
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

    // 게시글 객체 생성 메서드
    private Post createPost(String title, String content, User user) {
        return Post.create(title, content, user);
    }

    // 디폴트 게시글 작성용 객체
    private PostCreate defaultPostCreate() {
        return new PostCreate(TITLE,CONTENT);
    }

    // 디폴트 게시글 응답용 객체
    private PostResponse defaultPostResponse() {
        return PostResponse.from(defaultPost());
    }

    // 게시글 응답용 객체 생성 메서드
    private PostResponse createPostResponse(Post post) {
        return PostResponse.from(post);
    }

    // 디폴트 게시글 응답용 디테일 객체
    private PostDetailResponse defaultPostDetailResponse() {
        return PostDetailResponse.from(defaultPost(), LIKE_CNT);
    }

    // 디폴트 게시글 수정용 객체
    private PostUpdate defaultPostUpdate() {
        return new PostUpdate(TITLE + " updated", CONTENT + " updated");
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



    @Test
    @WithCustomMockUser
    @DisplayName("게시글 작성 성공")
    void create_post_success() throws Exception {
        // given
        PostCreate dto = defaultPostCreate();
        PostResponse created = defaultPostResponse();

        given(postService.createPost(eq(USER_ID), any(PostCreate.class))).willReturn(created);

        // when
        ResultActions result = mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)));

        // then
        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(TITLE))
                .andExpect(jsonPath("$.content").value(CONTENT))
                .andExpect(jsonPath("$.userId").value(USER_ID));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("게시글 작성 실패 - 존재하지 않는 userId")
    void create_post_fail_userId_not_found() throws Exception {
        // given
        PostCreate dto = defaultPostCreate();

        given(postService.createPost(eq(USER_ID), any(PostCreate.class)))
                .willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

        // when
        ResultActions result = mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)));

        // then
        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.path").value("/posts"));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("게시글 작성 실패 - 잘못된 제목 입력")
    void create_post_fail_title_not_valid() throws Exception {
        // given
        PostCreate dto = new PostCreate("", CONTENT);

        given(postService.createPost(eq(USER_ID), any(PostCreate.class)))
                .willThrow(new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        // when
        ResultActions result = mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)));

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/posts"));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("게시글 작성 실패 - 잘못된 내용 입력")
    void create_post_fail_content_not_valid() throws Exception {
        // given
        PostCreate dto = new PostCreate(TITLE, "");

        given(postService.createPost(eq(USER_ID), any(PostCreate.class)))
                .willThrow(new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        // when
        ResultActions result = mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)));

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/posts"));
    }


    @Test
    @WithCustomMockUser
    @DisplayName("게시글 수정 성공")
    void update_post_success() throws Exception {
        // given
        PostUpdate dto = defaultPostUpdate();
        Post post = createPost(
                TITLE + " updated",
                CONTENT + " updated",
                defaultUser()
        );
        PostResponse updated = createPostResponse(post);

        given(postService.updatePost(eq(USER_ID), eq(ID), any(PostUpdate.class)))
                .willReturn(updated);

        // when
        ResultActions result = mockMvc.perform(patch("/posts/{id}",ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(TITLE + " updated"))
                .andExpect(jsonPath("$.content").value(CONTENT + " updated"))
                .andExpect(jsonPath("$.userId").value(USER_ID));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("게시글 수정 실패 - 존재하지 않는 게시글 id")
    void update_post_fail_id_not_found() throws Exception {
        // given
        PostUpdate dto = defaultPostUpdate();;

        given(postService.updatePost(eq(USER_ID), eq(ID), any(PostUpdate.class)))
                .willThrow(new CustomException(ErrorCode.POST_NOT_FOUND));

        // when
        ResultActions result = mockMvc.perform(patch("/posts/{id}",ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)));

        // then
        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value(ErrorCode.POST_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.POST_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.path").value("/posts/" + ID));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("게시글 수정 실패 - 접속 userId 와 게시글 작성 userId 다름")
    void update_post_fail_userId_not_same() throws Exception {
        // given
        PostUpdate dto = defaultPostUpdate();

        given(postService.updatePost(eq(USER_ID), eq(ID), any(PostUpdate.class)))
                .willThrow(new CustomException(ErrorCode.POST_ACCESS_DENIED));

        // when
        ResultActions result = mockMvc.perform(patch("/posts/{id}",ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)));

        // then
        result
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.code").value(ErrorCode.POST_ACCESS_DENIED.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.POST_ACCESS_DENIED.getMessage()))
                .andExpect(jsonPath("$.path").value("/posts/" + ID));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("게시글 수정 실패 - 잘못된 제목 입력")
    void update_post_fail_title_not_valid() throws Exception {
        // given
        PostUpdate dto = new PostUpdate("", CONTENT + " updated");

        given(postService.updatePost(eq(USER_ID), eq(ID) ,any(PostUpdate.class)))
                .willThrow(new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        // when
        ResultActions result = mockMvc.perform(patch("/posts/{id}",ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)));

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/posts/" + ID));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("게시글 수정 실패 - 잘못된 내용 입력")
    void update_post_fail_content_not_valid() throws Exception {
        // given
        PostUpdate dto = new PostUpdate(TITLE + " updated", "");

        given(postService.updatePost(eq(USER_ID), eq(ID), any(PostUpdate.class)))
                .willThrow(new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        // when
        ResultActions result = mockMvc.perform(patch("/posts/{id}",ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)));

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/posts/" + ID));
    }


    @Test
    @WithCustomMockUser
    @DisplayName("게시글 삭제 성공")
    void delete_post_success() throws Exception {
        // when
        ResultActions result = mockMvc.perform(delete("/posts/{id}",ID));

        // then
        result
                .andExpect(status().isNoContent());
        then(postService).should().deletePost(USER_ID, ID);
    }

    @Test
    @WithCustomMockUser
    @DisplayName("게시글 삭제 실패 - 존재하지 않는 게시글 id")
    void delete_post_fail_id_not_found() throws Exception {
        // given
        willThrow(new CustomException(ErrorCode.POST_NOT_FOUND))
                .given(postService).deletePost(USER_ID, ID);

        // when
        ResultActions result = mockMvc.perform(delete("/posts/{id}",ID));

        // then
        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value(ErrorCode.POST_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.POST_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.path").value("/posts/" + ID));
    }
}