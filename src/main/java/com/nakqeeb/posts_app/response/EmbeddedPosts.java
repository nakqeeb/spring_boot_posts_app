package com.nakqeeb.posts_app.response;

import com.nakqeeb.posts_app.entity.Post;
import lombok.Data;

import java.util.List;

@Data
public class EmbeddedPosts {
    private List<Post> posts;
}