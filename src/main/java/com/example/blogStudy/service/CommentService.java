package com.example.blogStudy.service;

import com.example.blogStudy.dto.response.CommentResponse;
import com.example.blogStudy.entity.Comment;
import com.example.blogStudy.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    // 댓글 전체 조회
    public List<CommentResponse> getComments() {
        List<Comment> comments = commentRepository.findAllWithUser();
        return comments.stream()
                .map(CommentResponse::from)
                .toList();
    }
}
