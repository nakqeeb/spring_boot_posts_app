package com.nakqeeb.posts_app.response;

import com.nakqeeb.posts_app.entity.Post;
import lombok.Data;

import java.util.List;

@Data
public class PostsPageResponse {
    private EmbeddedPosts _embedded;
    private int size;
    private int totalPages;
    private long totalElements;
    private int number;
    private boolean last;
}