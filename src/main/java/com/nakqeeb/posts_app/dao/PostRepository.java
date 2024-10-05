package com.nakqeeb.posts_app.dao;

import com.nakqeeb.posts_app.entity.Post;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

//@Tag(name = "PostRepository", description = "post-entity-controller")
//@Repository
//@RepositoryRestResource(exported=true, path="postss")
@RepositoryRestResource(exported = false)
public interface PostRepository extends JpaRepository<Post, Long> {

    // http://localhost:8005/posts/search/findByIdAndApproved?id=1
    // @RestResource(path = "findByIdAndApproved", rel = "findByIdAndApproved")
    // Optional<Post> findByIdAndApprovedTrue(Long id);
    // The above approach is working perfectly, but I will use service/controller approach to customize the error message if post not found exception


    // GET: http://localhost:8005/api/posts/search/findByUserId?userId=2
    // Page<Post> findByUserId(@RequestParam("user_id") Long userId, Pageable pageable);

    Page<Post> findByUserId(Long userId, Pageable pageable);

//    @Query("SELECT p FROM Post p WHERE p.user.id = :userId")
//    Page<Post> findByCurrentUserId(@Param("userId") Long userId, Pageable pageable);

    // GET: http://localhost:8005/posts/search/approved?page=0&size=5
    @RestResource(path = "approved", rel = "approved")
    Page<Post> findByApprovedTrue(Pageable pageable);

    @RestResource(path = "", rel = "approved")
    Optional<Post> findByIdAndApprovedTrue(Long id);

}
