package com.example.blogStudy.dto.response;

import com.example.blogStudy.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private String user_id;
    private String name;
    private Long post_id;

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
          comment.getId(),
          comment.getContent(),
          comment.getCreatedAt(),
          comment.getUser().getId(),
          comment.getUser().getName(),
          comment.getPost().getId()
        );
    }
}
