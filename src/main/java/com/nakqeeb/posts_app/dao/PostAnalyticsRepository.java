package com.nakqeeb.posts_app.dao;

import com.nakqeeb.posts_app.entity.PostAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface PostAnalyticsRepository extends JpaRepository<PostAnalytics, Long> {
    Optional<PostAnalytics> findByPostId(Long postId);
}