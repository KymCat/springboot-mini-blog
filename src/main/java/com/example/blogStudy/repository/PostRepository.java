package com.example.blogStudy.repository;

import com.example.blogStudy.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 게시글 전체 조회 N+1 fetch join 해결 방법
    @Query("SELECT p FROM Post p JOIN FETCH p.user")
    List<Post> findAllWithUser();

}
