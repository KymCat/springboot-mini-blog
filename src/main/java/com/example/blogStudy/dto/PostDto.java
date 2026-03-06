package com.example.blogStudy.dto;

import com.example.blogStudy.entity.Post;
import com.example.blogStudy.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime postDate;
    private LocalDateTime editDate;

    public static PostDto from(Post post) {
        return new PostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getPostDate(),
                post.getEditDate()
        );
    }

    public Post toEntity(User user) {
        return Post.create(this.id, this.title, this.content, user);
    }
}
