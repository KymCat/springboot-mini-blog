package com.example.blogStudy.controller;

import com.example.blogStudy.dto.response.PostResponseDto;
import com.example.blogStudy.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
