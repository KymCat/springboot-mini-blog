package com.example.blogStudy.controller;

import com.example.blogStudy.dto.create.CommentCreate;
import com.example.blogStudy.dto.response.CommentResponse;
import com.example.blogStudy.dto.update.CommentUpdate;
import com.example.blogStudy.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // id 해당 게시글 댓글 조회
    @GetMapping("/posts/{id}/comments")
    public PagedModel<CommentResponse> getComments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page)
    {
        return commentService.getComments(id,page);
    }


    // 댓글 작성
    @PostMapping("/posts/{postId}/comments")
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
