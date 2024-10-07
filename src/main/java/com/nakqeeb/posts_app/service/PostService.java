package com.nakqeeb.posts_app.service;

import com.nakqeeb.posts_app.dao.CommentRepository;
import com.nakqeeb.posts_app.dao.PostRepository;
import com.nakqeeb.posts_app.dto.AddCommentDto;
import com.nakqeeb.posts_app.dto.CreatePostDto;
import com.nakqeeb.posts_app.dto.UpdatePostDto;
import com.nakqeeb.posts_app.entity.Comment;
import com.nakqeeb.posts_app.entity.Post;
import com.nakqeeb.posts_app.entity.User;
import com.nakqeeb.posts_app.exception.PostNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;

    @Autowired
    public PostService(PostRepository postRepository, UserService userService, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.commentRepository = commentRepository;
    }

    public Post createPost(CreatePostDto createPostDto, String userEmail) throws Exception {
        Optional<User> user = userService.findUserByEmail(userEmail);

        if(user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        } else if (!user.get().isActivated()) {
            throw new Exception("Your account is deactivated");
        }
        Post post = new Post();
        post.setTitle(createPostDto.getTitle());
        post.setContent(createPostDto.getContent());
        post.setUser(user.get());

        return postRepository.save(post);
    }

    public Page<Post> findCurrentUserPosts(String userEmail, Pageable pageable) throws Exception {
        Optional<User> user = userService.findUserByEmail(userEmail);

        if(user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        } else if (!user.get().isActivated()) {
            throw new Exception("Your account is deactivated");
        }

        return postRepository.findByUserId(user.get().getId(), pageable);
    }

    public Post findPostById(Long id) throws PostNotFoundException {
        Optional<Post> post = postRepository.findById(id);

        if (post.isEmpty()) {
            throw new PostNotFoundException("Post with id " + id + " does not exist");
        }

        return post.get();
    }

    public Page<Post> findApprovedPosts(Pageable pageable) {
        return postRepository.findByApprovedTrue(pageable);
    }

    public Post findApprovedPost(Long id) throws PostNotFoundException {
        Optional<Post> post = postRepository.findByIdAndApprovedTrue(id);

        if (post.isEmpty()) {
            throw new PostNotFoundException("Post with id " + id + " does not exist");
        }

        return post.get();
    }

    public Post updatePost(String userEmail, Long id, UpdatePostDto updatePostDto) throws Exception {
        Optional<User> user = userService.findUserByEmail(userEmail);

        if(user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        } else if (!user.get().isActivated()) {
            throw new Exception("Your account is deactivated");
        }

        Optional<Post> post = postRepository.findById(id);

        if (post.isEmpty()) {
            throw new PostNotFoundException("Post with id " + id + " does not exist");
        }

        post.get().setTitle(updatePostDto.getTitle());
        post.get().setContent(updatePostDto.getContent());

        return postRepository.save(post.get());
    }

    public void deletePost(String userEmail, Long id) throws Exception {
        Optional<User> user = userService.findUserByEmail(userEmail);

        if(user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        } else if (!user.get().isActivated()) {
            throw new Exception("Your account is deactivated");
        }

        Optional<Post> post = postRepository.findById(id);

        if (post.isEmpty()) {
            throw new PostNotFoundException("Post with id " + id + " does not exist");
        }

        postRepository.deleteById(id);
    }

    public Comment addComment(AddCommentDto addCommentDto, String userEmail, Long postId) throws Exception {
        Optional<User> user = userService.findUserByEmail(userEmail);

        if(user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        } else if (!user.get().isActivated()) {
            throw new Exception("Your account is deactivated");
        }

        // Find the post
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        // Create the comment
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user.get());
        comment.setContent(addCommentDto.getContent());

        // Save and return the comment
        return commentRepository.save(comment);
    }

    public List<Comment> getComments(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public Comment getCommentById(Long id) throws Exception {
        Optional<Comment> comment = commentRepository.findById(id);

        if (comment.isEmpty()) {
            throw new Exception("Comment with id " + id + " does not exist");
        }

        return comment.get();
    }

    public void deleteComment(String userEmail, Long commentId) throws Exception {
        Optional<User> user = userService.findUserByEmail(userEmail);
        if(user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        } else if (!user.get().isActivated()) {
            throw new Exception("Your account is deactivated");
        }

        Comment comment = getCommentById(commentId);

        if (!Objects.equals(comment.getUser().getId(), user.get().getId())) {
            throw new AccessDeniedException("Unauthorized");
        }

        commentRepository.deleteById(commentId);
    }
}
