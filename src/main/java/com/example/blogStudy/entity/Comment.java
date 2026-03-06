package com.example.blogStudy.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "comment_id")
    private Long id;

    @Column(nullable = false, name = "comment_content")
    private String content;

    @Column(nullable = false, name = "comment_date")
    private LocalDateTime commentDate;


    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Comment(String content, User user, Post post) {
        this.content = content;
        this.commentDate = LocalDateTime.now();
        this.user = user;
        this.post = post;
    }
}
