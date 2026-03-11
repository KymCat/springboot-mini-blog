package com.example.blogStudy.dto.create;

import com.example.blogStudy.entity.Comment;
import com.example.blogStudy.entity.Post;
import com.example.blogStudy.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentCreate {
    private String content;

    public Comment toEntity(User user, Post post) {
        return Comment.builder()
                .content(this.content)
                .user(user)
                .post(post)
                .build();
    }
}

