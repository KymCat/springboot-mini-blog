package com.example.blogStudy.dto.response;

import com.example.blogStudy.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime postDate;
    private LocalDateTime editDate;
    private String userId;
    private String UserName;

    // Entity -> DTO
    public static PostResponseDto from(Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getPostDate(),
                post.getEditDate(),
                post.getUser().getId(), // 엔티티 에서 필요한 정보만
                post.getUser().getName()
        );
    }

}

// 게시글 단건 및 리스트 조회 API
// User 객체는 넣지 않고 필요한 값만 꺼냄