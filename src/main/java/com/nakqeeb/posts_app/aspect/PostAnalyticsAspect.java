package com.nakqeeb.posts_app.aspect;

import com.nakqeeb.posts_app.dao.PostAnalyticsRepository;
import com.nakqeeb.posts_app.entity.Post;
import com.nakqeeb.posts_app.entity.PostAnalytics;
import com.nakqeeb.posts_app.exception.PostNotFoundException;
import com.nakqeeb.posts_app.service.PostService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
public class PostAnalyticsAspect {

    private final PostAnalyticsRepository postAnalyticsRepository;
    private final PostService postService;

    @Autowired
    public PostAnalyticsAspect(PostAnalyticsRepository postAnalyticsRepository, PostService postService) {
        this.postAnalyticsRepository = postAnalyticsRepository;
        this.postService = postService;
    }

    // Define a pointcut for the findApprovedPost method
    @Pointcut("execution(* com.nakqeeb.posts_app.controller.PostController.findApprovedPost(..))")
    public void findApprovedPostMethod() {
    }

    @AfterReturning(pointcut = "findApprovedPostMethod()", returning = "result")
    public void afterFindApprovedPost(JoinPoint joinPoint, Object result) throws PostNotFoundException {
        System.out.println("====> Executing afterFindApprovedPost method");
        // Access the method arguments (the postId in this case)
        Object[] args = joinPoint.getArgs();

        if (args != null && args.length > 0) {
            String postId = (String) args[0]; // Getting postId
            System.out.println("Post ID: " + postId);
            Optional<PostAnalytics> postAnalytics = postAnalyticsRepository.findByPostId(Long.parseLong(postId));
            if (postAnalytics.isPresent()) {
                postAnalytics.get().setViewsCount(postAnalytics.get().getViewsCount() + 1);
                postAnalyticsRepository.save(postAnalytics.get());
            } else {
                Post post = postService.findPostById(Long.parseLong(postId));
                PostAnalytics newPostAnalytics = new PostAnalytics();
                newPostAnalytics.setViewsCount(1);
                newPostAnalytics.setPost(post);
                postAnalyticsRepository.save(newPostAnalytics);
            }
        }

    }
}
