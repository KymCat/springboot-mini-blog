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
@Table(
        name = "likes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "post_id"})
        }
)
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime createAt;

    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;


    @Builder
    public Like(User user, Post post) {
        this.createAt = LocalDateTime.now();
        this.user = user;
        this.post = post;
    }
}
