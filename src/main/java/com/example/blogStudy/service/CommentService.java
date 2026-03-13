package com.example.blogStudy.service;

import com.example.blogStudy.dto.create.CommentCreate;
import com.example.blogStudy.dto.response.CommentResponse;
import com.example.blogStudy.dto.update.CommentUpdate;
import com.example.blogStudy.entity.Comment;
import com.example.blogStudy.entity.Post;
import com.example.blogStudy.entity.User;
import com.example.blogStudy.exception.CustomException;
import com.example.blogStudy.exception.ErrorCode;
import com.example.blogStudy.repository.CommentRepository;
import com.example.blogStudy.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // 세션 (임시)
    private User session = User.create(
            "kim",
            "pw123",
            "김민수");


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


    // 댓글 작성
    @Transactional
    public CommentResponse createComment(Long postId, CommentCreate dto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Comment comment = dto.toEntity(session, post);
        commentRepository.save(comment);
        return CommentResponse.from(comment);
    }


    // 댓글 수정
    @Transactional
    public CommentResponse updateComment(Long id, CommentUpdate dto) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        comment.validateOwner(session.getId()); // 권한 확인
        comment.update(dto);
        return CommentResponse.from(comment);
    }


    // 댓글 삭제
    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        comment.validateOwner(session.getId());
        commentRepository.delete(comment);
    }
}
