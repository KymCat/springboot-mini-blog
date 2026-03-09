package com.example.blogStudy.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createAt;

    @Column(nullable = false)
    private LocalDateTime updateAt;

    // FK
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public static Post create(String title, String content, User user) {
        Post post = new Post();
        post.title = title;
        post.content = content;
        post.createAt = LocalDateTime.now();
        post.updateAt = LocalDateTime.now();
        post.user = user;
        return post;
    }
}

