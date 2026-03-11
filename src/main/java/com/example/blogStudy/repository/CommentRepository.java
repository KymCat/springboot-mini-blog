package com.example.blogStudy.repository;

import com.example.blogStudy.dto.response.CommentResponse;
import com.example.blogStudy.entity.Comment;
import com.example.blogStudy.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
        @Query("SELECT c FROM Comment c JOIN FETCH c.user where c.post.id = :postId")
        List<Comment> findByPost(@Param("postId") Long postId);
}
