package com.nakqeeb.posts_app.controller;

import com.nakqeeb.posts_app.dto.ActivateUserDto;
import com.nakqeeb.posts_app.dto.ApprovePostDto;
import com.nakqeeb.posts_app.dto.UpdateRoleDto;
import com.nakqeeb.posts_app.entity.LoginCounter;
import com.nakqeeb.posts_app.entity.Post;
import com.nakqeeb.posts_app.entity.RoleEnum;
import com.nakqeeb.posts_app.entity.User;
import com.nakqeeb.posts_app.exception.ErrorMapper;
import com.nakqeeb.posts_app.exception.PostNotFoundException;
import com.nakqeeb.posts_app.response.EmbeddedPosts;
import com.nakqeeb.posts_app.response.PostsPageResponse;
import com.nakqeeb.posts_app.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Admin", description = "AdminController")
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminController {
    private final AdminService adminService;
    private final ErrorMapper errorMapper;

    public AdminController(AdminService adminService, ErrorMapper errorMapper) {
        this.adminService = adminService;
        this.errorMapper = errorMapper;
    }

//    @Operation(summary = "Get all posts")
    @Operation(
            description = "Get all posts by (Admin and SUPER_ADMIN)",
            summary = "This is a summary for findAllPosts endpoint",
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

    @GetMapping("posts")
    public ResponseEntity<?> findAllPosts() {
        try {
            return new ResponseEntity<>(adminService.findAllPosts(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(this.errorMapper.createErrorMap(e), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            description = "Get all user's posts by his/her id  by (Admin and SUPER_ADMIN)",
            summary = "This is a summary for findPostsByUserId endpoint"
    )
    @GetMapping("/posts/user/{id}")
    public ResponseEntity<?> findPostsByUserId(@Parameter(description = "ID of the user") @PathVariable String id, Pageable pageable) {
        try {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.unsorted()); // this to fix error "No property '[\"string\"]' found for type 'Post'" when using Swagger
            Page<Post> posts = adminService.findPostsByUserId(Long.parseLong(id), pageable);
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
            description = "Get all users by (Admin and SUPER_ADMIN)",
            summary = "This is a summary for findAllUsers endpoint"
    )
    @GetMapping("/users")
    public ResponseEntity<?> findAllUsers() {
        try {
            return new ResponseEntity<>(adminService.findAllUsers(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(this.errorMapper.createErrorMap(e), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            description = "Get single user by id by (Admin and SUPER_ADMIN)",
            summary = "This is a summary for findAllUsers endpoint"
    )
    @GetMapping("/users/{id}")
    public ResponseEntity<?> findUserById(@PathVariable String id) {
        User user = adminService.findUserById(Long.parseLong(id));
        try {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(this.errorMapper.createErrorMap(e), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            description = "Get single user by email by (Admin and SUPER_ADMIN)",
            summary = "This is a summary for findUserByEmail endpoint"
    )
    @GetMapping("/users/email")
    public ResponseEntity<?> findUserByEmail(@RequestParam(name = "email") String email) {
        User user = adminService.findUserByEmail(email);
        try {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(this.errorMapper.createErrorMap(e), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            description = "Activate user account by (Admin and SUPER_ADMIN)",
            summary = "This is a summary for activateUser endpoint"
    )
    @PutMapping("/users/activate/{id}")
    public ResponseEntity<?> activateUser(@PathVariable String id, @RequestBody @Valid ActivateUserDto activateUserDto) {

        try {
            this.adminService.activateUser(Long.parseLong(id), activateUserDto);
            // Prepare a response message with status
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User " + (activateUserDto.isActivated() ? "activated" : "deactivated") + " successfully");
            response.put("status", HttpStatus.OK.value());
            response.put("activated", activateUserDto.isActivated());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(this.errorMapper.createErrorMap(e.getMessage()), HttpStatus.NOT_FOUND);
        }

    }

    @Operation(
            description = "Update user's role by (Admin and SUPER_ADMIN)",
            summary = "This is a summary for updateUserRole endpoint"
    )
    @PutMapping("/users/role/{id}")
    public ResponseEntity<?> updateUserRole(@PathVariable String id, @RequestBody @Valid UpdateRoleDto updateRoleDto) {

        if (!isValidRole(updateRoleDto.getRole())) {
            throw new IllegalArgumentException("Invalid role. Must be USER, ADMIN, or SUPER_ADMIN");
        }
        try {
            this.adminService.updateUserRole(Long.parseLong(id), updateRoleDto);
            // Prepare a response message with status
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User role updated to " + updateRoleDto.getRole() + " successfully");
            response.put("status", HttpStatus.OK.value());
            response.put("role", updateRoleDto.getRole());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(this.errorMapper.createErrorMap(e.getMessage()), HttpStatus.NOT_FOUND);
        }

    }

    @Operation(
            description = "Approve post by (Admin and SUPER_ADMIN)",
            summary = "This is a summary for approvePost endpoint"
    )
    @PutMapping("/posts/approve/{id}")
    public ResponseEntity<?> approvePost(@PathVariable String id, @RequestBody @Valid ApprovePostDto approvePostDto) {

        try {
            this.adminService.approvePost(Long.parseLong(id), approvePostDto);
            // Prepare a response message with status
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Post " + (approvePostDto.isApproved() ? "approved" : "disapproved") + " successfully");
            response.put("status", HttpStatus.OK.value());
            response.put("activated", approvePostDto.isApproved());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(this.errorMapper.createErrorMap(e.getMessage()), HttpStatus.NOT_FOUND);
        }

    }

    public boolean isValidRole(String role) {
        try {
            // Attempt to convert the string to RoleEnum
            RoleEnum.valueOf(role);
            return true;  // It's a valid enum value
        } catch (IllegalArgumentException e) {
            // If an exception occurs, the value is not valid for RoleEnum
            return false;
        }
    }

    @Operation(
            description = "Get Login Counter info by date by (Admin and SUPER_ADMIN). Date format must be yyyy-mm-dd",
            summary = "This is a summary for getLoginCounterInfo endpoint. By default, it will fetch the information for today's date. Date format must be yyyy-mm-dd"
    )
    @GetMapping("/login-counter")
    public ResponseEntity<?> getLoginCounterInfo(@RequestParam(required = false) String date) {
        try {
            if (date == null) {
                LocalDate today = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                date = today.format(formatter);
            }
            LoginCounter loginCounter = adminService.getLoginCounterInfo(date);
            return new ResponseEntity<>(loginCounter, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(this.errorMapper.createErrorMap(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
            description = "Delete comment by commentId by (Admin and SUPER_ADMIN)",
            summary = "This is a summary for deleteComment endpoint"
    )
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable String commentId) throws PostNotFoundException {
        try {
            adminService.deleteComment(Long.parseLong(commentId));
            // Prepare a response message with status
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Comment deleted successfully");
            response.put("status", HttpStatus.OK.value());
            response.put("id", commentId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(this.errorMapper.createErrorMap(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
