package com.example.blogStudy.service;

import com.example.blogStudy.controller.PostController;
import com.example.blogStudy.dto.create.PostCreateDto;
import com.example.blogStudy.dto.response.PostResponseDto;
import com.example.blogStudy.dto.update.PostUpdateDto;
import com.example.blogStudy.entity.Post;
import com.example.blogStudy.entity.User;
import com.example.blogStudy.exception.CustomException;
import com.example.blogStudy.exception.ErrorCode;
import com.example.blogStudy.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    // 세션 (임시)
    private User session = User.create(
            "kim",
            "pw123",
            "김민수");

    // 게시글 전체 조회
    public List<PostResponseDto> getPosts() {
        return postRepository.findAllWithUser().stream()
                .map(PostResponseDto::from)
                .toList();
    }

    // 게시글 단일 조회
    public PostResponseDto getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        return PostResponseDto.from(post);
    }

    // 게시글 작성
    @Transactional
    public PostResponseDto createPost(PostCreateDto dto) {
        Post saved = postRepository.save(dto.toEntity(session));    // 실제 서비스에는 진짜 session 이 들어감
        return  PostResponseDto.from(saved);
    }

    // 게시글 수정
    @Transactional
    public PostResponseDto updatePost(Long id, PostUpdateDto dto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        post.update(dto);
        return PostResponseDto.from(post);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        postRepository.delete(post);
    }
}
