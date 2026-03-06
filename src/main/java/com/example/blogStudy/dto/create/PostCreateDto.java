package com.example.blogStudy.dto.create;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostCreateDto {
    private String title;
    private String content;

    // 게시글 작성 API 에서 RequestBody 로 받음
    // user-id는 세션에서 입력됨
}
