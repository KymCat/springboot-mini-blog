package com.example.blogStudy.service;

import com.example.blogStudy.dto.create.PostCreate;
import com.example.blogStudy.dto.response.CommentResponse;
import com.example.blogStudy.dto.response.PostDetailResponse;
import com.example.blogStudy.dto.response.PostResponse;
import com.example.blogStudy.dto.update.PostUpdate;
import com.example.blogStudy.entity.Post;
import com.example.blogStudy.entity.User;
import com.example.blogStudy.exception.CustomException;
import com.example.blogStudy.exception.ErrorCode;
import com.example.blogStudy.repository.CommentRepository;
import com.example.blogStudy.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // 세션 (임시)
    private User session = User.create(
            "kim",
            "pw123",
            "김민수");

    // 게시글 전체 조회
    public List<PostResponse> getPosts() {
        return postRepository.findAllWithUser().stream()
                .map(PostResponse::from)
                .toList();
    }

    // 게시글 단일 조회
    public PostDetailResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        List<CommentResponse> comments = commentRepository.findByPost(id).stream()
                .map(CommentResponse::from)
                .toList();

        return PostDetailResponse.from(post, comments);
    }

    // 게시글 작성
    @Transactional
    public PostResponse createPost(PostCreate dto) {
        Post saved = postRepository.save(dto.toEntity(session));    // 실제 서비스에는 진짜 session 이 들어감
        return  PostResponse.from(saved);
    }

    // 게시글 수정
    @Transactional
    public PostResponse updatePost(Long id, PostUpdate dto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        post.update(dto);
        return PostResponse.from(post);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        postRepository.delete(post);
    }
}
