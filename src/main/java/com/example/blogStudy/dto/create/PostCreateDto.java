package com.example.blogStudy.dto.create;

import com.example.blogStudy.entity.Post;
import com.example.blogStudy.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostCreateDto {
    private String title;
    private String content;

    // 게시글 작성 API 에서 RequestBody 로 받음
    // user-id는 세션에서 입력됨

    public Post toEntity(User user) {
        return Post.create(this.title, this.content, user);
    }
}
