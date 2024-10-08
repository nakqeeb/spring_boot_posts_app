package com.nakqeeb.posts_app.dao;

import com.nakqeeb.posts_app.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface LikeRepository extends JpaRepository<Like, Long> {

    // Check if a user already liked a post
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    // Count the number of likes for a post
    int countByPostId(Long postId);
}