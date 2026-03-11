package com.example.blogStudy.controller;

import com.example.blogStudy.dto.create.PostCreateDto;
import com.example.blogStudy.dto.response.PostResponseDto;
import com.example.blogStudy.dto.update.PostUpdateDto;
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
    public List<PostResponseDto> getPosts() {
        return postService.getPosts();
    }

    // 게시글 단일 조회
    @GetMapping("/post/{id}")
    public PostResponseDto getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    // 게시글 작성
    @PostMapping("/post")
    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostCreateDto dto) {
        PostResponseDto created = postService.createPost(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    // 게시글 수정
    @PatchMapping("/post/{id}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long id, @RequestBody PostUpdateDto dto) {
        PostResponseDto updated = postService.updatePost(id,dto);

        return ResponseEntity.ok(updated);
    }

    // 게시글 삭제
    @DeleteMapping("/post/{id}")
    public ResponseEntity<PostResponseDto> deletePost(@PathVariable Long id) {
        postService.deletePost(id);

        return ResponseEntity.noContent().build();
    }

}
