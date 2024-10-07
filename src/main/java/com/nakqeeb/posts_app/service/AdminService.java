package com.nakqeeb.posts_app.service;

import com.nakqeeb.posts_app.dao.*;
import com.nakqeeb.posts_app.dto.ActivateUserDto;
import com.nakqeeb.posts_app.dto.ApprovePostDto;
import com.nakqeeb.posts_app.dto.UpdateRoleDto;
import com.nakqeeb.posts_app.entity.*;
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
public class AdminService {
    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final RoleRepository roleRepository;

    private final LoginCounterRepository loginCounterRepository;

    private final CommentRepository commentRepository;

    @Autowired
    public AdminService(UserRepository userRepository, PostRepository postRepository, RoleRepository roleRepository, LoginCounterRepository loginCounterRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.roleRepository = roleRepository;
        this.loginCounterRepository = loginCounterRepository;
        this.commentRepository = commentRepository;
    }

    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }

    public Page<Post> findPostsByUserId(Long id, Pageable pageable) {
        return postRepository.findByUserId(id, pageable);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        return user.get();
    }

    public User findUserById(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        return user.get();
    }

    public void activateUser(Long id, ActivateUserDto activateUserDto) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty() || Objects.equals(user.get().getEmail(), "super.admin@email.com")) {
            throw new UsernameNotFoundException("User not found");
        }
        user.get().setActivated(activateUserDto.isActivated());

        userRepository.save(user.get());
    }

    public void approvePost(Long id, ApprovePostDto approvePostDto) throws PostNotFoundException {
        Optional<Post> post = postRepository.findById(id);

        if (post.isEmpty()) {
            throw new PostNotFoundException("Post with id " + id + " does not exist");
        }
        post.get().setApproved(approvePostDto.isApproved());

        postRepository.save(post.get());
    }

    public void updateUserRole(Long id, UpdateRoleDto updateRoleDto) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty() || Objects.equals(user.get().getEmail(), "super.admin@email.com")) {
            throw new UsernameNotFoundException("User not found");
        }

        Optional<Role> role = roleRepository.findByName(RoleEnum.valueOf(updateRoleDto.getRole()));

        if (role.isEmpty()) {
            throw new RuntimeException("Role not found");
        }

        user.get().setRole(role.get());

        userRepository.save(user.get());

    }

    public LoginCounter getLoginCounterInfo(String date) throws Exception {
        Optional<LoginCounter> loginCounter = loginCounterRepository.findByDate(date);
        if (loginCounter.isEmpty()) {
            throw new Exception("No logging information found");
        }
        return loginCounter.get();
    }

    public void deleteComment(Long commentId) throws Exception {
        Optional<Comment> comment = commentRepository.findById(commentId);

        if (comment.isEmpty()) {
            throw new Exception("Comment with id " + commentId + " does not exist");
        }

        commentRepository.deleteById(commentId);
    }
}
