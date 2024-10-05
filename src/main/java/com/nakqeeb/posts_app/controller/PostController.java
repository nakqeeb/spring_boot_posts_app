package com.nakqeeb.posts_app.controller;

import com.nakqeeb.posts_app.dto.CreatePostDto;
import com.nakqeeb.posts_app.dto.UpdatePostDto;
import com.nakqeeb.posts_app.entity.Post;
import com.nakqeeb.posts_app.exception.ErrorMapper;
import com.nakqeeb.posts_app.exception.PostNotFoundException;
import com.nakqeeb.posts_app.response.EmbeddedPosts;
import com.nakqeeb.posts_app.response.PostsPageResponse;
import com.nakqeeb.posts_app.service.JwtService;
import com.nakqeeb.posts_app.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "Posts", description = "PostController")
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final ErrorMapper errorMapper;
    private final JwtService jwtService;

    @Autowired
    public PostController(PostService postService, ErrorMapper errorMapper, JwtService jwtService) {
        this.postService = postService;
        this.errorMapper = errorMapper;
        this.jwtService = jwtService;
    }

    @Operation(
            description = "Create post by USER, Admin and SUPER_ADMIN",
            summary = "This is a summary for createPost endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> createPost(@RequestBody @Valid CreatePostDto createPostDto, HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt = authHeader.substring(7);

        try {
            String userEmail = jwtService.extractUsername(jwt);
            Post newPost = postService.createPost(createPostDto, userEmail);
            return new ResponseEntity<>(newPost, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(this.errorMapper.createErrorMap(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    // http://localhost:8005/api/posts/myPosts?page=0&size=5
    @Operation(
            description = "Get current logged in user's post by USER, Admin and SUPER_ADMIN",
            summary = "This is a summary for getCurrentUserPosts endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @GetMapping("/myPosts")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> getCurrentUserPosts(HttpServletRequest request, Pageable pageable) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt = authHeader.substring(7);

        try {
            String userEmail = jwtService.extractUsername(jwt);
            Page<Post> currentUserPosts = postService.findCurrentUserPosts(userEmail, pageable);
            return new ResponseEntity<>(currentUserPosts, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(this.errorMapper.createErrorMap(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            description = "Update user's post by himself/herself",
            summary = "This is a summary for updatePost endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> updatePost(@PathVariable String id, @RequestBody @Valid UpdatePostDto updatePostDto, HttpServletRequest request) throws PostNotFoundException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt = authHeader.substring(7);
        try {
            String userEmail = jwtService.extractUsername(jwt);
            Post currentPost = this.postService.findPostById(Long.parseLong(id));
            if (!Objects.equals(userEmail, currentPost.getUser().getEmail())) {
                throw new AccessDeniedException("Unauthorized");
            }
            Post updatedPost = postService.updatePost(userEmail, Long.parseLong(id), updatePostDto);
            // Prepare a response message with status
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Post updated successfully");
            response.put("status", HttpStatus.OK.value());
            response.put("post", updatedPost);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(this.errorMapper.createErrorMap(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            description = "Get all approved posts (Accessible by auth and non auth users)",
            summary = "This is a summary for findApprovedPosts endpoint"
    )
    @GetMapping("/approved")
    public ResponseEntity<?> findApprovedPosts(Pageable pageable) {
        try {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.unsorted()); // this to fix error "No property '[\"string\"]' found for type 'Post'" when using Swagger
            Page<Post> posts = postService.findApprovedPosts(pageable);
            EmbeddedPosts embeddedPosts = new EmbeddedPosts();
            PostsPageResponse response = new PostsPageResponse();
            embeddedPosts.setPosts(posts.getContent());
            response.set_embedded(embeddedPosts);
            response.setTotalPages(posts.getTotalPages());
            response.setSize(posts.getSize());
            response.setTotalElements(posts.getTotalElements());
            response.setNumber(response.getNumber());
            response.setLast(posts.isLast());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(this.errorMapper.createErrorMap(e), HttpStatus.BAD_REQUEST);
        }
    }


    @Operation(
            description = "Get approved post by its id (Accessible by auth and non auth users)",
            summary = "This is a summary for findApprovedPost endpoint"
    )
    @GetMapping("/approved/{id}")
    public ResponseEntity<?> findApprovedPost(@PathVariable String id) throws PostNotFoundException {
        Post post = postService.findApprovedPost(Long.parseLong(id));

        try {
            return new ResponseEntity<>(post, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(this.errorMapper.createErrorMap(e), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            description = "Delete user's post by himself/herself",
            summary = "This is a summary for deletePost endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable String id, HttpServletRequest request) throws PostNotFoundException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt = authHeader.substring(7);
        try {
            String userEmail = jwtService.extractUsername(jwt);
            Post currentPost = this.postService.findPostById(Long.parseLong(id));
            if (!Objects.equals(userEmail, currentPost.getUser().getEmail())) {
                throw new AccessDeniedException("Unauthorized");
            }
            postService.deletePost(userEmail, Long.parseLong(id));
            // Prepare a response message with status
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Post deleted successfully");
            response.put("status", HttpStatus.OK.value());
            response.put("id", id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(this.errorMapper.createErrorMap(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
