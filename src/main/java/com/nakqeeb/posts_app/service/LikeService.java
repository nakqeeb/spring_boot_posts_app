package com.nakqeeb.posts_app.service;

import com.nakqeeb.posts_app.dao.LikeRepository;
import com.nakqeeb.posts_app.dao.PostAnalyticsRepository;
import com.nakqeeb.posts_app.dao.PostRepository;
import com.nakqeeb.posts_app.dao.UserRepository;
import com.nakqeeb.posts_app.entity.Like;
import com.nakqeeb.posts_app.entity.Post;
import com.nakqeeb.posts_app.entity.PostAnalytics;
import com.nakqeeb.posts_app.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostAnalyticsRepository postAnalyticsRepository;

    public void addLike(String userEmail, Long postId) {

        // Retrieve the user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Check if the user already liked the post
        if (likeRepository.existsByUserIdAndPostId(user.getId(), postId)) {
            throw new IllegalArgumentException("User has already liked this post.");
        }

        // Retrieve the post
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        // Create a new like
        Like like = new Like();
        like.setUser(user);
        like.setPost(post);

        // Save the like
        likeRepository.save(like);

        // Update the post analytics
        PostAnalytics analytics = post.getAnalytics();
        int newLikesCount = likeRepository.countByPostId(postId);
        analytics.updateLikesCount(newLikesCount);

        postAnalyticsRepository.save(analytics);
    }
}