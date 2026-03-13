package com.example.blogStudy.controller;

import com.example.blogStudy.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // 해당 게시글 좋아요 여부
    @GetMapping("/posts/{postId}/likes")
    ResponseEntity<Boolean> likes(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.likes(postId));
    }

    // 게시글 좋아요 설정
    @PostMapping("/posts/{postId}/likes")
    ResponseEntity<Void> likePost(@PathVariable Long postId) {
        likeService.likePost(postId);

        return ResponseEntity.ok().build();
    }
}
