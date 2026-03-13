package com.example.blogStudy.controller;

import com.example.blogStudy.dto.create.PostCreate;
import com.example.blogStudy.dto.response.CommentResponse;
import com.example.blogStudy.dto.response.PostDetailResponse;
import com.example.blogStudy.dto.response.PostResponse;
import com.example.blogStudy.dto.update.PostUpdate;
import com.example.blogStudy.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 전체 조회
    @GetMapping("/posts")
    public PagedModel<PostResponse> getPosts(
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable)
    {
        return postService.getPosts(pageable);
    }

    // 게시글 단일 조회
    @GetMapping("/posts/{id}")
    public PostDetailResponse getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    // id 해당 게시글 댓글 조회
    @GetMapping("/posts/{id}/comments")
    public PagedModel<CommentResponse> getComments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page)
    {
        return postService.getComments(id,page);
    }

    // 게시글 작성
    @PostMapping("/posts")
    public ResponseEntity<PostResponse> createPost(@RequestBody PostCreate dto) {
        PostResponse created = postService.createPost(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    // 게시글 수정
    @PatchMapping("/posts/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id, @RequestBody PostUpdate dto) {
        PostResponse updated = postService.updatePost(id,dto);

        return ResponseEntity.ok(updated);
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<PostResponse> deletePost(@PathVariable Long id) {
        postService.deletePost(id);

        return ResponseEntity.noContent().build();
    }

}
