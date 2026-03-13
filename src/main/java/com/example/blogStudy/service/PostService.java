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
import com.example.blogStudy.repository.LikeRepository;
import com.example.blogStudy.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    // 세션 (임시)
    private User session = User.create(
            "kim",
            "pw123",
            "김민수");

    // 게시글 전체 조회
    public PagedModel<PostResponse> getPosts(Pageable pageable) {
        return new PagedModel<>(
                postRepository.findAllWithUser(pageable)
                        .map(PostResponse::from));
    }

    // 게시글 단일 조회
    public PostDetailResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 좋아요 갯수 가져오기
        int likeCount = likeRepository.countByPostId(id);

        return PostDetailResponse.from(post, likeCount);
    }

    // id 해당 게시글 댓글 조회
    public PagedModel<CommentResponse> getComments(Long id, int page) {
        Pageable pageable = PageRequest.of(
                page,
                3,
                Sort.by("createdAt").descending());

        // Page => PagedModel : Page 타입보다 안정적인 구조인 PagedModel 반환 권장
        return new PagedModel<>(
                commentRepository.findByPostId(id,pageable)
                        .map(CommentResponse::from));
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
