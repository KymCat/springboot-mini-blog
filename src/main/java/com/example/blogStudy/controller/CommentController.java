package com.example.blogStudy.controller;

import com.example.blogStudy.dto.create.CommentCreate;
import com.example.blogStudy.dto.response.CommentResponse;
import com.example.blogStudy.dto.update.CommentUpdate;
import com.example.blogStudy.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 전체 조회
    @GetMapping("/comments")
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(commentService.getComments(pageable));
    }

    // 댓글 단일 조회
    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getComment(id));
    }

    // 댓글 작성
    @PostMapping("/comments/{postId}")
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long postId, @RequestBody CommentCreate dto) {
        CommentResponse created = commentService.createComment(postId, dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    // 댓글 수정
    @PatchMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long id, @RequestBody CommentUpdate dto) {
        CommentResponse updated = commentService.updateComment(id, dto);

        return ResponseEntity.ok(updated);
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);

        return ResponseEntity.noContent().build();
    }
}
