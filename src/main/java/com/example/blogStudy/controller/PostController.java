package com.example.blogStudy.controller;

import com.example.blogStudy.dto.create.PostCreate;
import com.example.blogStudy.dto.response.PostDetailResponse;
import com.example.blogStudy.dto.response.PostResponse;
import com.example.blogStudy.dto.update.PostUpdate;
import com.example.blogStudy.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    // 게시글 작성
    @PostMapping("/posts")
    public ResponseEntity<PostResponse> createPost(Authentication auth,
                                                   @Valid @RequestBody PostCreate dto) {

        String userId = auth.getName();
        PostResponse created = postService.createPost(userId, dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    // 게시글 수정
    @PatchMapping("/posts/{id}")
    public ResponseEntity<PostResponse> updatePost(Authentication auth,
                                                   @PathVariable Long id,
                                                   @Valid @RequestBody PostUpdate dto) {
        String userId = auth.getName();
        PostResponse updated = postService.updatePost(userId, id, dto);

        return ResponseEntity.ok(updated);
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<PostResponse> deletePost(Authentication auth,
                                                   @PathVariable Long id) {
        String userId = auth.getName();
        postService.deletePost(userId, id);

        return ResponseEntity.noContent().build();
    }

}
