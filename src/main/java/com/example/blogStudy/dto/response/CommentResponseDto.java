package com.example.blogStudy.dto.response;

import com.example.blogStudy.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String content;
    private LocalDateTime commentDate;
    private String user_id;
    private String name;
    private Long post_id;

    public static CommentResponseDto from(Comment comment) {
        return new CommentResponseDto(
          comment.getId(),
          comment.getContent(),
          comment.getCreatedAt(),
          comment.getUser().getId(),
          comment.getUser().getName(),
          comment.getPost().getId()
        );
    }
}
