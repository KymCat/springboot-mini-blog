package com.example.blogStudy.service;

import com.example.blogStudy.dto.response.PostResponseDto;
import com.example.blogStudy.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    // 게시글 전체 조회
    public List<PostResponseDto> getPosts() {
        return postRepository.findAllWithUser().stream()
                .map(PostResponseDto::from)
                .toList();
    }

}
