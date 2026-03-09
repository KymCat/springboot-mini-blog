package com.example.blogStudy.repository;

import com.example.blogStudy.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
