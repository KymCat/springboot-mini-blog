package com.example.blogStudy.dto.response;

import com.example.blogStudy.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PostDetailResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userId;
    private String name;

    private int likeCount;
    private List<CommentResponse> comments;

    public static PostDetailResponse from(Post post, int likeCount, List<CommentResponse> comments) {
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getUser().getId(),
                post.getUser().getName(),
                likeCount,
                comments
        );
    }
}
