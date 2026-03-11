package com.example.blogStudy.dto.update;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostUpdate {
    private String title;
    private String content;

    // 게시글 수정 API
    // id는 URL Path 로 전달
    // body 에는 수정할 내용만 입력
    // Post 엔티티에 update() 메서드 작성 필요
}
