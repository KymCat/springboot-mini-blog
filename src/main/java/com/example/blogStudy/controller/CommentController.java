package com.example.blogStudy.controller;

import com.example.blogStudy.dto.response.CommentResponse;
import com.example.blogStudy.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 전체 조회
    @GetMapping("/comments")
    public ResponseEntity<List<CommentResponse>> getComments() {
        return ResponseEntity.ok(commentService.getComments());
    }
}
