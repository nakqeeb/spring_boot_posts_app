package com.nakqeeb.posts_app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddCommentDto {
    @NotNull(message = "Content is required")
    @Size(min = 10, message = "Content must be at least 10 characters long")
    private String content;
}
