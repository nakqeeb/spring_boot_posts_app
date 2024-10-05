package com.nakqeeb.posts_app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePostDto {

    @NotNull(message = "Title is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @NotNull(message = "Content is required")
    @Size(min = 10, message = "Content must be at least 10 characters long")
    private String content;

}
