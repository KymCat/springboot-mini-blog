package com.example.blogStudy.controller;

import com.example.blogStudy.security.CustomUserDetails;
import com.example.blogStudy.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    ResponseEntity<Boolean> likes(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @PathVariable Long postId) {

        String userId = userDetails.getUserId();
        return ResponseEntity.ok(likeService.likes(userId, postId));
    }

    // 게시글 좋아요 생성
    @PostMapping("/posts/{postId}/likes")
    ResponseEntity<Void> likePost(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @PathVariable Long postId) {

        String userId = userDetails.getUserId();
        likeService.likePost(userId, postId);

        return ResponseEntity.ok().build();
    }
}
