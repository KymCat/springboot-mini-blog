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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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


    // 댓글 전체 조회
    public Page<CommentResponse> getComments(Pageable pageable) {
        Page<Comment> comments = commentRepository.findAllWithUser(pageable);
        return comments.map(CommentResponse::from);

    }

    // 댓글 단일 조회
    public CommentResponse getComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        return CommentResponse.from(comment);
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

        comment.update(dto);
        return CommentResponse.from(comment);
    }

    // 댓글 삭제
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        commentRepository.delete(comment);
    }
}
