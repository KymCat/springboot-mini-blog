package com.example.blogStudy.service;

import com.example.blogStudy.dto.response.PostResponse;
import com.example.blogStudy.entity.Post;
import com.example.blogStudy.entity.User;
import com.example.blogStudy.repository.LikeRepository;
import com.example.blogStudy.repository.PostRepository;
import com.example.blogStudy.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.web.PagedModel;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    // 1. 필드 (Mock, 상수)
    @Mock private PostRepository postRepository;
    @Mock private UserRepository userRepository;
    @Mock LikeRepository likeRepository;

    @InjectMocks
    private PostService postService;

    private static final String USER_ID = "test1user";
    private static final String PASSWORD = "password1";
    private static final String NICKNAME = "name1";
    private static final long ID = 1;
    private static final String TITLE = "title";
    private static final String CONTENT = "content";

    // 2. 테스트용 객체 생성 메서드
    private Pageable defaultPageable() {
        return PageRequest.of(
                0, 5, Sort.by("createdAt").descending());
    }
    private User defaultUser() {
        return User.create(USER_ID, PASSWORD, NICKNAME);
    }
    private Post defaultPost() {
        return Post.create(TITLE, CONTENT, defaultUser());
    }


    // 3. 테스트 코드
    @Test
    @DisplayName("게시글 전체 조회 성공")
    void get_posts_success() {
        // given
        Pageable pageable = defaultPageable();
        Post post = defaultPost();
        Page<Post> posts = new PageImpl<>(List.of(post));

        given(postRepository.findAllWithUser(pageable)).willReturn(posts);

        // when
        PagedModel<PostResponse> result =  postService.getPosts(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0))
                .usingRecursiveComparison()
                .isEqualTo(PostResponse.from(post));
    }
//
//    @Test
//    void getPost() {
//    }
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