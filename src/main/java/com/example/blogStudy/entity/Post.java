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
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false, name = "post_title")
    private String title;

    @Column(nullable = false, name = "post_content")
    private String content;

    @Column(nullable = false, name = "post_date")
    private LocalDateTime postDate;

    @Column(nullable = false, name = "post_edit_date")
    private LocalDateTime editDate;

    // FK
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public static Post create(String title, String content, User user) {
        Post post = new Post();
        post.title = title;
        post.content = content;
        post.postDate = LocalDateTime.now();
        post.editDate = LocalDateTime.now();
        post.user = user;
        return post;
    }
}

