package com.example.blogStudy.controller;

import com.example.blogStudy.dto.create.PostCreate;
import com.example.blogStudy.dto.response.PostDetailResponse;
import com.example.blogStudy.dto.response.PostResponse;
import com.example.blogStudy.dto.update.PostUpdate;
import com.example.blogStudy.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 전체 조회
    @GetMapping("/post")
    public List<PostResponse> getPosts() {
        return postService.getPosts();
    }

    // 게시글 단일 조회
    @GetMapping("/post/{id}")
    public PostDetailResponse getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    // 게시글 작성
    @PostMapping("/post")
    public ResponseEntity<PostResponse> createPost(@RequestBody PostCreate dto) {
        PostResponse created = postService.createPost(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    // 게시글 수정
    @PatchMapping("/post/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id, @RequestBody PostUpdate dto) {
        PostResponse updated = postService.updatePost(id,dto);

        return ResponseEntity.ok(updated);
    }

    // 게시글 삭제
    @DeleteMapping("/post/{id}")
    public ResponseEntity<PostResponse> deletePost(@PathVariable Long id) {
        postService.deletePost(id);

        return ResponseEntity.noContent().build();
    }

}
